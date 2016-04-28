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
import javax.inject.Named;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.MainActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateModule;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.events.EventPreview;
import org.illegaller.ratabb.hishoot2i.events.EventSave;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_INT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.SS_DOUBLE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.TEMPLATE_ID;

public class MainActivity extends BaseActivity implements MainActivityView {
  private static final String KEY_TEMPLATE_ACTIVITY = "key_template_activity";
  @BindView(R.id.progress_bar) View mProgressBar;
  @BindView(R.id.fabSave) View mFab;
  @BindView(R.id.flBottom) View flBottom;
  @BindView(R.id.mainImageView) ImageView mImageView;
  @BindView(R.id.pipetteView) PipetteView mPipetteView;
  @Inject MainActivityPresenter mPresenter;
  @Inject @Named(BG_COLOR_INT) IntTray bgColorIntTray;
  @Inject @Named(SS_DOUBLE_ENABLE) BooleanTray ssDoubleEnableTray;
  @Inject @Named(TEMPLATE_ID) StringTray templateIdTray;
  @Inject @Named(BG_COLOR_ENABLE) BooleanTray bgColorEnableTray;
  @Nullable @InjectExtra(KEY_TEMPLATE_ACTIVITY) Template mTemplate;
  private String pathImageSS1;
  private String pathImageSS2;
  private String pathImageBg;
  private DataImagePath mDataImagePath;
  private ActionBar actionBar;

  public static void start(Context context, Template template) {
    Intent starter = new Intent(context, MainActivity.class);
    starter.putExtra(KEY_TEMPLATE_ACTIVITY, template);
    starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    context.startActivity(starter);
  }

  public Point pointBackgroundTemplate() {
    Utils.checkNotNull(mTemplate, "mTemplate == null");
    Point templatePoint = mTemplate.templatePoint;
    return ssDoubleEnableTray.get() ? new Point(templatePoint.x * 2, templatePoint.y)
        : templatePoint;
  }

  @Override protected void setupToolbar(ActionBar actionBar) {
    this.actionBar = actionBar; /*need for show/hide on action pipette*/
    this.actionBar.setDisplayShowTitleEnabled(false);
    this.actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override protected int getToolbarId() {
    return R.id.toolbar;
  }

  @Override protected int layoutRes() {
    return R.layout.activity_main;
  }

  @Override protected void setupComponent(ApplicationComponent component) {
    component.plus(new TemplateModule()).plus(new MainActivityModule()).inject(this);
  }

  @OnClick({ R.id.fabSave, R.id.flBottom }) void onClick(View view) {
    if (view == mFab) {
      mPresenter.perform(true, mDataImagePath, mTemplate);
    } else if (view == flBottom) mPresenter.closeTool(true);
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
    final String action = getIntent().getAction();
    if (Intent.ACTION_SEND.equals(action)) {
      final Uri imageUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
      mPresenter.handleImageReceive(imageUri, templateIdTray.get());
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
        .setAction(R.string.open, new View.OnClickListener() {
          @Override public void onClick(View view) {
            Utils.openImageView(MainActivity.this, uri);
          }
        })
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
        pathImageSS1 = event.path;
        break;
      case SS2:
        pathImageSS2 = event.path;
        break;
      case BG:
        pathImageBg = event.path;
        break;
      default:
      case NONE:/*no-op, just closing tools & perform image process*/
        break;
    }
    mPresenter.closeTool(true);
  }

  @Subscribe public void onEvent(EventPipette event) {
    mPresenter.pipette(event, mImageView, mPipetteView);
  }

  /////////// MainActivityView ///////////////
  @Override public Context context() {
    return this;
  }

  @Override public void showFab(boolean isShow) {
    mFab.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public void showProgress(boolean isShow) {
    mProgressBar.setVisibility(isShow ? VISIBLE : INVISIBLE);
  }

  @Override public void perform() {
    mDataImagePath = new DataImagePath(pathImageSS1, pathImageSS2, pathImageBg);
    mPresenter.perform(false, mDataImagePath, mTemplate);
  }

  @Override public void setImageReceiveTemplate(@Nullable ImageReceive imageReceive,
      @NonNull Template template) {
    if (imageReceive == null) return;
    if (imageReceive.isBackground) {
      bgColorEnableTray.set(false);
      pathImageBg = imageReceive.imagePath;
    } else {
      pathImageSS1 = imageReceive.imagePath;
    }
    mTemplate = template;
    mPresenter.closeTool(true);
  }

  @Override public void closePipette() {
    mPipetteView.close(true);
  }

  @Override public void pipetteResult(int color) {
    bgColorIntTray.set(color);
    perform();
    ((CircleButton) ButterKnife.findById(this, R.id.cpfMixer)).setColor(color);
    ((CircleButton) ButterKnife.findById(this, R.id.cpfPipette)).setColor(color);
  }

  @Override public void showActionBar(boolean isShow) {
    if (isShow) {
      actionBar.show();
    } else {
      actionBar.hide();
    }
  }
}
