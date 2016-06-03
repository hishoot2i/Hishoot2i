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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ViewFlipper;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import java.io.File;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.FileUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialogBuilder;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

public class BackgroundToolFragment extends BaseFragment
    implements SeekBar.OnSeekBarChangeListener {
  public static final int REQ_IMAGE_BG = 0x03;
  public static final int REQ_IMAGE_CROP_BG = 0x04;
  @BindView(R.id.cbImage) SwitchCompat cbImage;
  @BindView(R.id.viewFlipper) ViewFlipper viewFlipper;
  @BindView(R.id.cbBlurBg) SwitchCompat cbBlur;
  @BindView(R.id.seekBar_blur_radius) AppCompatSeekBar sbBlurRadius;
  @BindView(R.id.cpfMixer) CircleButton cpfMixer;
  @BindView(R.id.cpfPipette) CircleButton cpfPipette;
  @BindView(R.id.cbCrop) AppCompatCheckBox cbCrop;

  @Inject TrayManager mTrayManager;
  private BooleanTray mColorEnableTray;
  private BooleanTray mBlurEnableTray;
  private IntTray mColorTray;
  private IntTray mBlurRadiusTray;
  private BooleanTray mCropEnableTray;
  private Point mBackgroundPoint = new Point(256, 256);

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
    getActivityComponent().inject(this);
    mColorEnableTray = mTrayManager.getBackgroundColorEnable();
    mBlurEnableTray = mTrayManager.getBackgroundImageBlurEnable();
    mColorTray = mTrayManager.getBackgroundColorInt();
    mBlurRadiusTray = mTrayManager.getBackgroundImageBlurRadius();
    mCropEnableTray = mTrayManager.getBackgroundImageCrop();
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
    sbBlurRadius.setOnSeekBarChangeListener(this);
  }

  @Override public void onProgressChanged(SeekBar sb, int i, boolean b) { /*no-op*/ }

  @Override public void onStartTrackingTouch(SeekBar sb) { /*no-op*/ }

  @Override public void onStopTrackingTouch(SeekBar sb) {
    mBlurRadiusTray.setValue(sb.getProgress());
  }

  @OnCheckedChanged({
      R.id.cbImage, R.id.cbBlurBg, R.id.cbCrop
  }) void onCheckChange(CompoundButton cb, boolean check) {
    if (cb == cbImage) {
      mColorEnableTray.setValue(!check);
      vfDisplay(!check);
    } else if (cb == cbBlur) {
      mBlurEnableTray.setValue(check);
      sbBlurRadius.setEnabled(check);
    } else if (cb == cbCrop) mCropEnableTray.setValue(check);
  }

  @OnClick({ R.id.img_config_bg, R.id.cpfMixer, R.id.cpfPipette }) void onClick(View view) {
    if (view == cpfMixer) {
      ColorPickerDialogBuilder.create()
          .colorInit(mColorTray.getValue())
          .listener((dialog, color) -> {
            mColorTray.setValue(color);
            cpfMixer.setColor(color);
            cpfPipette.setColor(color);
            EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.NONE, ""));
          })
          .build()
          .show(getFragmentManager(), ColorPickerDialog.TAG);
    } else if (view == cpfPipette) {
      EventBus.getDefault().post(new EventPipette(true, mColorTray.getValue()));
    } else if (view.getId() == R.id.img_config_bg) {
      boolean isCrop = mCropEnableTray.isValue();
      if (isCrop) mBackgroundPoint = getPointBackground();
      Utils.openImagePicker(this, "Background", isCrop ? REQ_IMAGE_CROP_BG : REQ_IMAGE_BG);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    if (requestCode == REQ_IMAGE_CROP_BG) {
      String imagePath = null;
      try {
        imagePath = FileUtils.getPath(getActivity(), data.getData());
      } catch (Exception e) {
        CrashLog.logError("imagePath null", e);
      }
      if (imagePath == null) {
        /*cancel cropping :/*/
        EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.BG, data.getDataString()));
      } else {
        Intent intent =
            CropActivity.getIntent(getActivity(), UILHelper.stringFiles(new File(imagePath)),
                mBackgroundPoint);
        this.startActivityForResult(intent, REQ_IMAGE_BG);
      }
    } else if (requestCode == REQ_IMAGE_BG) {
      EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.BG, data.getDataString()));
    }
  }

  Point getPointBackground() {
    return ((MainActivity) getActivity()).pointBackgroundTemplate();
  }

  void vfDisplay(boolean isBgColor) {
    viewFlipper.setInAnimation(AnimUtils.animTranslateY(1F, 0F));
    viewFlipper.setOutAnimation(AnimUtils.animTranslateY(0F, -1F));
    viewFlipper.setDisplayedChild(isBgColor ? 0 : 1);
  }

  @Override protected int layoutRes() {
    return R.layout.bottom_tool_background;
  }
}
