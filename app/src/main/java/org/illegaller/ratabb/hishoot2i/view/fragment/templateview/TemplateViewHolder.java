package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.SwipeRevealLayout.SimpleSwipeListener;
import java.util.List;
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
import org.illegaller.ratabb.hishoot2i.view.common.BaseAnimateViewHolder;
import org.illegaller.ratabb.hishoot2i.view.widget.AlphaPatternDrawable;

public class TemplateViewHolder extends BaseAnimateViewHolder {
  @BindView(R.id.swipe_layout) SwipeRevealLayout swipeRevealLayout;
  @BindView(R.id.previewPrimary) ImageView previewPrimary;
  @BindView(R.id.previewSecondary) ImageView previewSecondary;
  @BindView(R.id.titlePrimary) TextView titlePrimary;
  @BindView(R.id.titleSecondary) TextView titleSecondary;
  @BindView(R.id.authorSecondary) TextView authorSecondary;
  @BindView(R.id.idSecondary) TextView idSecondary;
  @BindView(R.id.favIcon) AppCompatImageView favIcon;
  @BindView(R.id.layoutDelete) View layoutDelete;
  @BindView(R.id.layoutFav) View layoutFav;
  @BindView(R.id.layoutSecondary) View layoutSecondary;
  @BindView(R.id.layoutPrimary) View layoutPrimary;
  @BindDimen(R.dimen.rectangle_size) int recSize;

  private boolean mIsFav = false;
  private Template mTemplate;
  private boolean mIsOpen = false;
  private final SimpleSwipeListener mSwipeListener = new SimpleSwipeListener() {
    @Override public void onOpened(SwipeRevealLayout view) {
      mIsOpen = true;
    }

    @Override public void onClosed(SwipeRevealLayout view) {
      mIsOpen = false;
    }
  };

  public TemplateViewHolder(View view) {
    super(view);
    ButterKnife.bind(this, view);
    final AlphaPatternDrawable drawable = new AlphaPatternDrawable(recSize);
    previewPrimary.setBackground(drawable);
    previewSecondary.setBackground(drawable);
    swipeRevealLayout.setSwipeListener(mSwipeListener);

    layoutDelete.setOnClickListener(
        v -> EventBus.getDefault().post(new EventUninstallTemplate(mTemplate.id)));

    layoutFav.setOnClickListener(v -> {
      mIsFav = !mIsFav;
      updateFavIcon(getContext(), mIsFav);
      EventBus.getDefault().post(new EventTemplateFav(mTemplate.id, mIsFav));
    });

    layoutPrimary.setOnClickListener(v -> startMainActivity(getContext()));
    layoutSecondary.setOnClickListener(v -> startMainActivity(getContext()));
  }

  public static TemplateViewHolder inflate(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.row_template, parent, false);
    return new TemplateViewHolder(view);
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void onBind(Template template, List<String> favList) {
    this.mTemplate = template;
    titlePrimary.setText(template.name);
    titleSecondary.setText(template.name);
    authorSecondary.setText(template.author);
    idSecondary.setText(template.id);
    if (template.previewFile != null) {
      UILHelper.displayPreview(previewPrimary, template.previewFile);
      UILHelper.displayPreview(previewSecondary, template.previewFile);
    }
    mIsFav = favList.contains(template.id);
    updateFavIcon(getContext(), mIsFav);
    layoutDelete.setVisibility(isNotHtzOrDefaultTemplate() ? View.VISIBLE : View.GONE);
  }

  ///////////////////////////////////////////////
  private void startMainActivity(final Context context) {
    MainActivity.start(context, mTemplate);
  }

  private void updateFavIcon(final Context context, boolean isFav) {
    favIcon.setImageDrawable(ResUtils.getVectorDrawable(context,
        isFav ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
  }

  private boolean isNotHtzOrDefaultTemplate() {
    return mTemplate.type != TemplateType.HTZ && !mTemplate.id.equalsIgnoreCase(
        AppConstants.DEFAULT_TEMPLATE_ID);
  }
}
