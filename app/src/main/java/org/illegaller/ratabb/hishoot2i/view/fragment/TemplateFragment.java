package org.illegaller.ratabb.hishoot2i.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import butterknife.BindView;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateFragmentModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateModule;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.events.EventProgress;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.events.EventUninstallTemplate;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.presenter.TemplateFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.adapter.TemplateAdapter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

public class TemplateFragment extends BaseFragment implements TemplateFragmentView {
  private static final String KEY_FAV_FRAGMENT = "fav";
  private static final int REQ_UNINSTALL_TEMPLATE = 0x05;
  @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
  @BindView(R.id.noContent) View noContent;
  @Inject TemplateFragmentPresenter mPresenter;
  @InjectExtra(KEY_FAV_FRAGMENT) Boolean isFavFragment;
  @Inject @Named(IKeyNameTray.TEMPLATE_ID) StringTray templateIdTray;
  @Inject @Named(IKeyNameTray.TEMPLATE_FAV) StringTray templateFavTray;

  private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      if (newState != RecyclerView.SCROLL_STATE_DRAGGING) return;
      for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
        View childAt = recyclerView.getChildAt(i);
        TemplateAdapter.ViewHolder viewHolder =
            (TemplateAdapter.ViewHolder) recyclerView.getChildViewHolder(childAt);
        if (viewHolder.isOpen()) viewHolder.getSwipeRevealLayout().close(true);
      }
    }
  };
  private List<Template> templateList = new ArrayList<>();
  private TemplateAdapter mAdapter;
  private String IdPendingRemove;

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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.findItem(R.id.action_search).setVisible(true);
    mPresenter.setupSearchView(getActivity(), menu.findItem(R.id.action_search));
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override protected int layoutRes() {
    return R.layout.fragment_template;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mPresenter.attachView(this);
    mAdapter = new TemplateAdapter(templateList, getFavList());
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setItemAnimator(new FadeInAnimator(new OvershootInterpolator(1f)));
    mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mAdapter));
    mRecyclerView.addOnScrollListener(onScrollListener);
    mPresenter.performList(isFavFragment ? templateFavTray.get() : TemplateManager.NO_FAV);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mAdapter != null) mAdapter.saveStates(outState);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (mAdapter != null) mAdapter.restoreStates(savedInstanceState);
  }

  @Override public void onDestroyView() {
    mRecyclerView.removeOnScrollListener(onScrollListener);
    mPresenter.detachView();
    super.onDestroyView();
  }

  @Override protected void setupComponent(ApplicationComponent appComponent) {
    appComponent.plus(new TemplateModule()).plus(new TemplateFragmentModule()).inject(this);
  }

  @Override public void showProgress(boolean isShow) {
    EventBus.getDefault().post(new EventProgress(isShow));
  }

  @Override public void setTemplateList(List<Template> list) {
    templateList.clear();
    templateList.addAll(list);
    updateView();
  }

  void updateView() {
    if (templateList.size() == 0) {
      noContent.setVisibility(View.VISIBLE);
      mRecyclerView.setVisibility(View.GONE);
    } else {
      mAdapter.animateTo(templateList);
      mRecyclerView.scrollToPosition(0);
      noContent.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
    }
    updateBadgeBottomBar(templateList.size());
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == REQ_UNINSTALL_TEMPLATE) {
      templateList.remove(mPresenter.getTemplateById(IdPendingRemove));
      updateView();
    }
  }

  @Subscribe public void onEvent(EventUninstallTemplate event) {
    IdPendingRemove = event.templateId;
    startActivityForResult(Utils.getIntentUninstall(IdPendingRemove), REQ_UNINSTALL_TEMPLATE);
  }

  @Subscribe public void onEvent(EventTemplateFav event) {
    if (event.isAdd) {
      addTemplateFav(event.templateId);
    } else {
      removeTemplateFav(event.templateId);
    }
  }

  List<String> getFavList() {
    return Utils.stringToList(templateFavTray.get());
  }

  void updateBadgeBottomBar(int count) {
    EventBus.getDefault()
        .post(new EventBadgeBB(isFavFragment ? EventBadgeBB.Type.FAV : EventBadgeBB.Type.INSTALLED,
            count));
  }

  void addTemplateFav(String templateId) {
    String value = null;
    if (Utils.isEmpty(templateFavTray.get())) {
      value = templateId;
    } else {
      List<String> list = getFavList();
      if (!list.contains(templateId)) if (list.add(templateId)) value = Utils.listToString(list);
    }
    if (value != null) templateFavTray.set(value);
  }

  void removeTemplateFav(String templateId) {
    List<String> list = getFavList();
    if (!list.contains(templateId)) return;
    list.remove(templateId);
    templateFavTray.set(Utils.listToString(list));
    if (isFavFragment) {
      templateList.remove(mPresenter.getTemplateById(templateId));
      updateView();
    }
  }
}