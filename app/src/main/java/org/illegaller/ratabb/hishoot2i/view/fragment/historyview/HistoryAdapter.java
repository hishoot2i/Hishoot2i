package org.illegaller.ratabb.hishoot2i.view.fragment.historyview;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
  private List<String> mListPath = new ArrayList<>();
  private ViewBinderHelper mBinderHelper = new ViewBinderHelper();

  @Inject HistoryAdapter() {
  }

  void setListPath(List<String> list) {
    this.mListPath.clear();
    this.mListPath.addAll(list);
    notifyDataSetChanged();
  }

  void saveStates(Bundle bundle) {
    mBinderHelper.saveStates(bundle);
  }

  void restoreStates(Bundle bundle) {
    mBinderHelper.restoreStates(bundle);
  }

  @Override public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return HistoryViewHolder.inflate(parent);
  }

  @Override public void onBindViewHolder(HistoryViewHolder holder, int position) {
    holder.onBind(getItem(position));
    mBinderHelper.bind(holder.swipeRevealLayout, getItem(position));
  }

  private String getItem(int position) {
    return mListPath.get(position);
  }

  @Override public int getItemCount() {
    return mListPath.size();
  }
}
