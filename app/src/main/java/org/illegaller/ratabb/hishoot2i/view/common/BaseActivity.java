package org.illegaller.ratabb.hishoot2i.view.common;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.Window;
import butterknife.ButterKnife;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.utils.DeviceUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {
  private ViewGroup contentView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupComponent(HishootApplication.get(this).getApplicationComponent());
    setContentView(layoutRes());
    contentView = ButterKnife.findById(this, android.R.id.content);
    ButterKnife.bind(this);
    setupWindowAnimAndTransparentStatusBar();
    if (getToolbarId() != 0) {
      setSupportActionBar((Toolbar) ButterKnife.findById(this, getToolbarId()));
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) setupToolbar(actionBar);
    }
  }

  private void setupWindowAnimAndTransparentStatusBar() {
    final Window window = getWindow();
    window.setWindowAnimations(R.style.Animation_Hishoot_Window);
    DeviceUtils.setTransparentStatusBar(window);
  }

  @Override protected void onDestroy() {
    Utils.fixInputMethodManager(this);
    Utils.unbindDrawables(contentView);
    super.onDestroy();
  }

  @IdRes protected abstract int getToolbarId();

  @LayoutRes protected abstract int layoutRes();

  protected abstract void setupToolbar(ActionBar actionBar);

  protected abstract void setupComponent(ApplicationComponent component);
}
