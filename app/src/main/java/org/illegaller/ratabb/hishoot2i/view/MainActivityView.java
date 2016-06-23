package org.illegaller.ratabb.hishoot2i.view;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import com.roughike.bottombar.BottomBar;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;

public interface MainActivityView extends Mvp.View {

  boolean isToolOpen();

  //void showActionBar(boolean isShow);

  void showFab(boolean isShow);

  void showProgress(boolean isShow);

  void setImageReceiveTemplate(ImageReceive imageReceive, Template template);

  TrayManager getTrayManager();

  DataImagePath getDataImagePath();

  Template getTemplate();

  BottomBar getBottomBar();

  void setBottomBar(BottomBar bottomBar);

  ViewPager getViewPager();

  View getViewBottom();

  View getFabSave();

  PipetteView getPipetteView();

  ImageView getImageView();

  ActionBar getSupportActionBar();

  Intent getIntent();
}
