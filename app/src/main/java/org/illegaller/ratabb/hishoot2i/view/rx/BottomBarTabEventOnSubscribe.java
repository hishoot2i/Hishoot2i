package org.illegaller.ratabb.hishoot2i.view.rx;

import com.roughike.bottombar.BottomBar;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.onlyUiThread;

class BottomBarTabEventOnSubscribe implements Observable.OnSubscribe<BottomBarTabEvent> {
  final BottomBar view;

  BottomBarTabEventOnSubscribe(BottomBar view) {
    this.view = view;
  }

  @Override public void call(Subscriber<? super BottomBarTabEvent> subscriber) {
    onlyUiThread();
    final BottomBarOnTabClickListener listener = new BottomBarOnTabClickListener() {
      @Override public void onTabSelected(int position) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(new BottomBarTabEvent(view, position, BottomBarTabEvent.Kind.SELECT));
        }
      }

      @Override public void onTabReSelected(int position) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(new BottomBarTabEvent(view, position, BottomBarTabEvent.Kind.RESELECT));
        }
      }
    };
    view.setOnTabClickListener(listener);
    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {

      }
    });
    //emit?
    subscriber.onNext(new BottomBarTabEvent(view, 0, BottomBarTabEvent.Kind.SELECT));
  }
}
