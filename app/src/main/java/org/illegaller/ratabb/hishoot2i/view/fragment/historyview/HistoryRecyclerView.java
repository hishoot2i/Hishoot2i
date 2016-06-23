package org.illegaller.ratabb.hishoot2i.view.fragment.historyview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.common.BaseRecyclerView;
import rx.Subscription;

public class HistoryRecyclerView extends BaseRecyclerView {
  @Inject HistoryAdapter mAdapter;

  public HistoryRecyclerView(Context context) {
    this(context, null);
  }

  public HistoryRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HistoryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @NonNull @Override protected Subscription subscription() {
    return RxRecyclerView.scrollStateChanges(this)
        .subscribe(this::scrollStateChanges, CrashLog::logError);
  }

  private void scrollStateChanges(int newState) {
    if (newState != RecyclerView.SCROLL_STATE_DRAGGING) return;
    for (int i = 0, count = getChildCount(); i < count; i++) {
      final View view = getChildAt(i);
      final HistoryViewHolder vh = (HistoryViewHolder) getChildViewHolder(view);
      if (vh.isOpen()) vh.swipeRevealLayout.close(true);
    }
  }

  @NonNull @Override protected LayoutManager layoutManager() {
    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
    layoutManager.setOrientation(VERTICAL);
    return layoutManager;
  }

  @NonNull @Override protected Adapter adapter() {
    return mAdapter;
  }
}
