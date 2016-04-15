package org.illegaller.ratabb.hishoot2i.view.fragment;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerTemplateFragmentComponent;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateFragmentModule;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBottomBar;
import org.illegaller.ratabb.hishoot2i.events.EventProgressBar;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.presenter.TemplateFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.adapter.TemplateAdapter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.widget.ItemListTemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;


public class TemplateFragment extends BaseFragment implements TemplateFragmentView {
    private static final String KEY_FAV_FRAGMENT = "fav";
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Inject TemplateFragmentPresenter mPresenter;
    @InjectExtra(KEY_FAV_FRAGMENT) Boolean isFavFragment;
    private TrayManager mTrayManager;
    private List<Template> templateList = new ArrayList<>();
    private TemplateAdapter mAdapter;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
                final View view = recyclerView.getChildAt(i);
                final ItemListTemplate itemListTemplate = ButterKnife.findById(view, R.id.item_template);
                if (itemListTemplate.getExpand()) itemListTemplate.doExpand();
            }
        }
    };

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
        mPresenter.setSearchView(getActivity(), menu.findItem(R.id.action_search));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mAdapter = new TemplateAdapter(templateList, getFavList(), mTrayManager.getTemplateIdTray());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new FadeInAnimator(new OvershootInterpolator(1f)));
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(mAdapter));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mPresenter.performList();

    }

    @Override public void onDestroyView() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override protected void setupComponent(AppComponent component) {
        mTrayManager = HishootApplication.get(getActivity()).getComponent().trayManager();
        DaggerTemplateFragmentComponent.builder()
                .templateFragmentModule(new TemplateFragmentModule())
                .build().inject(this);
    }

    @Override public void showProgress() {
        EventBus.getDefault().post(new EventProgressBar(true));
    }

    @Override public void hideProgress() {
        EventBus.getDefault().post(new EventProgressBar(false));
    }

    @Override public void setTemplateList(List<Template> list) {
        templateList.clear();
        templateList.addAll(filterFavoriteList(list));
        updateView();
    }

    void updateView() {
        mAdapter.animateTo(templateList);
        mRecyclerView.scrollToPosition(0);
        updateBadgeBottomBar(templateList.size());
    }


    @Subscribe public void onEvent(EventTemplateFav e) {
        if (e.isAdd) addTemplateFav(e.templateId);
        else removeTemplateFav(e.templateId);
    }

    List<Template> filterFavoriteList(List<Template> list) {
        if (!isFavFragment) return list;
        List<Template> result = new ArrayList<>();
        final String idOnFav = mTrayManager.getTemplateFavTray().get();
        if (Utils.isEmpty(idOnFav)) return result;
        String[] arrayIdFav = Utils.stringToArray(idOnFav);
        for (String s : arrayIdFav)
            for (Template t : list) if (s.equalsIgnoreCase(t.id)) result.add(t);
        Collections.sort(result, TemplateProvider.TEMPLATE_NAME_COMPARATOR);
        return result;
    }

    List<String> getFavList() {
        return Utils.stringToList(mTrayManager.getTemplateFavTray().get());
    }

    void updateBadgeBottomBar(int count) {
        EventBus.getDefault().post(new EventBadgeBottomBar(count, isFavFragment ? 1 : 0));
    }

    void addTemplateFav(String templateId) {
        String value = null;
        if (Utils.isEmpty(mTrayManager.getTemplateFavTray().get())) value = templateId;
        else {
            List<String> list = getFavList();
            if (!list.contains(templateId))
                if (list.add(templateId)) value = Utils.listToString(list);
        }
        if (value != null) mTrayManager.getTemplateFavTray().set(value);
    }

    void removeTemplateFav(String templateId) {
        List<String> list = getFavList();
        if (!list.contains(templateId)) return;
        list.remove(templateId);
        mTrayManager.getTemplateFavTray().set(Utils.listToString(list));
        if (isFavFragment) {
            templateList.remove(mPresenter.getProvider().findById(templateId));
            updateView();
        }
    }

}