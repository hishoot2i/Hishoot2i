package org.illegaller.ratabb.hishoot2i.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.OnClick;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.CropActivityModule;
import org.illegaller.ratabb.hishoot2i.presenter.CropActivityPresenter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.widget.CropImageView;

public class CropActivity extends BaseActivity implements CropActivityView {
  private static final String KEY_PATH_IMAGE = "path_image";
  private static final String KEY_POINT_RATIO = "point_ratio";
  @InjectExtra(KEY_PATH_IMAGE) String pathImage;
  @InjectExtra(KEY_POINT_RATIO) Point pointRatio;
  @BindView(R.id.cropImageVIew) CropImageView mCropImageView;
  @BindView(R.id.pbCrop) ProgressBar mProgressBar;
  @Inject CropActivityPresenter presenter;

  public static Intent getIntent(Context context, String path, Point ratio) {
    Intent starter = new Intent(context, CropActivity.class);
    starter.putExtra(KEY_PATH_IMAGE, path);
    starter.putExtra(KEY_POINT_RATIO, ratio);
    starter.putExtra(Intent.EXTRA_RETURN_RESULT, true);
    return starter;
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Dart.inject(this);
    presenter.attachView(this);
    presenter.initView(pathImage);
  }

  @Override protected void onDestroy() {
    presenter.detachView();
    super.onDestroy();
  }

  @Override protected int getToolbarId() {
    return View.NO_ID;
  }

  @Override protected void setupComponent(ApplicationComponent component) {
    component.plus(new CropActivityModule()).inject(this);
  }

  @Override protected int layoutRes() {
    return R.layout.activity_crop;
  }

  @Override protected void setupToolbar(ActionBar actionBar) { /*no-op*/ }

  @OnClick({ R.id.btnOkCrop, R.id.btnCancelCrop }) void onClick(View view) {
    final int viewId = view.getId();
    if (viewId == R.id.btnOkCrop) {
      presenter.performSaveCrop(mCropImageView.getCroppedBitmap());
    } else if (viewId == R.id.btnCancelCrop) onResult(null);
  }

  @Override public void onResult(@Nullable Uri uri) {
    if (uri != null) {
      setResult(Activity.RESULT_OK, new Intent().setData(uri));
    } else {
      setResult(Activity.RESULT_CANCELED);
    }
    finish();
  }

  @Override public void showProgress(boolean isShow) {
    mProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    mCropImageView.setVisibility(isShow ? View.GONE : View.VISIBLE);
  }

  @Override public void setCropImageView(Bitmap bitmap) {
    mCropImageView.setCustomRatio(pointRatio.x, pointRatio.y);
    mCropImageView.setImageBitmap(bitmap);
    showProgress(false);
  }

  @Override public Context context() {
    return this;
  }
}
