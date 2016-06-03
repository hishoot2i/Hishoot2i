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
import java.io.File;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.BottomBarOnTabClickListener;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.FileUtils;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.MainActivityView;
import org.illegaller.ratabb.hishoot2i.view.adapter.ToolFragmentAdapter;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;
import rx.Subscription;

public class MainActivityPresenter extends BasePresenter<MainActivityView> {
  private final ViewPagerChangeListener mViewPagerChange = new ViewPagerChangeListener();
  private final MainBottomBarOnTab mBottomBarOnTab = new MainBottomBarOnTab();
  @Inject TemplateManager mTemplateManager;
  private ViewPager mViewPager;
  private BottomBar mBottomBar;
  private View mViewBottom;
  private Subscription mSubscription;

  @Inject public MainActivityPresenter() {
  }

  public void onSaveInstanceState(Bundle outState) {
    mBottomBar.onSaveInstanceState(outState);
  }

  @Override public void detachView() {
    this.mViewPager.removeOnPageChangeListener(mViewPagerChange);
    this.mBottomBar = null;
    this.mViewPager = null;
    this.mViewBottom = null;
    if (mSubscription != null) mSubscription.unsubscribe();
    super.detachView();
  }

  public void setup(AppCompatActivity activity, Bundle saveState) {
    this.mViewBottom = ButterKnife.findById(activity, R.id.flBottom);
    this.mViewPager = ButterKnife.findById(activity, R.id.viewPager);
    this.mViewPager.setAdapter(new ToolFragmentAdapter(activity.getSupportFragmentManager()));
    this.mViewPager.addOnPageChangeListener(mViewPagerChange);
    this.mBottomBar = bottomBar(activity, saveState);
    openTool();
  }

  public void handleImageReceive(Uri imageUri, final String templateId) {
    String stringUri = FileUtils.getPath(getView().getContext(), imageUri);
    final String sImagePath = UILHelper.stringFiles(new File(stringUri));
    mSubscription = mTemplateManager.getTemplateList(TemplateManager.NO_FAV)
        .compose(SimpleSchedule.schedule())
        .subscribe(t -> showDialog(sImagePath, mTemplateManager.getTemplateById(templateId)));
  }

  public boolean isToolOpen() {
    return mViewBottom.getTranslationY() == 0;
  }

  public void closeTool(boolean needPerform) {
    AnimUtils.translateY(mViewBottom, 0, mViewBottom.getHeight());
    showShadowBottomBar();
    getView().showFab(needPerform);
    if (needPerform) getView().perform();
  }

  public void perform(boolean isSaving, DataImagePath dataImagePath, Template template) {
    getView().showProgress(true);
    getView().showFab(false);
    if (isSaving) {
      HishootService.startActionSave(getView().getContext(), dataImagePath, template);
    } else {
      HishootService.startActionPreview(getView().getContext(), dataImagePath, template);
    }
  }

  public void pipette(EventPipette event, View view, PipetteView pipetteView) {
    if (event.isShow) {
      closeTool(false);
      view.setDrawingCacheEnabled(true);
      view.setOnTouchListener((v, me) -> {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
          try {
            int color = v.getDrawingCache().getPixel((int) me.getX(), (int) me.getY());
            if (color != PipetteView.CANCEL) pipetteView.setColor(color);
          } catch (IllegalArgumentException e) {
            CrashLog.logError("getPixel", e);
          }
          return true;
        }
        return false;
      });
      pipetteView.setColor(event.color);
      pipetteView.open();
      getView().showActionBar(false);
    } else {
      view.setDrawingCacheEnabled(false);
      view.setOnTouchListener(null);
      getView().showActionBar(true);
      if (event.color != PipetteView.CANCEL) {
        getView().pipetteResult(event.color);
      } else {
        getView().showFab(true);
      }
    }
  }

  void openTool() {
    getView().closePipette();
    AnimUtils.translateY(mViewBottom, mViewBottom.getHeight(), 0);
    mBottomBar.hideShadow();
    getView().showFab(false);
  }

  void showShadowBottomBar() {
    View view = ButterKnife.findById(mBottomBar, R.id.bb_bottom_bar_shadow);
    if (view != null) view.setVisibility(View.VISIBLE);
  }

  ///////////////////////////////////////////////////////
  void showDialog(String sImagePath, @NonNull Template template) {
    AlertDialog dialog = new AlertDialog.Builder(getView().getContext()).create();
    dialog.setTitle(R.string.what_image);
    dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.background),
        (dialogInterface, i) -> {
          getView().setImageReceiveTemplate(new ImageReceive(sImagePath, true), template);
          dialogInterface.dismiss();
        });
    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.screen),
        (dialogInterface, i) -> {
          getView().setImageReceiveTemplate(new ImageReceive(sImagePath, false), template);
          dialogInterface.dismiss();
        });
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
    bottomBar.setOnTabClickListener(mBottomBarOnTab);
    bottomBar.selectTabAtPosition(0, false);
    return bottomBar;
  }

  String getString(@StringRes int resId) {
    return getView().getContext().getString(resId);
  }

  private class ViewPagerChangeListener extends ViewPager.SimpleOnPageChangeListener {
    @Override public void onPageSelected(int position) {
      if (mBottomBar == null) return;
      mBottomBar.selectTabAtPosition(position, true);
    }
  }

  private class MainBottomBarOnTab extends BottomBarOnTabClickListener {

    @Override public void onTabSelected(int position) {
      if (mViewPager == null) return;
      if (position != mViewPager.getCurrentItem()) {
        mViewPager.setCurrentItem(position, true); //FIXME : #228
      }
      if (!isToolOpen()) openTool();
    }

    @Override public void onTabReSelected(int position) {
      if (!isToolOpen()) openTool();
    }
  }
}
