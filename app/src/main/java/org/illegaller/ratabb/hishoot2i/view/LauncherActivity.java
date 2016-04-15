package org.illegaller.ratabb.hishoot2i.view;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerLauncherActivityComponent;
import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBottomBar;
import org.illegaller.ratabb.hishoot2i.events.EventProgressBar;
import org.illegaller.ratabb.hishoot2i.presenter.LauncherActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.PermissionHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.Bind;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LauncherActivity extends BaseActivity
        implements LauncherActivityView, PermissionHelper.Callback {
    @Bind(R.id.progress_bar) SmoothProgressBar mProgressBar;
    @Bind(R.id.search_view) MaterialSearchView mSearchView;
    @Bind(R.id.coordinator) CoordinatorLayout coordinator;
    @Bind(R.id.flContent) View content;
    @Inject LauncherActivityPresenter mPresenter;

    public static Intent getIntent(Context context) {
        return new Intent(context, LauncherActivity.class);
    }

    @Override protected void setupComponent(AppComponent component) {
          DaggerLauncherActivityComponent.builder()
                .launcherActivityModule(new LauncherActivityModule())
                .build()
                .inject(this);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.HishootTheme);
        super.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.attachBottomBar(coordinator, content, savedInstanceState);
        PermissionHelper.writeExternalStorage().with(this, this).build().runRequest();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().onResult(requestCode, permissions, grantResults);
    }

    @Override protected int getLayoutRes() {
        return R.layout.activity_launcher;
    }

    @Override protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    @Override protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override public void onBackPressed() {
        if (mSearchView.isSearchOpen()) mSearchView.closeSearch();
        else super.onBackPressed();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_about) {
            Utils.startAbout(this);
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.GONE);// TODO:??
    }

    @Subscribe public void onEvent(EventProgressBar e) {
        mProgressBar.setVisibility(e.isShow ? View.VISIBLE : View.GONE);
    }

    @Subscribe public void onEvent(EventBadgeBottomBar e) {
        mPresenter.updateBottomBarBadge(e.pos, e.count);
    }

    ///////////////// LauncherActivityView /////////////////
    @Override public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    @Override public Context context() {
        return this;
    }

    ///////////////// PermissionHelper.Callback /////////////////
    @Override public void allow() {
        CrashLog.log("permission granted");
    }

    @Override public void deny(String permission) {
        CrashLog.logError("permission deny", null);
    }
}
