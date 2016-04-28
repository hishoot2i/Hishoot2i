package org.illegaller.ratabb.hishoot2i.presenter;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;
import java.io.File;
import java.util.List;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.SimpleObserver;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.MainActivityView;
import org.illegaller.ratabb.hishoot2i.view.adapter.ToolFragmentAdapter;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;

public class MainActivityPresenter
    implements IPresenter<MainActivityView>, OnTabClickListener, ViewPager.OnPageChangeListener,
    View.OnTouchListener {
  private ViewPager mViewPager;
  private BottomBar mBottomBar;
  private View flBottom;
  private PipetteView pipetteView;
  private MainActivityView mView;
  private TemplateManager templateManager;

  public MainActivityPresenter(TemplateManager templateManager) {
    this.templateManager = templateManager;
  }

  public void onSaveInstanceState(Bundle outState) {
    mBottomBar.onSaveInstanceState(outState);
  }

  @Override public void attachView(MainActivityView view) {
    this.mView = view;
  }

  @Override public void detachView() {
    this.mViewPager.removeOnPageChangeListener(this);
    this.mBottomBar = null;
    this.mViewPager = null;
    this.flBottom = null;
    this.pipetteView = null;
    this.mView = null;
  }

  public void setup(AppCompatActivity activity, Bundle saveState) {
    this.flBottom = ButterKnife.findById(activity, R.id.flBottom);
    this.mViewPager = ButterKnife.findById(activity, R.id.viewPager);
    this.mViewPager.setAdapter(new ToolFragmentAdapter(activity.getSupportFragmentManager()));
    this.mViewPager.addOnPageChangeListener(this);
    this.mBottomBar = bottomBar(activity, saveState);
    openTool();
  }

  public void handleImageReceive(Uri imageUri, final String templateId) {
    String stringUri = Utils.getStringFromUri(mView.context(), imageUri);
    final String sImagePath = UILHelper.stringFiles(new File(stringUri));
    templateManager.getTemplateList(TemplateManager.NO_FAV)
        .subscribe(new SimpleObserver<List<Template>>() {
          @Override public void onCompleted() {
            showDialog(sImagePath, templateManager.getTemplateById(templateId));
          }
        });
  }

  public boolean isToolOpen() {
    return flBottom.getTranslationY() == 0;
  }

  public void closeTool(boolean needPerform) {
    AnimUtils.translateY(flBottom, 0, flBottom.getHeight());
    showShadowBottomBar();
    mView.showFab(needPerform);
    if (needPerform) mView.perform();
  }

  public void perform(boolean isSaving, DataImagePath dataImagePath, Template template) {
    mView.showProgress(true);
    mView.showFab(false);
    if (isSaving) {
      HishootService.startActionSave(mView.context(), dataImagePath, template);
    } else {
      HishootService.startActionPreview(mView.context(), dataImagePath, template);
    }
  }

  public void pipette(EventPipette event, View view, PipetteView pipetteView) {
    if (event.isShow) {
      closeTool(false);
      view.setDrawingCacheEnabled(true);
      this.pipetteView = pipetteView;
      view.setOnTouchListener(this);
      this.pipetteView.setColor(event.color);
      this.pipetteView.open();
      mView.showActionBar(false);
    } else {
      view.setDrawingCacheEnabled(false);
      view.setOnTouchListener(null);
      mView.showActionBar(true);
      if (event.color != PipetteView.CANCEL) {
        mView.pipetteResult(event.color);
      } else {
        mView.showFab(true);
      }
    }
  }

  @Override public boolean onTouch(View view, MotionEvent me) {
    if (me.getAction() == MotionEvent.ACTION_DOWN) {
      try {
        int color = view.getDrawingCache().getPixel((int) me.getX(), (int) me.getY());
        if (color != PipetteView.CANCEL) pipetteView.setColor(color);
      } catch (IllegalArgumentException e) {
        CrashLog.logError("getPixel", e);
      }
      return true;
    }
    return false;
  }

  @Override public void onTabSelected(int p) {
    if (p != mViewPager.getCurrentItem()) mViewPager.setCurrentItem(p, true);
    if (!isToolOpen()) openTool();
  }

  @Override public void onTabReSelected(int p) {
    if (!isToolOpen()) openTool();
  }

  @Override public void onPageSelected(int p) {
    mBottomBar.selectTabAtPosition(p, true);
  }

  @Override public void onPageScrollStateChanged(int i) { /*no-op*/ }

  @Override public void onPageScrolled(int i, float f, int i2) { /*no-op*/ }

  void openTool() {
    mView.closePipette();
    AnimUtils.translateY(flBottom, flBottom.getHeight(), 0);
    mBottomBar.hideShadow();
    mView.showFab(false);
  }

  void showShadowBottomBar() {
    View view = ButterKnife.findById(mBottomBar, R.id.bb_bottom_bar_shadow);
    if (view != null) view.setVisibility(View.VISIBLE);
  }

  ///////////////////////////////////////////////////////
  void showDialog(String sImagePath, @NonNull Template template) {
    AlertDialog dialog = new AlertDialog.Builder(mView.context()).create();
    dialog.setTitle(R.string.what_image);
    dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.background),
        new DialogOnClick(sImagePath, true, template));
    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.screen),
        new DialogOnClick(sImagePath, false, template));
    dialog.show();
  }

  BottomBar bottomBar(AppCompatActivity activity, Bundle saveState) {
    final int[][] sResource = {
        { R.drawable.ic_phone_android_black_24dp, R.string.screen },
        { R.drawable.ic_image_black_24dp, R.string.background },
        { R.drawable.ic_style_black_24dp, R.string.badge }
    };
    final int count = sResource.length;
    BottomBarTab[] barTabs = new BottomBarTab[count];
    for (int i = 0; i < count; i++) {
      Drawable icon = ResUtils.getVectorDrawable(activity, sResource[i][0]);
      barTabs[i] = new BottomBarTab(icon, sResource[i][1]);
    }
    BottomBar bottomBar = BottomBar.attach(activity, saveState);
    bottomBar.setItems(barTabs);
    bottomBar.setOnTabClickListener(this);
    bottomBar.selectTabAtPosition(0, false);
    return bottomBar;
  }

  String getString(@StringRes int resId) {
    return mView.context().getString(resId);
  }

  class DialogOnClick implements DialogInterface.OnClickListener {
    private final String imagePath;
    private final boolean isBackground;
    private final Template template;

    private DialogOnClick(String imagePath, boolean isBackground, Template template) {
      this.imagePath = imagePath;
      this.isBackground = isBackground;
      this.template = template;
    }

    @Override public void onClick(DialogInterface dialog, int i) {
      mView.setImageReceiveTemplate(new ImageReceive(imagePath, isBackground), template);
      dialog.dismiss();
    }
  }
}
