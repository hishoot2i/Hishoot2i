package org.illegaller.ratabb.hishoot2i.presenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.FileExtensionFilter;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.historyview.HistoryFragmentView;
import rx.Observable;
import rx.Subscription;

public class HistoryFragmentPresenter extends BasePresenter<HistoryFragmentView> {

  private final FileExtensionFilter mFileExtFilter = new FileExtensionFilter("png");
  private Subscription mSubscription;

  @Inject public HistoryFragmentPresenter() {
  }

  @Override public void detachView() {
    if (mSubscription != null) mSubscription.unsubscribe();
    super.detachView();
  }

  public void perform() {
    checkViewAttached();
    getView().showProgress(true);
    mSubscription = getListObservable().compose(SimpleSchedule.schedule())
        .subscribe(list -> getView().setList(list),
            throwable -> CrashLog.logError("list", throwable), () -> getView().showProgress(false));
  }

  Observable<List<String>> getListObservable() {
    return Observable.create((Observable.OnSubscribe<List<String>>) subscriber -> {
      try {
        final List<String> result = new ArrayList<>();
        final File folder = AppConstants.getHishootDir();
        final File[] files = folder.listFiles(mFileExtFilter);
        for (File file : files) {
          result.add(file.getAbsolutePath());
        }
        subscriber.onNext(result);
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }
}
