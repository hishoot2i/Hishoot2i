package org.illegaller.ratabb.hishoot2i.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.CropActivityView;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.widget.CropImageView;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule.schedule;
import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.loadImage;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.saveBackgroundCrop;

public class CropActivityPresenter extends BasePresenter<CropActivityView> {

  @Inject CropActivityPresenter() {
  }

  public void initView() {
    checkViewAttached();
    showProgress();
    final CompositeSubscription sub = new CompositeSubscription();
    sub.add(imageObservable().compose(schedule())
        .subscribe(this::setCropView, CrashLog::logError, this::hideProgress));

    sub.add(RxView.clicks(getMvpView().getViewBtnOk())
        .subscribe(click -> performSaveCrop(), CrashLog::logError));

    sub.add(RxView.clicks(getMvpView().getViewBtnCancel())
        .subscribe(click -> resultCancel(null), CrashLog::logError));

    addAutoUnSubscribe(sub);
  }

  private void performSaveCrop() {
    showProgress();
    addAutoUnSubscribe(saveObservable().compose(schedule())
        .subscribe(this::resultOk, this::resultCancel, this::hideProgress));
  }

  private void setCropView(Bitmap bitmap) {
    final Point ration = getMvpView().getPointRatio();
    final CropImageView cropImageView = getMvpView().getCropImageView();
    cropImageView.setCustomRatio(ration.x, ration.y);
    cropImageView.setImageBitmap(checkNotNull(bitmap, "bitmap == null"));
  }

  private void showProgress() {
    getMvpView().showProgress(true);
  }

  private void hideProgress() {
    getMvpView().showProgress(false);
  }

  private void resultOk(Uri uri) {
    getMvpView().setResult(Activity.RESULT_OK, intentResult(uri));
    getMvpView().finish();
  }

  private void resultCancel(@Nullable Throwable throwable) {
    if (throwable != null) CrashLog.logError(throwable);
    getMvpView().setResult(Activity.RESULT_CANCELED);
    getMvpView().finish();
  }

  private Intent intentResult(Uri data) {
    return new Intent().setData(data);
  }

  private Observable<Bitmap> imageObservable() {
    return Observable.create(subscriber -> {
      try {
        final String pathImage = getMvpView().getPathImage();
        subscriber.onNext(loadImage(pathImage));
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }

  private Observable<Uri> saveObservable() {
    return Observable.create(subscriber -> {
      final Bitmap bitmap = getMvpView().getCropImageView().getCroppedBitmap();
      if (bitmap != null) {
        try {
          final File file = saveBackgroundCrop(getMvpView().getContext(), bitmap);
          subscriber.onNext(Uri.fromFile(file));
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      } else {
        subscriber.onError(new NullPointerException("bitmap crop is null"));
      }
    });
  }
}
