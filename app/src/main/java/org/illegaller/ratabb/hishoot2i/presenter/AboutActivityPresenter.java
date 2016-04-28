package org.illegaller.ratabb.hishoot2i.presenter;

import org.illegaller.ratabb.hishoot2i.view.AboutActivityView;
import org.illegaller.ratabb.hishoot2i.view.fragment.AboutFragment;

public class AboutActivityPresenter implements IPresenter<AboutActivityView> {
  private AboutActivityView mView;

  @Override public void attachView(AboutActivityView view) {
    this.mView = view;
  }

  @Override public void detachView() {
    this.mView = null;
  }

  public void init() {
    this.mView.setFragmentAboutLibs(AboutFragment.newInstance());
  }
}
