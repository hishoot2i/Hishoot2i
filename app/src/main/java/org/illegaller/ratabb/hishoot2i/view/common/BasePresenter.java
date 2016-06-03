package org.illegaller.ratabb.hishoot2i.view.common;

public class BasePresenter<T extends Mvp.View> implements Mvp.Presenter<T> {
  private T mMvpView;

  @Override public void attachView(T mvpView) {
    this.mMvpView = mvpView;
  }

  @Override public void detachView() {
    this.mMvpView = null;
  }

  public T getView() {
    return mMvpView;
  }

  public boolean isViewAttached() {
    return this.mMvpView != null;
  }

  public void checkViewAttached() {
    if (!isViewAttached()) {
      throw new RuntimeException("Presenter.attachView(MvpVew) before perform");
    }
  }
}
