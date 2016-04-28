package org.illegaller.ratabb.hishoot2i.view.adapter;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.events.EventUninstallTemplate;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.AlphaPatternDrawable;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {
  private final List<Template> templateList;
  private final List<String> favList;
  private final ViewBinderHelper binderHelper = new ViewBinderHelper();

  public TemplateAdapter(List<Template> templateList, List<String> favList) {
    this.templateList = new ArrayList<>(templateList);
    this.favList = new ArrayList<>(favList);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.row_template, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bindView(getItem(position));
    binderHelper.bind(holder.swipeRevealLayout, getItem(position).id);
  }

  @Override public int getItemCount() {
    return templateList.size();
  }

  public void saveStates(Bundle bundle) {
    binderHelper.saveStates(bundle);
  }

  public void restoreStates(Bundle bundle) {
    binderHelper.restoreStates(bundle);
  }

  public void animateTo(List<Template> templates) {
    applyAndAnimateRemovals(templates);
    applyAndAnimateAdditions(templates);
    applyAndAnimateMovedItems(templates);
  }

  Template getItem(int position) {
    return templateList.get(position);
  }

  Template removeItem(int position) {
    final Template template = templateList.remove(position);
    notifyItemRemoved(position);
    return template;
  }

  void applyAndAnimateRemovals(List<Template> newList) {
    for (int i = templateList.size() - 1; i >= 0; i--) {
      final Template template = templateList.get(i);
      if (!newList.contains(template)) removeItem(i);
    }
  }

  void applyAndAnimateAdditions(List<Template> newList) {
    for (int i = 0; i < newList.size(); i++) {
      final Template template = newList.get(i);
      if (!templateList.contains(template)) addItem(i, template);
    }
  }

  void applyAndAnimateMovedItems(List<Template> newList) {
    for (int to = newList.size() - 1; to >= 0; to--) {
      final Template model = newList.get(to);
      final int from = templateList.indexOf(model);
      if (from >= 0 && from != to) moveItem(from, to);
    }
  }

  void addItem(int position, Template template) {
    templateList.add(position, template);
    notifyItemInserted(position);
  }

  void moveItem(int from, int to) {
    final Template template = templateList.remove(from);
    templateList.add(to, template);
    notifyItemMoved(from, to);
  }

  ///////////////////////*ViewHolder*//////////////////////////////
  public class ViewHolder extends AnimateViewHolder {
    @BindView(R.id.swipe_layout) SwipeRevealLayout swipeRevealLayout;
    @BindView(R.id.previewPrimary) ImageView previewPrimary;
    @BindView(R.id.previewSecondary) ImageView previewSecondary;
    @BindView(R.id.titlePrimary) TextView titlePrimary;
    @BindView(R.id.titleSecondary) TextView titleSecondary;
    @BindView(R.id.authorSecondary) TextView authorSecondary;
    @BindView(R.id.idSecondary) TextView idSecondary;
    @BindView(R.id.favIcon) AppCompatImageView favIcon;
    @BindView(R.id.deleteLayout) View deleteLayout;
    private boolean isFav = false;
    private Template template;
    private boolean isOpen = false;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, itemView);
      int rectangleSize = ResUtils.getDimensionPixelSize(view.getContext(), R.dimen.rectangle_size);
      previewPrimary.setBackground(new AlphaPatternDrawable(rectangleSize));
      previewSecondary.setBackground(new AlphaPatternDrawable(rectangleSize));
      swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
        @Override public void onOpened(SwipeRevealLayout view) {
          isOpen = true;
        }

        @Override public void onClosed(SwipeRevealLayout view) {
          isOpen = false;
        }
      });
    }

    public SwipeRevealLayout getSwipeRevealLayout() {
      return swipeRevealLayout;
    }

    public boolean isOpen() {
      return isOpen;
    }

    void bindView(Template t) {
      template = t;
      titlePrimary.setText(template.name);
      titleSecondary.setText(template.name);
      authorSecondary.setText(template.author);
      idSecondary.setText(template.id);
      if (template.previewFile != null) {
        UILHelper.displayPreview(previewPrimary, template.previewFile);
        UILHelper.displayPreview(previewSecondary, template.previewFile);
      }
      isFav = favList.contains(template.id);
      favIcon.setImageDrawable(isFav ? ResUtils.getVectorDrawable(itemView.getContext(),
          R.drawable.ic_favorite_black_24dp) : ResUtils.getVectorDrawable(itemView.getContext(),
          R.drawable.ic_favorite_border_black_24dp));
      deleteLayout.setVisibility(templateApkNotDefault() ? View.VISIBLE : View.GONE);
    }

    @OnClick({ R.id.layoutPrimary, R.id.layoutSecondary }) void onClick(View view) {
      MainActivity.start(view.getContext(), template);
    }

    @OnClick(R.id.deleteLayout) void onClickDelete(View view) {
      // TODO: send to fragment to startForResultActivity ?

      EventBus.getDefault().post(new EventUninstallTemplate(template.id));
    }

    @OnClick(R.id.favLayout) void onClickFav(View view) {
      isFav = !isFav;
      favIcon.setImageDrawable(
          isFav ? ResUtils.getVectorDrawable(view.getContext(), R.drawable.ic_favorite_black_24dp)
              : ResUtils.getVectorDrawable(view.getContext(),
                  R.drawable.ic_favorite_border_black_24dp));
      EventBus.getDefault().post(new EventTemplateFav(template.id, isFav));
    }

    boolean templateApkNotDefault() {
      return template.type != TemplateType.HTZ && !template.id.equalsIgnoreCase(
          AppConstants.DEFAULT_TEMPLATE_ID);
    }

    @Override public void preAnimateRemoveImpl() {
      ViewCompat.setTranslationY(itemView, 1);
      ViewCompat.setAlpha(itemView, 1);
    }

    @Override public void preAnimateAddImpl() {
      ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
      ViewCompat.setAlpha(itemView, 0);
    }

    @Override public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
      ViewCompat.animate(itemView)
          .translationY(-itemView.getHeight() * 0.3f)
          .alpha(0)
          .setDuration(300)
          .setListener(listener)
          .start();
    }

    @Override public void animateAddImpl(ViewPropertyAnimatorListener listener) {
      ViewCompat.animate(itemView)
          .translationY(0)
          .alpha(1)
          .setDuration(300)
          .setListener(listener)
          .start();
    }
  }
}
