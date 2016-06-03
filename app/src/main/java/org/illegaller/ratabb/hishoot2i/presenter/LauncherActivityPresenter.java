package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB.Type;
import org.illegaller.ratabb.hishoot2i.utils.BottomBarOnTabClickListener;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivityView;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.historyview.HistoryFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragment;

public class LauncherActivityPresenter extends BasePresenter<LauncherActivityView> {
  private BottomBarBadge mBadgeBarInstalled, mBadgeBarFav, mBadgeBarSaved;
  private BottomBar mBottomBar;

  @Inject public LauncherActivityPresenter() {
  }

  @Override public void detachView() {
    this.mBadgeBarInstalled = null;
    this.mBadgeBarFav = null;
    this.mBottomBar = null;
    super.detachView();
  }

  public void bottomBarSaveState(Bundle outState) {
    mBottomBar.onSaveInstanceState(outState);
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
      Drawable icon = ResUtils.getVectorDrawable(getView().getContext(), sResource[i][0]);
      items[i] = new BottomBarTab(icon, sResource[i][1]);
    }
    mBottomBar = BottomBar.attachShy(ButterKnife.findById(activity, R.id.coordinator),
        ButterKnife.findById(activity, R.id.flContent), bundle);
    mBottomBar.setItems(items);
    mBottomBar.setOnTabClickListener(new BottomBarOnTabClickListener() {
      @Override public void onTabSelected(int position) {
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
        getView().setFragment(fragment);
      }
    });
    int colorAccent = ContextCompat.getColor(getView().getContext(), R.color.colorAccent);
    mBadgeBarInstalled = mBottomBar.makeBadgeForTabAt(0, colorAccent, 0);
    mBadgeBarFav = mBottomBar.makeBadgeForTabAt(1, colorAccent, 0);
    mBadgeBarSaved = mBottomBar.makeBadgeForTabAt(2, colorAccent, 0);
    mBadgeBarInstalled.hide();
    mBadgeBarFav.hide();
    mBadgeBarSaved.hide();
  }

  @NonNull BottomBarBadge bottomBarBadge(Type type) {
    switch (type) {
      default:
      case INSTALLED:
        return mBadgeBarInstalled;
      case FAV:
        return mBadgeBarFav;
      case SAVED:
        return mBadgeBarSaved;
    }
  }

  public void bottomBarBadge(Type type, int count) {
    BottomBarBadge barBadge = bottomBarBadge(type);
    barBadge.setCount(count);
    if (count > 0) {
      barBadge.show();
    } else {
      barBadge.hide();
    }
  }
}
