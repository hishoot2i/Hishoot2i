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
import org.illegaller.ratabb.hishoot2i.view.common.BaseAnimateViewHolder;

import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.displayPreview;
import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.stringFiles;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.openImageView;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.shareImage;

class HistoryViewHolder extends BaseAnimateViewHolder<String> {
  @BindView(R.id.ivHistory) ImageView mImageHistory;
  @BindView(R.id.swipe_layout) SwipeRevealLayout swipeRevealLayout;
  @BindView(R.id.layoutDelete) View layoutDelete;
  @BindView(R.id.layoutShare) View layoutShare;
  private String mPathImage;
  private boolean mIsOpen = false;

  private HistoryViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    swipeRevealLayout.setSwipeListener(new SimpleSwipeListener() {
      @Override public void onOpened(SwipeRevealLayout view) {
        mIsOpen = true;
      }

      @Override public void onClosed(SwipeRevealLayout view) {
        mIsOpen = false;
      }
    });
    mImageHistory.setOnClickListener(this::clickOpen);
    layoutShare.setOnClickListener(this::clickShare);
    layoutDelete.setOnClickListener(this::clickDelete);
  }

  static HistoryViewHolder inflate(ViewGroup parent) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
    return new HistoryViewHolder(view);
  }

  private void clickOpen(View view) {
    openImageView(getContext(), getUri());
  }

  private void clickShare(View view) {
    shareImage(getContext(), getUri());
  }

  private void clickDelete(View view) {
    EventBus.getDefault().post(new EventDeleteFile(mPathImage));
  }

  private File getFile() {
    return new File(mPathImage);
  }

  private Uri getUri() {
    return Uri.fromFile(getFile());
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  @Override protected void onBind(String path) {
    mPathImage = path;
    displayPreview(mImageHistory, stringFiles(getFile()));
  }
}
