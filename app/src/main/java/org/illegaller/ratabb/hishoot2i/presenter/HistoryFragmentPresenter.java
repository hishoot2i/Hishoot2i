package org.illegaller.ratabb.hishoot2i.presenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.FileExtensionFilter;
import org.illegaller.ratabb.hishoot2i.utils.SimpleObserver;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;
import org.illegaller.ratabb.hishoot2i.view.fragment.HistoryFragmentView;
import rx.Observable;

public class HistoryFragmentPresenter implements IPresenter<HistoryFragmentView> {
  private HistoryFragmentView mView;
  private SimpleSchedulers schedulers;
  private FileExtensionFilter filter = new FileExtensionFilter("png");

  public HistoryFragmentPresenter(SimpleSchedulers schedulers) {
    this.schedulers = schedulers;
  }

  @Override public void attachView(HistoryFragmentView view) {
    this.mView = view;
  }

  @Override public void detachView() {
    this.mView = null;
  }

  public void perform() {
    this.mView.showProgress(true);
    getListObservable().subscribe(new SimpleObserver<List<String>>() {
      @Override public void onNext(List<String> list) {
        mView.setList(list);
      }

      @Override public void onCompleted() {
        mView.showProgress(false);
      }

      @Override public void onError(Throwable e) {
        mView.showProgress(false);
        CrashLog.logError("list", e);
      }
    });
  }

  Observable<List<String>> getListObservable() {
    return Observable.create((Observable.OnSubscribe<List<String>>) subscriber -> {
      try {
        final List<String> result = new ArrayList<>();
        final File folder = AppConstants.getHishootDir();
        File[] files = folder.listFiles(filter);
        for (File file : files) {
          result.add(file.getAbsolutePath());
        }
        subscriber.onNext(result);
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    }).subscribeOn(schedulers.backgroundThread()).observeOn(schedulers.mainThread());
  }
}
