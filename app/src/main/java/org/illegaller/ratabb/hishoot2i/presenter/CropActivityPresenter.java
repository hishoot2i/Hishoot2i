package org.illegaller.ratabb.hishoot2i.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import java.io.File;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.CropActivityView;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.widget.CropImageView;
import rx.Observable;
import rx.Subscription;

public class CropActivityPresenter extends BasePresenter<CropActivityView> {
  private Subscription mSubscription;

  @Inject public CropActivityPresenter() {
  }

  @Override public void detachView() {
    if (mSubscription != null) mSubscription.unsubscribe();
    super.detachView();
  }

  public void initView(String pathImage) {
    checkViewAttached();
    getView().showProgress(true);
    mSubscription = imageObservable(pathImage).compose(SimpleSchedule.schedule())
        .subscribe(bitmap -> getView().setCropImageView(bitmap),
            throwable -> CrashLog.logError("initCropView", throwable),
            () -> getView().showProgress(false));
  }

  public void performSaveCrop(CropImageView cropImageView) {
    checkViewAttached();
    getView().showProgress(true);
    mSubscription = saveObservable(cropImageView).compose(SimpleSchedule.schedule())
        .subscribe(uri -> getView().onResult(uri), throwable -> {
          CrashLog.logError("doSaveCrop", throwable);
          getView().onResult(Uri.EMPTY);
        }, () -> getView().showProgress(false));
  }

  Observable<Bitmap> imageObservable(final String pathImage) {
    return Observable.create((Observable.OnSubscribe<Bitmap>) subscriber -> {
      try {
        Bitmap bitmap = UILHelper.loadImage(pathImage);
        subscriber.onNext(bitmap);
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }

  Observable<Uri> saveObservable(final CropImageView cropImageView) {
    return Observable.create((Observable.OnSubscribe<Uri>) subscriber -> {
      Bitmap bitmap = null;
      try {
        bitmap = cropImageView.getCroppedBitmap();
        if (bitmap != null) {
          File file = Utils.saveTempBackgroundCrop(cropImageView.getContext(), bitmap);
          subscriber.onNext(Uri.fromFile(file));
          subscriber.onCompleted();
        } else {
          subscriber.onError(new Exception("bitmap crop is null"));
        }
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }
}
