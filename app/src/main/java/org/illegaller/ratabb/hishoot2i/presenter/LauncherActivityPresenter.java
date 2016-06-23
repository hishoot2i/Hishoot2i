package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivityView;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.historyview.HistoryFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragment;
import org.illegaller.ratabb.hishoot2i.view.rx.RxBottomBar;

public class LauncherActivityPresenter extends BasePresenter<LauncherActivityView> {

  @Inject LauncherActivityPresenter() {
  }

  public void attachBottomBar(Activity activity, Bundle bundle) {
    checkViewAttached();
    final int[][] sResource = {
        { R.drawable.ic_book_black_24dp, R.string.installed },
        { R.drawable.ic_favorite_black_24dp, R.string.favorite },
        { R.drawable.ic_folder_special_black_24dp, R.string.saved }
    };
    final int count = sResource.length;
    BottomBarTab[] items = new BottomBarTab[count];
    for (int i = 0; i < count; i++) {
      Drawable icon = ResUtils.getVectorDrawable(getMvpView().getContext(), sResource[i][0]);
      items[i] = new BottomBarTab(icon, sResource[i][1]);
    }
    final BottomBar bar = BottomBar.attachShy(ButterKnife.findById(activity, R.id.coordinator),
        ButterKnife.findById(activity, R.id.flContent), bundle);
    bar.setItems(items);
    addAutoUnSubscribe(RxBottomBar.tabSelected(bar).subscribe(this::tabSelected, this::onError));

    getMvpView().setBottomBar(bar);
  }

  private void onError(Throwable throwable) {
    CrashLog.logError("BottomBar tabSelected", throwable);
  }

  private void tabSelected(int position) {
    Fragment fragment;
    switch (position) {
      default:
      case 0:
        fragment = TemplateFragment.newInstance(false);
        break;
      case 1:
        fragment = TemplateFragment.newInstance(true);
        break;
      case 2:
        fragment = HistoryFragment.newInstance();
        break;
    }
    getMvpView().setFragment(fragment);
  }
}
