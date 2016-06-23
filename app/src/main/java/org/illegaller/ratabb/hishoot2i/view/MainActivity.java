package org.illegaller.ratabb.hishoot2i.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.roughike.bottombar.BottomBar;
import java.io.File;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.events.EventPreview;
import org.illegaller.ratabb.hishoot2i.events.EventSave;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.FileUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.galleryAddPic;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.openImageView;

public class MainActivity extends BaseActivity implements MainActivityView {
  private static final String KEY_TEMPLATE_ACTIVITY = "key_template_activity";
  @BindView(R.id.progress_bar) View mProgressBar;
  @BindView(R.id.fabSave) View mFabSave;
  @BindView(R.id.flBottom) View mViewBottom;
  @BindView(R.id.viewPager) ViewPager mViewPager;
  @BindView(R.id.mainImageView) ImageView mImageView;
  @BindView(R.id.pipetteView) PipetteView mPipetteView;
  @Inject MainActivityPresenter mPresenter;
  @Nullable @InjectExtra(KEY_TEMPLATE_ACTIVITY) Template mTemplate;
  @Inject TrayManager mTrayManager;
  private String mPathImageSS1;
  private String mPathImageSS2;
  private String mPathImageBg;
  private BottomBar mBottomBar;

  public static void start(Context context, Template template) {
    Intent starter = new Intent(context, MainActivity.class);
    starter.putExtra(KEY_TEMPLATE_ACTIVITY, template);
    starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(starter);
  }

  public Point pointBackgroundTemplate() {
    final Point templatePoint = checkNotNull(mTemplate, "mTemplate == null").templatePoint;
    return mTrayManager.getDoubleEnable().isValue() ? new Point(templatePoint.x * 2,
        templatePoint.y) : templatePoint;
  }

  @Override protected void setupToolbar(ActionBar actionBar) {
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override protected int getToolbarId() {
    return R.id.toolbar;
  }

  @Override protected int layoutRes() {
    return R.layout.activity_main;
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
      return AboutActivity.start(this);
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Dart.inject(this);
    mPresenter.attachView(this);
    mPresenter.setup(this, savedInstanceState);
    EventBus.getDefault().register(this);
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override protected void onDestroy() {
    mPresenter.detachView();
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    if (isToolOpen()) {
      mPresenter.closeTool(true);
    } else if (mPipetteView.isOpen()) {
      mPipetteView.close(true);
    } else {
      super.onBackPressed();
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    getBottomBar().onSaveInstanceState(outState);
  }

  /////////////////// EvenBust subscribe //////////////////////////////
  @Subscribe public void onEvent(EventSave event) {
    showProgress(false);
    showFab(true);
    final Uri uri = event.uri;
    galleryAddPic(this, uri);
    Snackbar.make(mFabSave, R.string.has_saved, Snackbar.LENGTH_SHORT)
        .setAction(R.string.open, view -> openImageView(MainActivity.this, uri))
        .show();
  }

  @Subscribe public void onEvent(EventPreview event) {
    if (event.bitmap != null) {
      mImageView.setImageBitmap(event.bitmap);
    } else {
      Snackbar.make(mFabSave, event.message + '\n' + event.extra, Snackbar.LENGTH_SHORT).show();
    }
    showProgress(false);
    showFab(true);
  }

  @Subscribe public void onEvent(EventImageSet event) {
    switch (event.type) {
      case SS1:
        mPathImageSS1 = event.path;
        break;
      case SS2:
        mPathImageSS2 = event.path;
        break;
      case BG:
        mPathImageBg = event.path;
        break;
      default:
      case NONE: /* no-op, just closing tools & startService process*/
        break;
    }
    mPresenter.closeTool(true);
  }

  @Subscribe public void onEvent(EventPipette event) {
    mPresenter.pipette(event);
  }

  ////////////////////////////////////////////

  @Override public boolean isToolOpen() {
    return mViewBottom.getTranslationY() == 0;
  }

  @Override public ImageView getImageView() {
    return mImageView;
  }

  @Override public PipetteView getPipetteView() {
    return mPipetteView;
  }

  @Override public View getFabSave() {
    return mFabSave;
  }

  @Override public View getViewBottom() {
    return mViewBottom;
  }

  @Override public ViewPager getViewPager() {
    return mViewPager;
  }

  @Override public BottomBar getBottomBar() {
    return mBottomBar;
  }

  @Override public void setBottomBar(BottomBar bottomBar) {
    mBottomBar = bottomBar;
  }

  @Override public Context getContext() {
    return this;
  }

  @Override public void showFab(boolean isShow) {
    mFabSave.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public void showProgress(boolean isShow) {
    mProgressBar.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public DataImagePath getDataImagePath() {
    return new DataImagePath(mPathImageSS1, mPathImageSS2, mPathImageBg);
  }

  @Override public Template getTemplate() {
    return mTemplate;
  }

  @Override public TrayManager getTrayManager() {
    return mTrayManager;
  }

  @Override
  public void setImageReceiveTemplate(ImageReceive imageReceive, @NonNull Template template) {
    if (imageReceive == null) return;
    final String realPath = FileUtils.getRealPath(getContext(), imageReceive.getImageUri());
    if (realPath == null) throw new NullPointerException("create: " + imageReceive.getImageUri());
    final String path = UILHelper.stringFiles(new File(realPath));
    if (imageReceive.isBackground()) {
      mTrayManager.getBackgroundColorEnable().setValue(false);
      mPathImageBg = path;
    } else {
      mPathImageSS1 = path;
    }
    mTemplate = template;
    mPresenter.closeTool(true);
  }

/*  @Override public void showActionBar(boolean isShow) {
    if (isShow) {
      mActionBar.show();
    } else {
      mActionBar.hide();
    }
  }*/
}
