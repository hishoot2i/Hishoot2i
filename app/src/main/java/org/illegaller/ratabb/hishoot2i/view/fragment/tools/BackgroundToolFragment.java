package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.events.EventMainSetImage;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import java.io.File;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class BackgroundToolFragment extends BaseToolFragment
        implements SeekBar.OnSeekBarChangeListener {
    public static final int REQ_IMAGE_BG = 0x03;
    public static final int REQ_IMAGE_CROP_BG = 0x04;
    @Bind(R.id.cbImage) SwitchCompat cbImage;
    @Bind(R.id.layout_bg_image) View vBgImage;
    @Bind(R.id.layout_bg_color) View vBgColor;
    @Bind(R.id.cbBlurBg) SwitchCompat cbBlur;
    @Bind(R.id.seekBar_blur_radius) AppCompatSeekBar sbBlurRadius;
    @Bind(R.id.cpBackground) CircleButton cpBackground;
    @Bind(R.id.cbCrop) AppCompatCheckBox cbCrop;
    Point bgPoint = new Point(256, 256);
    private TrayManager mTrayManager;

    public BackgroundToolFragment() {
    }

    public static BackgroundToolFragment newInstance() {
        Bundle args = new Bundle();
        BackgroundToolFragment fragment = new BackgroundToolFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override int getLayoutRes() {
        return R.layout.bottom_tool_background;
    }

    @Override protected void onView() {
        boolean isBgColor = mTrayManager.getBgColorEnableTray().get();
        boolean isImageBlur = mTrayManager.getBgImageBlurEnableTray().get();
        cpBackground.setColor(mTrayManager.getBgColorIntTray().get());
        cbImage.setChecked(!isBgColor);
        cbCrop.setChecked(mTrayManager.getBgImageCropEnableTray().get());
        vBgColor.setVisibility(isBgColor ? View.VISIBLE : View.GONE);
        vBgImage.setVisibility(!isBgColor ? View.VISIBLE : View.GONE);
        cbBlur.setChecked(isImageBlur);
        sbBlurRadius.setEnabled(isImageBlur);
        sbBlurRadius.setProgress(mTrayManager.getBgImageBlurRadiusTray().get());
        sbBlurRadius.setOnSeekBarChangeListener(this);
    }

    @Override protected void setTrayManager(AppComponent appComponent) {
        mTrayManager = appComponent.trayManager();
    }

    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) { //no-op
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {//no-op
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        mTrayManager.getBgImageBlurRadiusTray().set(seekBar.getProgress());
    }

    @OnCheckedChanged({R.id.cbImage, R.id.cbBlurBg, R.id.cbCrop}) void onCheckChange(
            CompoundButton cb, boolean check) {
        if (cb == cbImage) mTrayManager.getBgColorEnableTray().set(!check);
        else if (cb == cbBlur) mTrayManager.getBgImageBlurEnableTray().set(check);
        else if (cb == cbCrop) mTrayManager.getBgImageCropEnableTray().set(check);
        onView();
    }

    @OnClick({R.id.img_config_bg, R.id.cpBackground}) void onClick(View view) {
        if (view == cpBackground) {
            final int c = mTrayManager.getBgColorIntTray().get();
            ColorPickerDialog dialog = new ColorPickerDialog.Builder()
                    .initColor(c).listener(new ColorPickerDialog.Listener() {
                        @Override public void onChange(int color) {
                            mTrayManager.getBgColorIntTray().set(color);
                            cpBackground.setColor(color);
                        }
                    }).create();
            dialog.show(getFragmentManager(), ColorPickerDialog.TAG);
        } else if (view.getId() == R.id.img_config_bg) {
            boolean isCrop = mTrayManager.getBgImageCropEnableTray().get();
            if (isCrop) bgPoint = getPointBackground();
            Utils.openImagePicker(this, "Background", isCrop ? REQ_IMAGE_CROP_BG : REQ_IMAGE_BG);
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQ_IMAGE_CROP_BG) {
            String imagePath = Utils.getStringFromUri(getActivity(), data.getData());
            Intent intent = CropActivity.getIntent(getActivity(),
                    UILHelper.stringFiles(new File(imagePath)), bgPoint);
            this.startActivityForResult(intent, REQ_IMAGE_BG);
        } else if (requestCode == REQ_IMAGE_BG) {
            EventBus.getDefault().post(
                    new EventMainSetImage(EventMainSetImage.WhatImage.BG, data.getDataString()));
        }
    }

    Point getPointBackground() {
        MainActivity mainActivity = (MainActivity) getActivity();
        Point point0 = mainActivity.pointBackgroundTemplate();
        Point point = new Point(point0.x, point0.y);
        CrashLog.log(point.toString());
        return point;
    }
}
