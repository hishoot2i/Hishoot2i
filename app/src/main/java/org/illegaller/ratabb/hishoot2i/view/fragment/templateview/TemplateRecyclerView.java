package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.common.BaseRecyclerView;
import rx.Subscription;

public class TemplateRecyclerView extends BaseRecyclerView {
  @Inject TemplateAdapter mAdapter;

  public TemplateRecyclerView(Context context) {
    this(context, null);
  }

  public TemplateRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TemplateRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @NonNull @Override protected LayoutManager layoutManager() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(VERTICAL);
    return layoutManager;
  }

  @NonNull @Override protected Subscription subscription() {
    return RxRecyclerView.scrollStateChanges(this)
        .subscribe(this::scrollStateChanges, CrashLog::logError);
  }

  private void scrollStateChanges(int newState) {
    if (newState != RecyclerView.SCROLL_STATE_DRAGGING) return;
    for (int i = 0, count = getChildCount(); i < count; i++) {
      final View view = getChildAt(i);
      final TemplateViewHolder vh = (TemplateViewHolder) getChildViewHolder(view);
      if (vh.isOpen()) vh.swipeRevealLayout.close(true);
    }
  }

  @NonNull @Override protected Adapter adapter() {
    return mAdapter;
  }
}