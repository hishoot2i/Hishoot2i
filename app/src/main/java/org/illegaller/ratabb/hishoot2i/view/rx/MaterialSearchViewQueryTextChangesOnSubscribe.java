package org.illegaller.ratabb.hishoot2i.view.rx;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.onlyUiThread;

class MaterialSearchViewQueryTextChangesOnSubscribe implements Observable.OnSubscribe<String> {
  final MaterialSearchView view;

  MaterialSearchViewQueryTextChangesOnSubscribe(MaterialSearchView view) {
    this.view = view;
  }

  @Override public void call(Subscriber<? super String> subscriber) {
    onlyUiThread();

    MaterialSearchView.OnQueryTextListener listener = new MaterialSearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(newText);
          return true;
        }
        return false;
      }
    };
    view.setOnQueryTextListener(listener);
    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnQueryTextListener(null);
      }
    });
    //emit?
  }
}
