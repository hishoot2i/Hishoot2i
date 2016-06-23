package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import butterknife.BindView;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.events.EventProgress;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.events.EventUninstallTemplate;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.presenter.TemplateFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

public class TemplateFragment extends BaseFragment implements TemplateFragmentView {
  private static final String KEY_FAV_FRAGMENT = "fav";
  private static final int REQ_UNINSTALL_TEMPLATE = 0x05;
  private final List<Template> mTemplateList = new ArrayList<>();
  @BindView(R.id.templateRecyclerView) TemplateRecyclerView mRecyclerView;
  @BindView(R.id.noContent) View noContent;
  @Inject TemplateFragmentPresenter mPresenter;
  @InjectExtra(KEY_FAV_FRAGMENT) Boolean isFavFragment;
  @Inject @Named(IKeyNameTray.TEMPLATE_FAV) StringTray mTemplateFavTray;
  private String mIdPendingRemove;

  public TemplateFragment() {
  }

  public static TemplateFragment newInstance(boolean isFav) {
    Bundle args = new Bundle();
    args.putBoolean(KEY_FAV_FRAGMENT, isFav);
    TemplateFragment fragment = new TemplateFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    Dart.inject(this, getArguments());
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.findItem(R.id.action_search).setVisible(true);
    mPresenter.setupSearchView(getContext(), menu.findItem(R.id.action_search));
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override protected int layoutRes() {
    return R.layout.fragment_template;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mPresenter.attachView(this);
    mRecyclerView.mAdapter.setFavList(getFavList());
    mPresenter.performList(isFavFragment ? mTemplateFavTray.getValue() : TemplateManager.NO_FAV);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mRecyclerView.mAdapter != null) mRecyclerView.mAdapter.saveStates(outState);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (mRecyclerView.mAdapter != null) {
      mRecyclerView.mAdapter.restoreStates(savedInstanceState);
    }
  }

  @Override public void onDestroyView() {
    mPresenter.detachView();
    super.onDestroyView();
  }

  @Override public void showProgress(boolean isShow) {
    EventBus.getDefault().post(new EventProgress(isShow));
  }

  @Override public void setTemplateList(List<Template> list) {
    mTemplateList.clear();
    mTemplateList.addAll(list);
    updateView();
  }

  private void updateView() {
    if (mTemplateList.size() == 0) {
      noContent.setVisibility(View.VISIBLE);
      mRecyclerView.setVisibility(View.GONE);
    } else {
      mRecyclerView.mAdapter.animateTo(mTemplateList);
      noContent.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
    }
    updateBadgeBottomBar(mTemplateList.size());
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == REQ_UNINSTALL_TEMPLATE) {
      mTemplateList.remove(mPresenter.getTemplateById(mIdPendingRemove));
      updateView();
    }
  }

  @Subscribe public void onEvent(EventUninstallTemplate event) {
    mIdPendingRemove = event.templateId;
    startActivityForResult(Utils.getIntentUninstall(mIdPendingRemove), REQ_UNINSTALL_TEMPLATE);
  }

  @Subscribe public void onEvent(EventTemplateFav event) {
    if (event.isAdd) {
      addTemplateFav(event.templateId);
    } else {
      removeTemplateFav(event.templateId);
    }
  }

  private List<String> getFavList() {
    return Utils.stringToList(mTemplateFavTray.getValue());
  }

  private void updateBadgeBottomBar(int count) {
    EventBus.getDefault()
        .post(new EventBadgeBB(isFavFragment ? EventBadgeBB.Type.FAV : EventBadgeBB.Type.INSTALLED,
            count));
  }

  private void addTemplateFav(String templateId) {
    String value = null;
    if (Utils.isEmpty(mTemplateFavTray.getValue())) {
      value = templateId;
    } else {
      final List<String> list = getFavList();
      if (!list.contains(templateId) && list.add(templateId)) {
        value = Utils.listToString(list);
      }
    }
    if (value != null) mTemplateFavTray.setValue(value);
  }

  private void removeTemplateFav(String templateId) {
    final List<String> list = getFavList();
    if (!list.contains(templateId)) return;
    list.remove(templateId);
    mTemplateFavTray.setValue(Utils.listToString(list));
    if (isFavFragment) {
      mTemplateList.remove(mPresenter.getTemplateById(templateId));
      updateView();
    }
  }
}