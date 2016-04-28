package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.app.Activity;
import android.content.DialogInterface;
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
import javax.inject.Named;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ToolFragmentComponent;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_INT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_RADIUS;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_CROP_ENABLE;

public class BackgroundToolFragment extends BaseToolFragment
    implements SeekBar.OnSeekBarChangeListener, ColorPickerDialog.ColorChangeListener {
  public static final int REQ_IMAGE_BG = 0x03;
  public static final int REQ_IMAGE_CROP_BG = 0x04;
  @BindView(R.id.cbImage) SwitchCompat cbImage;
  @BindView(R.id.viewFlipper) ViewFlipper viewFlipper;
  @BindView(R.id.cbBlurBg) SwitchCompat cbBlur;
  @BindView(R.id.seekBar_blur_radius) AppCompatSeekBar sbBlurRadius;
  @BindView(R.id.cpfMixer) CircleButton cpfMixer;
  @BindView(R.id.cpfPipette) CircleButton cpfPipette;
  @BindView(R.id.cbCrop) AppCompatCheckBox cbCrop;
  @Inject @Named(BG_COLOR_ENABLE) BooleanTray colorEnableTray;
  @Inject @Named(BG_IMAGE_BLUR_ENABLE) BooleanTray blurEnableTray;
  @Inject @Named(BG_COLOR_INT) IntTray colorTray;
  @Inject @Named(BG_IMAGE_BLUR_RADIUS) IntTray blurRadiusTray;
  @Inject @Named(BG_IMAGE_CROP_ENABLE) BooleanTray cropEnableTray;
  private Point bgPoint = new Point(256, 256);

  public BackgroundToolFragment() {
  }

  public static BackgroundToolFragment newInstance() {
    Bundle args = new Bundle();
    BackgroundToolFragment fragment = new BackgroundToolFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected void setupComponent(ToolFragmentComponent component) {
    component.inject(this);
  }

  @Override int getLayoutRes() {
    return R.layout.bottom_tool_background;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean isBgColor = colorEnableTray.get();
    boolean isImageBlur = blurEnableTray.get();
    int color = colorTray.get();
    cpfMixer.setColor(color);
    cpfPipette.setColor(color);
    cbImage.setChecked(!isBgColor);
    cbCrop.setChecked(cropEnableTray.get());
    cbBlur.setChecked(isImageBlur);
    sbBlurRadius.setEnabled(isImageBlur);
    sbBlurRadius.setProgress(blurRadiusTray.get());
    sbBlurRadius.setOnSeekBarChangeListener(this);
  }

  @Override public void onProgressChanged(SeekBar sb, int i, boolean b) { /*no-op*/ }

  @Override public void onStartTrackingTouch(SeekBar sb) { /*no-op*/ }

  @Override public void onStopTrackingTouch(SeekBar sb) {
    blurRadiusTray.set(sb.getProgress());
  }

  @OnCheckedChanged({
      R.id.cbImage, R.id.cbBlurBg, R.id.cbCrop
  }) void onCheckChange(CompoundButton cb, boolean check) {
    if (cb == cbImage) {
      colorEnableTray.set(!check);
      vfDisplay(!check);
    } else if (cb == cbBlur) {
      blurEnableTray.set(check);
      sbBlurRadius.setEnabled(check);
    } else if (cb == cbCrop) cropEnableTray.set(check);
  }

  @Override public void onColorChange(DialogInterface dialog, int color) {
    colorTray.set(color);
    cpfMixer.setColor(color);
    cpfPipette.setColor(color);
    EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.NONE, ""));
  }

  @OnClick({
      R.id.img_config_bg, R.id.cpfMixer, R.id.cpfPipette
  }) void onClick(View view) {
    if (view == cpfMixer) {
      new ColorPickerDialog.Builder().colorInit(colorTray.get())
          .listener(this)
          .create()
          .show(getFragmentManager(), ColorPickerDialog.TAG);
    } else if (view == cpfPipette) {
      EventBus.getDefault().post(new EventPipette(true, colorTray.get()));
    } else if (view.getId() == R.id.img_config_bg) {
      boolean isCrop = cropEnableTray.get();
      if (isCrop) bgPoint = getPointBackground();
      Utils.openImagePicker(this, "Background", isCrop ? REQ_IMAGE_CROP_BG : REQ_IMAGE_BG);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    if (requestCode == REQ_IMAGE_CROP_BG) {
      String imagePath = Utils.getStringFromUri(getActivity(), data.getData());
      Intent intent =
          CropActivity.getIntent(getActivity(), UILHelper.stringFiles(new File(imagePath)),
              bgPoint);
      this.startActivityForResult(intent, REQ_IMAGE_BG);
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
}
