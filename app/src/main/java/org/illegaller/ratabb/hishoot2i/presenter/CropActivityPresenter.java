package org.illegaller.ratabb.hishoot2i.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import java.io.File;
import java.io.IOException;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.SimpleObserver;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.CropActivityView;
import rx.Observable;
import rx.Subscriber;

public class CropActivityPresenter implements IPresenter<CropActivityView> {
  private SimpleSchedulers schedulers;
  private CropActivityView view;

  public CropActivityPresenter(SimpleSchedulers schedulers) {
    this.schedulers = schedulers;
  }

  @Override public void attachView(CropActivityView view) {
    this.view = view;
  }

  @Override public void detachView() {
    this.view = null;
  }

  public void initView(String pathImage) {
    view.showProgress(true);
    imageObservable(pathImage).subscribe(new SimpleObserver<Bitmap>() {
      @Override public void onNext(Bitmap bitmap) {
        view.setCropImageView(bitmap);
      }
    });
  }

  public void performSaveCrop(Bitmap bitmap) {
    saveObservable(bitmap).subscribe(new SimpleObserver<Uri>() {
      @Override public void onNext(Uri uri) {
        view.onResult(uri);
      }

      @Override public void onError(Throwable e) {
        CrashLog.logError("doSaveCrop", e);
        view.onResult(Uri.EMPTY);
      }
    });
  }

  Observable<Bitmap> imageObservable(final String pathImage) {
    view.showProgress(true);
    return Observable.create(new Observable.OnSubscribe<Bitmap>() {
      @Override public void call(Subscriber<? super Bitmap> subscriber) {
        Bitmap bitmap = UILHelper.loadImage(pathImage);
        if (bitmap == null) subscriber.onError(new Exception("display image crop"));
        subscriber.onNext(bitmap);
        subscriber.onCompleted();
      }
    }).subscribeOn(schedulers.backgroundThread()).observeOn(schedulers.mainThread());
  }

  Observable<Uri> saveObservable(final Bitmap bitmap) {
    return Observable.create(new Observable.OnSubscribe<Uri>() {
      @Override public void call(Subscriber<? super Uri> subscriber) {
        try {
          File file = Utils.saveTempBackgroundCrop(view.context(), bitmap);
          subscriber.onNext(Uri.fromFile(file));
          subscriber.onCompleted();
        } catch (IOException e) {
          subscriber.onError(e);
        }
      }
    }).subscribeOn(schedulers.backgroundThread()).observeOn(schedulers.mainThread());
  }
}
