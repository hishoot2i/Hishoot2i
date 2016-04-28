package org.illegaller.ratabb.hishoot2i.utils;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SimpleSchedulers {
  public Scheduler backgroundThread() {
    return Schedulers.io();
  }

  public Scheduler mainThread() {
    return AndroidSchedulers.mainThread();
  }
}
