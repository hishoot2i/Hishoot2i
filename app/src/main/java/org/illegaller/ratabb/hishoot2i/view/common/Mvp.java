package org.illegaller.ratabb.hishoot2i.view.common;

import android.content.Context;

public class Mvp {
  public interface View {
    Context getContext();
  }

  interface Presenter<V extends View> {
    void attachView(V mvpView);

    void detachView();
  }
}
