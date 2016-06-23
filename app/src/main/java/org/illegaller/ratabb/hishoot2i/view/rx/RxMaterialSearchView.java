package org.illegaller.ratabb.hishoot2i.view.rx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import rx.Observable;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public class RxMaterialSearchView {
  private RxMaterialSearchView() {
    throw new AssertionError("no instance");
  }

  @CheckResult @NonNull
  public static Observable<String> queryTextChanges(@NonNull MaterialSearchView view) {
    checkNotNull(view, "view == null");
    return Observable.create(new MaterialSearchViewQueryTextChangesOnSubscribe(view));
  }
}
