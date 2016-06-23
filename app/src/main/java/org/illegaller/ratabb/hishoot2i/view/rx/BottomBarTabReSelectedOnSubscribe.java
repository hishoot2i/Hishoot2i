package org.illegaller.ratabb.hishoot2i.view.rx;

import com.roughike.bottombar.BottomBar;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.onlyUiThread;

class BottomBarTabReSelectedOnSubscribe implements Observable.OnSubscribe<Integer> {
  final BottomBar view;

  BottomBarTabReSelectedOnSubscribe(BottomBar view) {
    this.view = view;
  }

  @Override public void call(Subscriber<? super Integer> subscriber) {
    onlyUiThread();

    final BottomBarOnTabClickListener listener = new BottomBarOnTabClickListener() {
      @Override public void onTabReSelected(int position) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(position);
        }
      }
    };
    view.setOnTabClickListener(listener);
    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {

      }
    });

    subscriber.onNext(view.getCurrentTabPosition());
  }
}
