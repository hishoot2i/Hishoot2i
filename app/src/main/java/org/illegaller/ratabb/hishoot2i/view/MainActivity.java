package org.illegaller.ratabb.hishoot2i.view;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerMainActivityComponent;
import org.illegaller.ratabb.hishoot2i.di.module.MainActivityModule;
import org.illegaller.ratabb.hishoot2i.events.EventProgressBar;
import org.illegaller.ratabb.hishoot2i.events.EventServiceDone;
import org.illegaller.ratabb.hishoot2i.events.EventTools;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.MainFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends BaseActivity implements MainActivityView {
    private static final String KEY_TEMPLATE_ACTIVITY = "key_template_activity";
    @Bind(R.id.progress_bar) SmoothProgressBar mProgressBar;
    @Bind(R.id.fabSave) FloatingActionButton mFab;
    @Inject MainActivityPresenter mPresenter;
    @Nullable @InjectExtra(KEY_TEMPLATE_ACTIVITY) Template mTemplate;
    private TrayManager mTrayManager;
    private MainFragment mMainFragment;

    public static void start(Context context, Template template) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra(KEY_TEMPLATE_ACTIVITY, template);
        context.startActivity(starter);
    }

    public Point pointBackgroundTemplate() {
        if (mTemplate == null) throw new RuntimeException("template null");
        Point templatePoint = mTemplate.templatePoint;
        return mTrayManager.getSsDoubleEnableTray().get()
                ? new Point(templatePoint.x * 2, templatePoint.y) : templatePoint;
    }

    @Override protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override protected void setupComponent(AppComponent component) {
        mTrayManager = component.trayManager();
        DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule())
                .build().inject(this);
    }

    @OnClick(R.id.flBottom) void onClickClose(View view) {
        mPresenter.closeTool();
    }

    @OnClick(R.id.fabSave) void onClickFab(View view) {
        if (mTemplate == null) throw new RuntimeException("template null");
        mProgressBar.setVisibility(View.VISIBLE);
        mFab.hide();
        HishootService.start(this, mMainFragment.getDataImagePath(), mTemplate);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        (menu.findItem(R.id.action_search)).setVisible(false);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_about) {
            Utils.startAbout(this);
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dart.inject(this);
        mPresenter.attachView(this);
        mPresenter.setup(this, savedInstanceState);
        mPresenter.handleImageReceive(getIntent(), mTrayManager.getTemplateIdTray().get(), mTemplate);
        EventBus.getDefault().register(this);
    }

    @Override protected void onDestroy() {
        mPresenter.detachView();
        mMainFragment = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (mPresenter.isToolOpen()) mPresenter.closeTool();
        else super.onBackPressed();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Subscribe public void onEvent(EventProgressBar e) {
        mProgressBar.setVisibility(e.isShow ? View.VISIBLE : View.INVISIBLE);
        mFab.setVisibility(e.isShow || mPresenter.isToolOpen() ? View.GONE : View.VISIBLE);
    }

    @Subscribe public void onEvent(EventTools e) {
        if (e.flag) mPresenter.closeTool();
    }

    @Subscribe public void onEvent(EventServiceDone event) {
        mProgressBar.setVisibility(View.GONE);
        mFab.show();
        final Uri uri = event.mUri;
        Utils.galleryAddPic(this, uri);
        Snackbar snackbar = Snackbar.make(
                ButterKnife.findById(this, R.id.flContent),
                R.string.has_saved,
                Snackbar.LENGTH_SHORT
        );
        snackbar.setAction(R.string.open, new View.OnClickListener() {
            @Override public void onClick(View view) {
                Utils.openImageView(MainActivity.this, uri);
            }
        });
        snackbar.show();
    }

    /////////// MainActivityView ///////////////
    @Override public Context context() {
        return this;
    }

    @Override public void fabSow(boolean isShow) {
        mFab.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override public void setMainFragment(ImageReceive imageReceive, @NonNull Template template) {
        if (imageReceive != null && imageReceive.isBackground)
            mTrayManager.getBgColorEnableTray().set(false);
        mMainFragment = MainFragment.newInstance(imageReceive, template);
        mTemplate = template;
        getFragmentManager().beginTransaction()
                .replace(R.id.flContent, mMainFragment)
                .commitAllowingStateLoss();
    }

}
