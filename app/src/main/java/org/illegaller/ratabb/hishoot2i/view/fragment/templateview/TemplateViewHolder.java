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
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.events.EventUninstallTemplate;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.common.BaseAnimateViewHolder;
import org.illegaller.ratabb.hishoot2i.view.widget.AlphaPatternDrawable;

import static org.illegaller.ratabb.hishoot2i.AppConstants.DEFAULT_TEMPLATE_ID;
import static org.illegaller.ratabb.hishoot2i.model.template.TemplateType.HTZ;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getVectorDrawable;
import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.displayPreview;

class TemplateViewHolder extends BaseAnimateViewHolder<TemplateAndFavList> {
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

  private TemplateViewHolder(View view) {
    super(view);
    ButterKnife.bind(this, view);
    final AlphaPatternDrawable drawable = new AlphaPatternDrawable(recSize);
    previewPrimary.setBackground(drawable);
    previewSecondary.setBackground(drawable);

    swipeRevealLayout.setSwipeListener(new SimpleSwipeListener() {
      @Override public void onOpened(SwipeRevealLayout view) {
        mIsOpen = true;
      }

      @Override public void onClosed(SwipeRevealLayout view) {
        mIsOpen = false;
      }
    });
    layoutDelete.setOnClickListener(this::clickDelete);
    layoutFav.setOnClickListener(this::clickFav);
    layoutPrimary.setOnClickListener(this::clickStartMain);
    layoutSecondary.setOnClickListener(this::clickStartMain);
  }

  static TemplateViewHolder inflate(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.row_template, parent, false);
    return new TemplateViewHolder(view);
  }

  private void clickDelete(View view) {
    EventBus.getDefault().post(new EventUninstallTemplate(mTemplate.id));
  }

  private void clickFav(View view) {
    mIsFav = !mIsFav;
    updateFavIcon(mIsFav);
    EventBus.getDefault().post(new EventTemplateFav(mTemplate.id, mIsFav));
  }

  private void clickStartMain(View view) {
    startMainActivity(getContext());
  }

  @Override protected void onBind(TemplateAndFavList templateAndFavList) {
    mTemplate = templateAndFavList.mTemplate;
    titlePrimary.setText(mTemplate.name);
    titleSecondary.setText(mTemplate.name);
    authorSecondary.setText(mTemplate.author);
    idSecondary.setText(mTemplate.id);
    if (mTemplate.previewFile != null) {
      displayPreview(previewPrimary, mTemplate.previewFile);
      displayPreview(previewSecondary, mTemplate.previewFile);
    }
    mIsFav = templateAndFavList.mFavList.contains(mTemplate.id);
    updateFavIcon(mIsFav);
    layoutDelete.setVisibility(isNotHtzOrDefaultTemplate() ? View.VISIBLE : View.GONE);
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  ///////////////////////////////////////////////
  private void startMainActivity(final Context context) {
    MainActivity.start(context, mTemplate);
  }

  private void updateFavIcon(boolean isFav) {
    favIcon.setImageDrawable(getVectorDrawable(getContext(),
        isFav ? R.drawable.ic_favorite_black_24dp : R.drawable.ic_favorite_border_black_24dp));
  }

  private boolean isNotHtzOrDefaultTemplate() {
    return mTemplate.type != HTZ && !mTemplate.id.equalsIgnoreCase(DEFAULT_TEMPLATE_ID);
  }
}
