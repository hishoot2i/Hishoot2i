package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB.Type;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivityView;
import org.illegaller.ratabb.hishoot2i.view.fragment.HistoryFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragment;

public class LauncherActivityPresenter
    implements IPresenter<LauncherActivityView>, OnTabClickListener {
  private BottomBarBadge badgeBarInstalled, badgeBarFav, badgeBarSaved;
  private BottomBar mBottomBar;
  private LauncherActivityView mView;

  @Override public void attachView(LauncherActivityView view) {
    this.mView = view;
  }

  @Override public void detachView() {
    this.badgeBarInstalled = null;
    this.badgeBarFav = null;
    this.mBottomBar = null;
    this.mView = null;
  }

  public void bottomBarSaveState(Bundle outState) {
    mBottomBar.onSaveInstanceState(outState);
  }

  public void attachBottomBar(Activity activity, Bundle bundle) {
    final int[][] sResource = {
        { R.drawable.ic_book_black_24dp, R.string.installed },
        { R.drawable.ic_favorite_black_24dp, R.string.favorite },
        { R.drawable.ic_folder_special_black_24dp, R.string.saved }
    };
    final int count = sResource.length;
    BottomBarTab[] items = new BottomBarTab[count];
    for (int i = 0; i < count; i++) {
      Drawable icon = ResUtils.getVectorDrawable(mView.context(), sResource[i][0]);
      items[i] = new BottomBarTab(icon, sResource[i][1]);
    }
    mBottomBar = BottomBar.attachShy(ButterKnife.findById(activity, R.id.coordinator),
        ButterKnife.findById(activity, R.id.flContent), bundle);
    mBottomBar.setItems(items);
    mBottomBar.setOnTabClickListener(this);
    int colorAccent = ContextCompat.getColor(this.mView.context(), R.color.colorAccent);
    badgeBarInstalled = mBottomBar.makeBadgeForTabAt(0, colorAccent, 0);
    badgeBarFav = mBottomBar.makeBadgeForTabAt(1, colorAccent, 0);
    badgeBarSaved = mBottomBar.makeBadgeForTabAt(2, colorAccent, 0);
    badgeBarInstalled.hide();
    badgeBarFav.hide();
    badgeBarSaved.hide();
  }

  public void bottomBarBadge(Type type, int count) {
    BottomBarBadge barBadge;
    switch (type) {
      default:
      case INSTALLED:
        barBadge = badgeBarInstalled;
        break;
      case FAV:
        barBadge = badgeBarFav;
        break;
      case SAVED:
        barBadge = badgeBarSaved;
        break;
    }

    if (barBadge != null) {
      barBadge.setCount(count);
      if (count > 0) {
        barBadge.show();
      } else {
        barBadge.hide();
      }
    }
  }

  @Override public void onTabSelected(int position) {
    Fragment fragment;
    switch (position) {
      default:
      case 0:
        fragment = TemplateFragment.newInstance(false);
        break;
      case 1:
        fragment = TemplateFragment.newInstance(true);/*fav fragment*/
        break;
      case 2:
        fragment = HistoryFragment.newInstance();
        break;
    }
    this.mView.setFragment(fragment);
  }

  @Override public void onTabReSelected(int position) {/*no-op*/ }
}
