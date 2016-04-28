package org.illegaller.ratabb.hishoot2i.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.events.EventProgress;
import org.illegaller.ratabb.hishoot2i.presenter.LauncherActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.PermissionHelper;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;

public class LauncherActivity extends BaseActivity
    implements LauncherActivityView, PermissionHelper.Callback {
  @BindView(R.id.progress_bar) View mProgressBar;
  @BindView(R.id.search_view) MaterialSearchView mSearchView;
  @BindView(R.id.coordinator) CoordinatorLayout coordinator;
  @BindView(R.id.flContent) View content;
  @Inject LauncherActivityPresenter mPresenter;

  public static Intent getIntent(Context context) {
    return new Intent(context, LauncherActivity.class);
  }

  @Override protected void setupComponent(ApplicationComponent component) {
    component.plus(new LauncherActivityModule()).inject(this);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    setTheme(R.style.HishootTheme);
    super.onCreate(savedInstanceState);
    mPresenter.attachView(this);
    mPresenter.attachBottomBar(coordinator, content, savedInstanceState);
    PermissionHelper.storagePermission().with(this, this).build().runRequest();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionHelper.getInstance().onResult(requestCode, permissions, grantResults);
  }

  @Override protected int getToolbarId() {
    return R.id.toolbar;
  }

  @Override protected int layoutRes() {
    return R.layout.activity_launcher;
  }

  @Override protected void setupToolbar(ActionBar actionBar) { /*no-op*/ }

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
    if (mSearchView.isSearchOpen()) {
      mSearchView.closeSearch();
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();
    if (itemId == R.id.action_about) {
      return AboutActivity.start(this);
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  void showProgress(boolean isShow) {
    mProgressBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
  }

  @Subscribe public void onEvent(EventProgress e) {
    showProgress(e.isShow);
  }

  @Subscribe public void onEvent(EventBadgeBB e) {
    mPresenter.bottomBarBadge(e.type, e.count);
  }

  ///////////////// LauncherActivityView /////////////////
  @Override public void setFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commitAllowingStateLoss();
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
