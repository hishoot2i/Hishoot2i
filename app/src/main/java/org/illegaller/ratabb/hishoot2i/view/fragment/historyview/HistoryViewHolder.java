package org.illegaller.ratabb.hishoot2i.view.fragment.historyview;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.SwipeRevealLayout.SimpleSwipeListener;
import java.io.File;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventDeleteFile;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseAnimateViewHolder;

public class HistoryViewHolder extends BaseAnimateViewHolder {
  @BindView(R.id.ivHistory) ImageView mImageHistory;
  @BindView(R.id.swipe_layout) SwipeRevealLayout swipeRevealLayout;
  @BindView(R.id.layoutDelete) View layoutDelete;
  @BindView(R.id.layoutShare) View layoutShare;
  private String mPathImage;
  private boolean mIsOpen = false;
  private final SimpleSwipeListener mSwipeListener = new SimpleSwipeListener() {
    @Override public void onClosed(SwipeRevealLayout view) {
      mIsOpen = false;
    }

    @Override public void onOpened(SwipeRevealLayout view) {
      mIsOpen = true;
    }
  };

  public HistoryViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    swipeRevealLayout.setSwipeListener(mSwipeListener);
    mImageHistory.setOnClickListener(
        v -> Utils.openImageView(getContext(), Uri.fromFile(new File(mPathImage))));
    layoutDelete.setOnClickListener(
        v -> EventBus.getDefault().post(new EventDeleteFile(mPathImage)));

    layoutShare.setOnClickListener(v -> Utils.shareImage(getContext(), Uri.parse(mPathImage)));
  }

  public static HistoryViewHolder inflate(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
    return new HistoryViewHolder(view);
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void onBind(String path) {
    mPathImage = path;
    UILHelper.displayPreview(mImageHistory, UILHelper.stringFiles(new File(mPathImage)));
  }
}
