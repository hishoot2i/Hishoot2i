package org.illegaller.ratabb.hishoot2i.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SimpleSchedule<T> implements Observable.Transformer<T, T> {
  private SimpleSchedule() {
  }

  public static <T> SimpleSchedule<T> schedule() {
    return new SimpleSchedule<>();
  }

  @Override public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }
}
