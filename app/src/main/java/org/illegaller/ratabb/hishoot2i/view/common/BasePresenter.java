package org.illegaller.ratabb.hishoot2i.view.common;

import android.support.annotation.NonNull;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import rx.Subscription;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public class BasePresenter<T extends Mvp.View> implements Mvp.Presenter<T> {
  private Subscription mSubscription;
  private Reference<T> mViewRef;

  @Override public void attachView(T mvpView) {
    this.mViewRef = new WeakReference<>(mvpView);
  }

  @Override public void detachView() {
    if (mViewRef != null) {
      this.mViewRef.clear();
      this.mViewRef = null;
    }
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
  }

  protected T getMvpView() {
    checkViewAttached();
    return mViewRef.get();
  }

  private boolean isViewAttached() {
    return this.mViewRef.get() != null;
  }

  protected void addAutoUnSubscribe(@NonNull final Subscription subscription) {
    mSubscription = checkNotNull(subscription, "Subscription must be not null");
  }

  protected void checkViewAttached() {
    if (!isViewAttached()) {
      throw new RuntimeException("Presenter.attachView(View) first");
    }
  }
}
