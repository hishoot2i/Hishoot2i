/*
package org.illegaller.ratabb.hishoot2i.presenter;

import android.os.Build;
import android.os.Bundle;
import org.illegaller.ratabb.hishoot2i.events.EventBadgeBB;
import org.illegaller.ratabb.hishoot2i.BuildConfig;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class LauncherActivityPresenterTest {
  @Mock LauncherActivity mView;
  @Mock TemplateFragment mFragment;
  private LauncherActivityPresenter mPresenter;

  @Before public void setup() {
    MockitoAnnotations.initMocks(this);
    mPresenter = new LauncherActivityPresenter();
    mPresenter.attachView(mView);
  }

  @After public void tearDown() {
    mPresenter.detachView();
  }

  @Test public void testAttachBottomBarAndCallSetFragment() {
    // TODO: 15/05/2016
    final Bundle bundle = new Bundle();

    mPresenter.attachBottomBar(mView, bundle);
  }

  @Test public void testSetBottomBarBadge() {
    // TODO: 15/05/2016
    EventBadgeBB.Type typeBadge = EventBadgeBB.Type.INSTALLED;
    int countBadge = 10;
    mPresenter.bottomBarBadge(typeBadge, countBadge);
  }
}
*/
