package org.illegaller.ratabb.hishoot2i.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.events.EventPreview;
import org.illegaller.ratabb.hishoot2i.events.EventSave;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity implements MainActivityView {
  private static final String KEY_TEMPLATE_ACTIVITY = "key_template_activity";
  @BindView(R.id.progress_bar) View mProgressBar;
  @BindView(R.id.fabSave) View mFab;
  @BindView(R.id.flBottom) View mViewBottom;
  @BindView(R.id.mainImageView) ImageView mImageView;
  @BindView(R.id.pipetteView) PipetteView mPipetteView;
  @Inject MainActivityPresenter mPresenter;
  @Inject TrayManager mTrayManager;
  @Nullable @InjectExtra(KEY_TEMPLATE_ACTIVITY) Template mTemplate;
  private IntTray mBgColorIntTray;
  private BooleanTray mDoubleEnableTray;
  private BooleanTray mBgColorEnableTray;
  private String mPathImageSS1;
  private String mPathImageSS2;
  private String mPathImageBg;
  private DataImagePath mDataImagePath;
  private ActionBar mActionBar;

  public static void start(Context context, Template template) {
    Intent starter = new Intent(context, MainActivity.class);
    starter.putExtra(KEY_TEMPLATE_ACTIVITY, template);
    starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(starter);
  }

  public Point pointBackgroundTemplate() {
    Utils.checkNotNull(mTemplate, "mTemplate == null");
    Point templatePoint = mTemplate.templatePoint;
    return mDoubleEnableTray.isValue() ? new Point(templatePoint.x * 2, templatePoint.y)
        : templatePoint;
  }

  @Override protected void setupToolbar(ActionBar actionBar) {
    this.mActionBar = actionBar; /*need for show/hide on action pipette*/
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override protected int getToolbarId() {
    return R.id.toolbar;
  }

  @Override protected int layoutRes() {
    return R.layout.activity_main;
  }

  @OnClick({ R.id.fabSave, R.id.flBottom }) void onClick(View view) {
    if (view == mFab) {
      mPresenter.perform(true, mDataImagePath, mTemplate);
    } else if (view == mViewBottom) mPresenter.closeTool(true);
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
    getActivityComponent().inject(this);
    mBgColorEnableTray = mTrayManager.getBackgroundColorEnable();
    mBgColorIntTray = mTrayManager.getBackgroundColorInt();
    mDoubleEnableTray = mTrayManager.getDoubleEnable();
    mPresenter.attachView(this);
    mPresenter.setup(this, savedInstanceState);
    final String action = getIntent().getAction();
    if (Intent.ACTION_SEND.equals(action)) {
      final Uri imageUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
      mPresenter.handleImageReceive(imageUri, mTrayManager.getTemplateID().getValue());
    }
    EventBus.getDefault().register(this);
  }

  @Override protected void onDestroy() {
    mPresenter.detachView();
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  @Override public void onBackPressed() {
    if (mPresenter.isToolOpen()) {
      mPresenter.closeTool(true);
    } else if (mPipetteView.isOpen()) {
      mPipetteView.close(true);
    } else {
      super.onBackPressed();
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mPresenter.onSaveInstanceState(outState);
  }

  /////////////////// EvenBust subscribe //////////////////////////////
  @Subscribe public void onEvent(EventSave event) {
    showProgress(false);
    showFab(true);
    final Uri uri = event.uri;
    Utils.galleryAddPic(this, uri);
    Snackbar.make(mFab, R.string.has_saved, Snackbar.LENGTH_SHORT)
        .setAction(R.string.open, view -> Utils.openImageView(MainActivity.this, uri))
        .show();
  }

  @Subscribe public void onEvent(EventPreview event) {
    if (event.result != null) {
      mImageView.setImageBitmap(event.result);
    } else {
      Snackbar.make(mFab, event.message + "\n" + event.extra, Snackbar.LENGTH_SHORT).show();
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
      case NONE: /* no-op, just closing tools & perform process*/
        break;
    }
    mPresenter.closeTool(true);
  }

  @Subscribe public void onEvent(EventPipette event) {
    mPresenter.pipette(event, mImageView, mPipetteView);
  }

  /////////// MainActivityView ///////////////

  @Override public Context getContext() {
    return this;
  }

  @Override public void showFab(boolean isShow) {
    mFab.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public void showProgress(boolean isShow) {
    mProgressBar.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public void perform() {
    mDataImagePath = new DataImagePath(mPathImageSS1, mPathImageSS2, mPathImageBg);
    mPresenter.perform(false, mDataImagePath, mTemplate);
  }

  @Override public void setImageReceiveTemplate(@Nullable ImageReceive imageReceive,
      @NonNull Template template) {
    if (imageReceive == null) return;
    if (imageReceive.isBackground) {
      mBgColorEnableTray.setValue(false);
      mPathImageBg = imageReceive.imagePath;
    } else {
      mPathImageSS1 = imageReceive.imagePath;
    }
    mTemplate = template;
    mPresenter.closeTool(true);
  }

  @Override public void closePipette() {
    mPipetteView.close(true);
  }

  @Override public void pipetteResult(int color) {
    mBgColorIntTray.setValue(color);
    perform();
    ((CircleButton) ButterKnife.findById(this, R.id.cpfMixer)).setColor(color);
    ((CircleButton) ButterKnife.findById(this, R.id.cpfPipette)).setColor(color);
  }

  @Override public void showActionBar(boolean isShow) {
    if (isShow) {
      mActionBar.show();
    } else {
      mActionBar.hide();
    }
  }
}
