package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ViewFlipper;
import butterknife.BindView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import java.io.File;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.animTranslateY;
import static org.illegaller.ratabb.hishoot2i.utils.FileUtils.getRealPath;
import static org.illegaller.ratabb.hishoot2i.utils.UILHelper.stringFiles;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.openImagePicker;

public class BackgroundToolFragment extends BaseFragment {
  private static final int REQ_IMAGE_BG = 0x03;
  private static final int REQ_IMAGE_CROP_BG = 0x04;
  @BindView(R.id.cbImage) SwitchCompat cbImage;
  @BindView(R.id.viewFlipper) ViewFlipper viewFlipper;
  @BindView(R.id.cbBlurBg) SwitchCompat cbBlur;
  @BindView(R.id.seekBar_blur_radius) AppCompatSeekBar sbBlurRadius;
  @BindView(R.id.cpfMixer) CircleButton cpfMixer;
  @BindView(R.id.cpfPipette) CircleButton cpfPipette;
  @BindView(R.id.cbCrop) AppCompatCheckBox cbCrop;
  @BindView(R.id.pick_image_background) View mPickImageBackground;
  @Inject TrayManager mTrayManager;
  private Subscription mSubscription;
  private BooleanTray mColorEnableTray;
  private BooleanTray mBlurEnableTray;
  private IntTray mColorTray;
  private IntTray mBlurRadiusTray;
  private BooleanTray mCropEnableTray;

  public BackgroundToolFragment() {
  }

  public static BackgroundToolFragment newInstance() {
    Bundle args = new Bundle();
    BackgroundToolFragment fragment = new BackgroundToolFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mColorEnableTray = mTrayManager.getBackgroundColorEnable();
    mBlurEnableTray = mTrayManager.getBackgroundImageBlurEnable();
    mColorTray = mTrayManager.getBackgroundColorInt();
    mBlurRadiusTray = mTrayManager.getBackgroundImageBlurRadius();
    mCropEnableTray = mTrayManager.getBackgroundImageCrop();
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override public void onDestroyView() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean isBgColor = mColorEnableTray.isValue();
    boolean isImageBlur = mBlurEnableTray.isValue();
    int color = mColorTray.getValue();
    cpfMixer.setColor(color);
    cpfPipette.setColor(color);
    cbImage.setChecked(!isBgColor);
    cbCrop.setChecked(mCropEnableTray.isValue());
    cbBlur.setChecked(isImageBlur);
    sbBlurRadius.setEnabled(isImageBlur);
    sbBlurRadius.setProgress(mBlurRadiusTray.getValue());
    mSubscription = viewSubscription();
  }

  private Subscription viewSubscription() {
    final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    mCompositeSubscription.add(RxSeekBar.changes(sbBlurRadius).subscribe(progress -> {
      mBlurRadiusTray.setValue(progress);
    }, CrashLog::logError));

    mCompositeSubscription.add(RxView.clicks(cpfMixer).subscribe(click -> {
      ColorPickerDialog.Builder.build(mColorTray.getValue(), ((dialog, color) -> {
        mColorTray.setValue(color);
        cpfMixer.setColor(color);
        cpfPipette.setColor(color);
        EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.NONE, ""));
      })).show(getFragmentManager());
    }, CrashLog::logError));

    mCompositeSubscription.add(RxView.clicks(cpfPipette).subscribe(click -> {
      EventBus.getDefault().post(new EventPipette(true, mColorTray.getValue()));
    }, CrashLog::logError));

    mCompositeSubscription.add(RxView.clicks(mPickImageBackground).subscribe(click -> {
      boolean isCrop = mCropEnableTray.isValue();
      openImagePicker(this, "Background", isCrop ? REQ_IMAGE_CROP_BG : REQ_IMAGE_BG);
    }, CrashLog::logError));

    mCompositeSubscription.add(RxCompoundButton.checkedChanges(cbImage).subscribe(check -> {
      mColorEnableTray.setValue(!check);
      vfDisplay(!check);
    }, CrashLog::logError));

    mCompositeSubscription.add(RxCompoundButton.checkedChanges(cbBlur).subscribe(check -> {
      mBlurEnableTray.setValue(check);
      sbBlurRadius.setEnabled(check);
    }, CrashLog::logError));

    mCompositeSubscription.add(RxCompoundButton.checkedChanges(cbCrop).subscribe(check -> {
      mCropEnableTray.setValue(check);
    }, CrashLog::logError));
    return mCompositeSubscription;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    if (requestCode == REQ_IMAGE_CROP_BG) {
      String imagePath = null;
      try {
        imagePath = getRealPath(getContext(), data.getData());
      } catch (Exception e) {
        CrashLog.logError(e);
      }
      if (imagePath == null) { /*cancel cropping :/*/
        EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.BG, data.getDataString()));
      } else {
        final Point mBackgroundPoint = ((MainActivity) getContext()).pointBackgroundTemplate();
        Intent intent = CropActivity.getIntent(getContext(), stringFiles(new File(imagePath)),
            mBackgroundPoint);
        this.startActivityForResult(intent, REQ_IMAGE_BG);
      }
    } else if (requestCode == REQ_IMAGE_BG) {
      EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.BG, data.getDataString()));
    }
  }

  private void vfDisplay(boolean isBgColor) {
    viewFlipper.setInAnimation(animTranslateY(1F, 0F));
    viewFlipper.setOutAnimation(animTranslateY(0F, -1F));
    viewFlipper.setDisplayedChild(isBgColor ? 0 : 1);
  }

  @Override protected int layoutRes() {
    return R.layout.bottom_tool_background;
  }
}
