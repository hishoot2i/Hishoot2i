package org.illegaller.ratabb.hishoot2i.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.HistoryFragmentModule;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.events.EventDeleteFile;
import org.illegaller.ratabb.hishoot2i.events.EventProgress;
import org.illegaller.ratabb.hishoot2i.presenter.HistoryFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.view.adapter.HistoryAdapter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

public class HistoryFragment extends BaseFragment implements HistoryFragmentView {
  @Inject HistoryFragmentPresenter presenter;
  @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
  @BindView(R.id.noContent) View noContent;
  private HistoryAdapter adapter;
  private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      if (newState != RecyclerView.SCROLL_STATE_DRAGGING) return;
      for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
        View childAt = recyclerView.getChildAt(i);
        HistoryAdapter.ViewHolder viewHolder =
            (HistoryAdapter.ViewHolder) recyclerView.getChildViewHolder(childAt);
        if (viewHolder.isOpen()) viewHolder.getSwipeRevealLayout().close(true);
      }
    }
  };

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

  @Override protected void setupComponent(ApplicationComponent appComponent) {
    appComponent.plus(new HistoryFragmentModule()).inject(this);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (adapter != null) adapter.saveStates(outState);
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (adapter != null) adapter.restoreStates(savedInstanceState);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    menu.findItem(R.id.action_search).setVisible(false);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public void onDestroyView() {
    mRecyclerView.removeOnScrollListener(onScrollListener);
    presenter.detachView();
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    presenter.attachView(this);
    adapter = new HistoryAdapter();
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.addOnScrollListener(onScrollListener);
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
/*        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            boolean delete = file.delete();
            if (delete) presenter.perform();
          }
        })*/
        .setPositiveButton(android.R.string.ok, (DialogInterface dialog, int i) -> {
          boolean delete = file.delete();
          if (delete) presenter.perform();
        })
        .show();
  }

  ///////////////////HistoryFragmentView//////////////////////////
  @Override public void setList(List<String> list) {
    if (list.size() == 0) {
      noContent.setVisibility(View.VISIBLE);
      mRecyclerView.setVisibility(View.GONE);
    } else {
      adapter.setPaths(list);
      noContent.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
    }
    EventBus.getDefault().post(new EventBadgeBB(EventBadgeBB.Type.SAVED, list.size()));
  }

  @Override public void showProgress(boolean isShow) {
    EventBus.getDefault().post(new EventProgress(isShow));
  }
}
