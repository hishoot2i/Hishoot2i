package org.illegaller.ratabb.hishoot2i.view.adapter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventDeleteFile;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
  private List<String> paths = new ArrayList<>();
  private ViewBinderHelper binderHelper = new ViewBinderHelper();

  public HistoryAdapter() {
  }

  public void setPaths(List<String> list) {
    this.paths.clear();
    this.paths.addAll(list);
    notifyDataSetChanged();
  }

  public void saveStates(Bundle bundle) {
    binderHelper.saveStates(bundle);
  }

  public void restoreStates(Bundle bundle) {
    binderHelper.restoreStates(bundle);
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.row_history, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.bindView(getItem(position));
    binderHelper.bind(holder.swipeRevealLayout, getItem(position));
  }

  String getItem(int position) {
    return paths.get(position);
  }

  @Override public int getItemCount() {
    return paths.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivHistory) ImageView ivHistory;
    @BindView(R.id.swipe_layout) SwipeRevealLayout swipeRevealLayout;
    private String pathImage;
    private boolean isOpen = false;
    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

    public SwipeRevealLayout getSwipeRevealLayout() {
      return swipeRevealLayout;
    }

    public boolean isOpen() {
      return isOpen;
    }

    void bindView(String path) {
      pathImage = path;
      UILHelper.displayPreview(ivHistory, UILHelper.stringFiles(new File(pathImage)));
      swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
        @Override public void onClosed(SwipeRevealLayout view) {
          isOpen = false;
        }

        @Override public void onOpened(SwipeRevealLayout view) {
          isOpen = true;
        }
      });
    }

    @OnClick({ R.id.ivHistory, R.id.deleteLayout, R.id.shareLayout }) void onClick(View view) {
      final int id = view.getId();
      switch (id) {
        default:
        case R.id.ivHistory:
          Utils.openImageView(view.getContext(), Uri.fromFile(new File(pathImage)));
          break;
        case R.id.deleteLayout:
          EventBus.getDefault().post(new EventDeleteFile(pathImage));
          break;
        case R.id.shareLayout:
          Utils.shareImage(view.getContext(), Uri.parse(pathImage));
          break;
      }
    }
  }
}
