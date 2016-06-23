package org.illegaller.ratabb.hishoot2i.view.fragment.historyview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import butterknife.BindView;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.events.EventDeleteFile;
import org.illegaller.ratabb.hishoot2i.events.EventProgress;
import org.illegaller.ratabb.hishoot2i.presenter.HistoryFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

public class HistoryFragment extends BaseFragment implements HistoryFragmentView {

  @Inject HistoryFragmentPresenter presenter;
  @BindView(R.id.historyRecyclerView) HistoryRecyclerView mRecyclerView;
  @BindView(R.id.noContent) View noContent;

  public HistoryFragment() {
  }

  public static HistoryFragment newInstance() {
    Bundle args = new Bundle();
    HistoryFragment fragment = new HistoryFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected int layoutRes() {
    return R.layout.fragment_history;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mRecyclerView.mAdapter != null) mRecyclerView.mAdapter.saveStates(outState);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (mRecyclerView.mAdapter != null) mRecyclerView.mAdapter.restoreStates(savedInstanceState);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.findItem(R.id.action_search).setVisible(false);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public void onDestroyView() {
    presenter.detachView();
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    presenter.attachView(this);
    presenter.perform();
  }

  @Override public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Subscribe public void onEvent(EventDeleteFile event) {
    final File file = new File(event.path);
    new AlertDialog.Builder(getActivity()).setCancelable(true)
        .setTitle("Confirm Deletion")
        .setMessage("This action cannot be undone.\nAre you sure?")
        .setNegativeButton(android.R.string.cancel, null)
        .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int i) -> {
          if (file.delete()) presenter.perform();
        })
        .show();
  }

  ///////////////////HistoryFragmentView//////////////////////////
  @Override public void setList(List<String> list) {
    if (list.size() == 0) {
      noContent.setVisibility(View.VISIBLE);
      mRecyclerView.setVisibility(View.GONE);
    } else {
      mRecyclerView.mAdapter.setListPath(list);
      noContent.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
    }
    EventBus.getDefault().post(new EventBadgeBB(EventBadgeBB.Type.SAVED, list.size()));
  }

  @Override public void showProgress(boolean isShow) {
    EventBus.getDefault().post(new EventProgress(isShow));
  }
}
