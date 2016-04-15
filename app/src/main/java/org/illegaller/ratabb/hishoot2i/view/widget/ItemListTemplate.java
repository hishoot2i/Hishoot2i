package org.illegaller.ratabb.hishoot2i.view.widget;

import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventTemplateFav;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static org.illegaller.ratabb.hishoot2i.utils.LayoutUtils.*;

public class ItemListTemplate extends ViewGroup {
    @Bind(R.id.imExpand) public CircleButton mExpandView;
    @Bind(R.id.imPreview) ImageView mPreview;
    @Bind(R.id.imUse) CircleButton mUseView;
    @Bind(R.id.imFav) CircleButton mFavView;
    @Bind(R.id.imInfo) CircleButton mInfoView;
    @Bind(R.id.txName) TextView mNameView;
    @Bind(R.id.txAuthor) TextView mAuthorView;
    @Bind(R.id.coverPreview) View mCoverPreview;
    private int mMaxHeight;
    private int mMinHeight;
    private int mRectangleSize;
    private String authorID;
    private Boolean isExpand = false;
    private Boolean isFav = false;
    private String templateId;

    public ItemListTemplate(Context context) {
        this(context, null);
    }

    public ItemListTemplate(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemListTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.merge_item_list_template, this, true);
        ButterKnife.bind(this, this);
        mMaxHeight = ResUtils.getDimensionPixelSize(context, R.dimen.item_template_max_height);
        mMinHeight = ResUtils.getDimensionPixelSize(context, R.dimen.item_template_min_height);
        mRectangleSize = ResUtils.getDimensionPixelSize(context, R.dimen.rectangle_size);
        authorID = getResources().getString(R.string.author_id);
        mCoverPreview.setBackground(new AlphaPatternDrawable(mRectangleSize));
    }

    @OnClick({R.id.imExpand, R.id.imFav, R.id.imInfo}) void onClick(View view) {
        if (view == mExpandView) doExpand();
        else if (view == mFavView) doFav();
        else if (view == mInfoView) doInfo();
    }


    public Boolean getExpand() {
        return isExpand;
    }

    public void doExpand() {
        isExpand = !isExpand;
        final int from = isExpand ? mMinHeight : mMaxHeight;
        final int to = isExpand ? mMaxHeight : mMinHeight;
        AnimUtils.height(mPreview, from, to);
        mPreview.setScaleType(isExpand ? FIT_CENTER : CENTER_CROP);
        mExpandView.setImageDrawable(isExpand ?
                ResUtils.getVectorDrawable(getContext(), R.drawable.ic_unfold_less_black_24dp)
                : ResUtils.getVectorDrawable(getContext(), R.drawable.ic_unfold_more_black_24dp));
    }

    public void bindTo(Template template, List<String> favList, String templateIdUse) {
        templateId = template.id;
        mNameView.setText(template.name);
        mAuthorView.setText(String.format(authorID, template.author, template.id));
        if (template.previewFile != null) UILHelper.displayPreview(mPreview, template.previewFile);
        mUseView.setVisibility(template.id.equalsIgnoreCase(templateIdUse) ? VISIBLE : INVISIBLE);
        isFav = favList.contains(templateId);
        mFavView.setAlpha(isFav ? 1f : 0.25f);
        mInfoView.setVisibility(templateApkNotDefault(template) ? VISIBLE : INVISIBLE);
    }

    void doFav() {
        isFav = !isFav;
        mFavView.setAlpha(isFav ? 1f : 0.25f);
        EventBus.getDefault().post(new EventTemplateFav(templateId, isFav));
    }

    void doInfo() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + templateId));
        getContext().startActivity(intent);
    }

    boolean templateApkNotDefault(Template template) {
        return template.type != TemplateType.HTZ
                && !template.id.equalsIgnoreCase(AppConstants.DEFAULT_TEMPLATE_ID);
    }

    ///////////////////////////////////////////////////////////////
    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int contentWidth = r - l - getPaddingRight();
        int contentRight = r - getPaddingRight();
        int contentTop = paddingTop;
        layoutView(mExpandView, contentRight - getWidthWithMargins(mExpandView), contentTop,
                mExpandView.getMeasuredWidth(), mExpandView.getMeasuredHeight());
        contentTop += getHeightWithMargins(mExpandView);
        layoutView(mFavView, contentRight - getWidthWithMargins(mFavView), contentTop,
                mFavView.getMeasuredWidth(), mFavView.getMeasuredHeight());
        contentRight -= getWidthWithMargins(mExpandView);
        layoutView(mUseView, contentRight - getWidthWithMargins(mUseView), paddingTop,
                mUseView.getMeasuredWidth(), mUseView.getMeasuredHeight());
        layoutView(mInfoView, contentRight - getWidthWithMargins(mInfoView), contentTop,
                mInfoView.getMeasuredWidth(), mInfoView.getMeasuredHeight());
        int widthName = contentWidth - getWidthWithMargins(mExpandView) - getWidthWithMargins(mUseView);
        layoutView(mNameView, paddingLeft, paddingTop, widthName, mNameView.getMeasuredHeight());
        int widthAuthor = contentWidth - (getWidthWithMargins(mInfoView) + getWidthWithMargins(mFavView));
        int topAuthor = paddingTop + getHeightWithMargins(mExpandView);
        layoutView(mAuthorView, paddingLeft, topAuthor, widthAuthor, mAuthorView.getMeasuredHeight());
        int heightUpContent = paddingTop + getHeightWithMargins(mExpandView) + getHeightWithMargins(mFavView);
        layoutView(mPreview, paddingLeft, heightUpContent, contentWidth, mMaxHeight);
        layoutView(mCoverPreview, paddingLeft, heightUpContent, contentWidth, mPreview.getHeight());
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthUsed = 0, heightUsed = 0;
        measureChildWithMargins(mCoverPreview, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        measureChildWithMargins(mUseView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        measureChildWithMargins(mInfoView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        measureChildWithMargins(mExpandView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        measureChildWithMargins(mFavView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        widthUsed += getMeasuredWidthWithMargins(mUseView) + getMeasuredWidthWithMargins(mExpandView);
        measureChildWithMargins(mNameView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(mExpandView);
        heightUsed += getMeasuredHeightWithMargins(mFavView);
        measureChildWithMargins(mAuthorView, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        measureChildWithMargins(mPreview, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
        heightUsed += getMeasuredHeightWithMargins(mPreview);
        int heightSize = heightUsed + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
}
