package org.illegaller.ratabb.hishoot2i.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding.view.RxView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.MainActivityView;
import org.illegaller.ratabb.hishoot2i.view.adapter.ToolFragmentAdapter;
import org.illegaller.ratabb.hishoot2i.view.common.BasePresenter;
import org.illegaller.ratabb.hishoot2i.view.rx.BottomBarTabEvent;
import org.illegaller.ratabb.hishoot2i.view.rx.RxBottomBar;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import org.illegaller.ratabb.hishoot2i.view.widget.PipetteView;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.translateY;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getVectorDrawable;
import static org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule.schedule;

public class MainActivityPresenter extends BasePresenter<MainActivityView> {
  @Inject TemplateManager mTemplateManager;
  private Subscription mSubscription;

  @Inject MainActivityPresenter() {
  }

  @Override public void detachView() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
    super.detachView();
  }

  public void setup(AppCompatActivity activity, Bundle saveState) {
    getMvpView().getViewPager()
        .setAdapter(new ToolFragmentAdapter(activity.getSupportFragmentManager()));
    final BottomBar bottomBar = bottomBar(activity, saveState);
    mSubscription = viewSubscription(bottomBar);
    getMvpView().setBottomBar(bottomBar);
    openTool();
    final Intent intent = getMvpView().getIntent();
    if (Intent.ACTION_SEND.equals(intent.getAction())) {
      imageReceive(intent.getParcelableExtra(Intent.EXTRA_STREAM));
    }
  }

  /**
   * we don't have template, so ask TemplateManager to populate list first, and
   * find by id from saved Tray
   *
   * @param imageUri create receive
   */
  private void imageReceive(Uri imageUri) {
    addAutoUnSubscribe(mTemplateManager.getTemplateList(TemplateManager.NO_FAV)
        .compose(schedule())
        .subscribe(templateList -> {
          final String templateID = getMvpView().getTrayManager().getTemplateID().getValue();
          final Template template = mTemplateManager.getTemplateById(templateID);
          AlertDialog dialog = new AlertDialog.Builder(getMvpView().getContext()).create();
          dialog.setTitle(R.string.what_image);
          dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.background),
              (dialogInterface, i) -> {
                getMvpView().setImageReceiveTemplate(new ImageReceive(imageUri, true), template);
                dialogInterface.dismiss();
              });
          dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.screen),
              (dialogInterface, i) -> {
                getMvpView().setImageReceiveTemplate(new ImageReceive(imageUri, false), template);
                dialogInterface.dismiss();
              });
          dialog.show();
        }, CrashLog::logError));
  }

  private Subscription viewSubscription(BottomBar bottomBar) {
    final CompositeSubscription subscription = new CompositeSubscription();
    subscription.add(RxViewPager.pageSelections(getMvpView().getViewPager()).subscribe(position -> {
      if (getMvpView().getBottomBar() == null) return;
      getMvpView().getBottomBar().selectTabAtPosition(position, true);
    }, CrashLog::logError));
    subscription.add(RxView.clicks(getMvpView().getFabSave()).subscribe(click -> {
      startServiceSave();
    }, CrashLog::logError));
    subscription.add(RxView.clicks(getMvpView().getViewBottom()).subscribe(click -> {
      closeTool(true);
    }, CrashLog::logError));

    subscription.add(RxBottomBar.tabEvent(bottomBar).subscribe(event -> {
          final BottomBarTabEvent.Kind kind = event.getKind();
          final int position = event.getPosition();
          if (kind == BottomBarTabEvent.Kind.SELECT) {
            final ViewPager viewPager = getMvpView().getViewPager();
            if (viewPager == null) return;
            if (position != viewPager.getCurrentItem()) {
              viewPager.setCurrentItem(position, true);
            }
            openToolsIfClosed();
          } else if (kind == BottomBarTabEvent.Kind.RESELECT) {
            openToolsIfClosed();
          }
        }, CrashLog::logError

    ));
    return subscription;
  }

  private void openToolsIfClosed() {
    if (!getMvpView().isToolOpen()) openTool();
  }

  public void closeTool(boolean needPerform) {
    translateY(getMvpView().getViewBottom(), 0, getMvpView().getViewBottom().getHeight());
    showShadowBottomBar();
    getMvpView().showFab(needPerform);
    if (needPerform) startServicePreview();
  }

  private void startServiceSave() {
    startService(true);
  }

  private void startServicePreview() {
    startService(false);
  }

  private void startService(boolean isSaving) {
    checkViewAttached();
    getMvpView().showProgress(true);
    getMvpView().showFab(false);
    final DataImagePath dataImagePath = getMvpView().getDataImagePath();
    final Template template = getMvpView().getTemplate();
    final Context context = getMvpView().getContext();
    if (isSaving) {
      HishootService.startActionSave(context, dataImagePath, template);
    } else {
      HishootService.startActionPreview(context, dataImagePath, template);
    }
  }

  public void pipette(EventPipette event) {
    final ImageView view = getMvpView().getImageView();
    final PipetteView pipetteView = getMvpView().getPipetteView();
    if (event.isShow) {
      closeTool(false);
      view.setDrawingCacheEnabled(true);
      view.setOnTouchListener((v, me) -> {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
          try {
            int color = v.getDrawingCache().getPixel((int) me.getX(), (int) me.getY());
            if (color != PipetteView.CANCEL) pipetteView.setColor(color);
          } catch (IllegalArgumentException iae) {
            CrashLog.logError(iae);
          }
          return true;
        }
        return false;
      });
      pipetteView.setColor(event.color);
      pipetteView.open();
      getMvpView().getSupportActionBar().hide();
    } else {
      view.setDrawingCacheEnabled(false);
      view.setOnTouchListener(null);
      getMvpView().getSupportActionBar().show();
      if (event.color != PipetteView.CANCEL) {
        getMvpView().getTrayManager().getBackgroundColorInt().setValue(event.color);
        getCircleButton(R.id.cpfMixer).setColor(event.color);
        getCircleButton(R.id.cpfPipette).setColor(event.color);
        startServicePreview();
      } else {
        getMvpView().showFab(true);
      }
    }
  }

  private CircleButton getCircleButton(@IdRes int id) {
    return ButterKnife.findById(getMvpView().getViewBottom(), id);
  }

  private void openTool() {
    getMvpView().getPipetteView().close(true);
    translateY(getMvpView().getViewBottom(), getMvpView().getViewBottom().getHeight(), 0);
    getMvpView().getBottomBar().hideShadow();
    getMvpView().showFab(false);
  }

  private void showShadowBottomBar() {
    View view = ButterKnife.findById(getMvpView().getBottomBar(), R.id.bb_bottom_bar_shadow);
    if (view != null) view.setVisibility(View.VISIBLE);
  }

  private BottomBar bottomBar(AppCompatActivity activity, Bundle saveState) {
    final int[][] sResource = {
        { R.drawable.ic_phone_android_black_24dp, R.string.screen },
        { R.drawable.ic_image_black_24dp, R.string.background },
        { R.drawable.ic_style_black_24dp, R.string.badge }
    };
    final int count = sResource.length;
    final BottomBarTab[] barTabs = new BottomBarTab[count];
    for (int i = 0; i < count; i++) {
      Drawable icon = getVectorDrawable(activity, sResource[i][0]);
      barTabs[i] = new BottomBarTab(icon, sResource[i][1]);
    }
    final BottomBar result = BottomBar.attach(activity, saveState);
    result.setItems(barTabs);
    return result;
  }

  private String getString(@StringRes int resId) {
    return getMvpView().getContext().getString(resId);
  }
}
