package org.illegaller.ratabb.hishoot2i.view.rx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.roughike.bottombar.BottomBar;
import rx.Observable;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public class RxBottomBar {

  private RxBottomBar() {
    throw new AssertionError("no instance");
  }

  @CheckResult @NonNull
  public static Observable<Integer> tabSelected(@NonNull BottomBar bottomBar) {
    checkNotNull(bottomBar, "bottomBar == null");
    return Observable.create(new BottomBarTabSelectedOnSubscribe(bottomBar));
  }

  @CheckResult @NonNull
  public static Observable<Integer> tabReSelected(@NonNull BottomBar bottomBar) {
    checkNotNull(bottomBar, "bottomBar == null");
    return Observable.create(new BottomBarTabReSelectedOnSubscribe(bottomBar));
  }

  @CheckResult @NonNull
  public static Observable<BottomBarTabEvent> tabEvent(@NonNull BottomBar bottomBar) {
    checkNotNull(bottomBar, "bottomBar == null");
    return Observable.create(new BottomBarTabEventOnSubscribe(bottomBar));
  }
}
