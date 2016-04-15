package org.illegaller.ratabb.hishoot2i.presenter;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivityView;
import org.illegaller.ratabb.hishoot2i.view.fragment.DownloadFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class LauncherActivityPresenter implements IPresenter<LauncherActivityView>,
        OnTabClickListener {
    private BottomBarBadge badgeBarInstalled, badgeBarFav;
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

    public void onSaveInstanceState(Bundle outState) {
        mBottomBar.onSaveInstanceState(outState);
    }

    public void attachBottomBar(CoordinatorLayout layout, View view, Bundle bundle) {
        final int[][] sResource = {
                {R.drawable.ic_book_black_24dp, R.string.installed},
                {R.drawable.ic_favorite_black_24dp, R.string.favorite},
                {R.drawable.ic_cloud_download_black_24dp, R.string.download}
        };
        final int count = sResource.length;
        BottomBarTab[] items = new BottomBarTab[count];
        for (int i = 0; i < count; i++) {
            Drawable icon = ResUtils.getVectorDrawable(mView.context(), sResource[i][0]);
            items[i] = new BottomBarTab(icon, sResource[i][1]);
        }
        mBottomBar = BottomBar.attachShy(layout, view, bundle);
        mBottomBar.setItems(items);
        mBottomBar.setOnTabClickListener(this);
        final int colorAccent = ContextCompat.getColor(this.mView.context(), R.color.colorAccent);
        badgeBarInstalled = mBottomBar.makeBadgeForTabAt(0, colorAccent, 0);
        badgeBarFav = mBottomBar.makeBadgeForTabAt(1, colorAccent, 0);
        badgeBarInstalled.hide();
        badgeBarFav.hide();
    }

    public void updateBottomBarBadge(int post, int count) {
        BottomBarBadge barBadge = null;
        if (post == 0) barBadge = badgeBarInstalled;
        else if (post == 1) barBadge = badgeBarFav;// barBadge Download?
        if (barBadge != null) {
            barBadge.setCount(count);
            if (count > 0) barBadge.show();
            else barBadge.hide();
        }
    }

    @Override public void onTabSelected(int position) {
        Fragment fragment;
        if (position == 2) fragment = DownloadFragment.newInstance();
        else if (position == 1) fragment = TemplateFragment.newInstance(true);
        else fragment = TemplateFragment.newInstance(false);
        this.mView.setFragment(fragment);
    }

    @Override public void onTabReSelected(int position) {//no-op
    }
}
