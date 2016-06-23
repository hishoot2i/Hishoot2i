package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.model.template.Template;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateViewHolder> {
  private final List<Template> mTemplateList = new ArrayList<>();
  private final List<String> mFavList = new ArrayList<>();
  private final ViewBinderHelper mBinderHelper = new ViewBinderHelper();

  @Inject TemplateAdapter() {
  }

  void setFavList(List<String> mFavList) {
    this.mFavList.clear();
    this.mFavList.addAll(mFavList);
  }

  @Override public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return TemplateViewHolder.inflate(parent);
  }

  @Override public void onBindViewHolder(TemplateViewHolder holder, int position) {
    holder.onBind(new TemplateAndFavList(getItem(position), mFavList));
    mBinderHelper.bind(holder.swipeRevealLayout, getItem(position).id);
  }

  @Override public int getItemCount() {
    return mTemplateList.size();
  }

  void saveStates(Bundle bundle) {
    mBinderHelper.saveStates(bundle);
  }

  void restoreStates(Bundle bundle) {
    mBinderHelper.restoreStates(bundle);
  }

  void animateTo(List<Template> templates) {
    applyAndAnimateRemovals(templates);
    applyAndAnimateAdditions(templates);
    applyAndAnimateMovedItems(templates);
  }

  private Template getItem(int position) {
    return mTemplateList.get(position);
  }

  private Template removeItem(int position) {
    final Template template = mTemplateList.remove(position);
    notifyItemRemoved(position);
    return template;
  }

  private void applyAndAnimateRemovals(List<Template> newList) {
    for (int i = mTemplateList.size() - 1; i >= 0; i--) {
      final Template template = mTemplateList.get(i);
      if (!newList.contains(template)) removeItem(i);
    }
  }

  private void applyAndAnimateAdditions(List<Template> newList) {
    for (int i = 0; i < newList.size(); i++) {
      final Template template = newList.get(i);
      if (!mTemplateList.contains(template)) addItem(i, template);
    }
  }

  private void applyAndAnimateMovedItems(List<Template> newList) {
    for (int to = newList.size() - 1; to >= 0; to--) {
      final Template model = newList.get(to);
      final int from = mTemplateList.indexOf(model);
      if (from >= 0 && from != to) moveItem(from, to);
    }
  }

  private void addItem(int position, Template template) {
    mTemplateList.add(position, template);
    notifyItemInserted(position);
  }

  private void moveItem(int from, int to) {
    final Template template = mTemplateList.remove(from);
    mTemplateList.add(to, template);
    notifyItemMoved(from, to);
  }
}
