package org.illegaller.ratabb.hishoot2i.view.rx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.jakewharton.rxbinding.view.ViewEvent;
import com.roughike.bottombar.BottomBar;

public class BottomBarTabEvent extends ViewEvent<BottomBar> {
  private final int mPposition;
  private final Kind mKind;

  BottomBarTabEvent(@NonNull BottomBar view, int position, Kind kind) {
    super(view);
    this.mPposition = position;
    this.mKind = kind;
  }

  @CheckResult @NonNull
  public static BottomBarTabEvent create(@NonNull BottomBar view, int position, Kind kind) {
    return new BottomBarTabEvent(view, position, kind);
  }

  public int getPosition() {
    return mPposition;
  }

  @Override public String toString() {
    return "BottomBarTabEvent{" +
        "mPposition=" + mPposition +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BottomBarTabEvent that = (BottomBarTabEvent) o;
    return mPposition == that.mPposition;
  }

  @Override public int hashCode() {
    return mPposition;
  }

  public Kind getKind() {
    return mKind;
  }

  public enum Kind {
    SELECT, RESELECT
  }
}
