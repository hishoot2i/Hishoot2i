package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.events.EventMainSetImage;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ScreenToolFragment extends BaseToolFragment {
    public static final int REQ_IMAGE_PIC_SS1 = 0x01;
    public static final int REQ_IMAGE_PIC_SS2 = 0x02;
    @Bind(R.id.cbDoubleSS) SwitchCompat cbDoubleSS;
    @Bind(R.id.img_config_ss1) CircleButton imConfig1;
    @Bind(R.id.img_config_ss2) CircleButton imConfig2;
    @Bind(R.id.swShadow) SwitchCompat swShadow;
    @Bind(R.id.swGlare) SwitchCompat swGlare;
    @Bind(R.id.layoutSS2) View lSS2;
    private TrayManager mTrayManager;


    public ScreenToolFragment() {
    }

    public static ScreenToolFragment newInstance() {
        Bundle args = new Bundle();
        ScreenToolFragment fragment = new ScreenToolFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override int getLayoutRes() {
        return R.layout.bottom_tool_screen;
    }

    @Override protected void onView() {
        boolean isDouble = mTrayManager.getSsDoubleEnableTray().get();
        cbDoubleSS.setChecked(isDouble);
        lSS2.setVisibility(isDouble ? View.VISIBLE : View.GONE);
        swGlare.setChecked(mTrayManager.getGlareEnableTray().get());
        swShadow.setChecked(mTrayManager.getShadowEnableTray().get());
    }

    @Override protected void setTrayManager(AppComponent appComponent) {
        mTrayManager = appComponent.trayManager();
    }

    @OnCheckedChanged({R.id.cbDoubleSS, R.id.swGlare, R.id.swShadow}) void onCheckedChanged(
            CompoundButton cb, boolean check) {
        if (cb == cbDoubleSS) mTrayManager.getSsDoubleEnableTray().set(check);
        else if (cb == swGlare) mTrayManager.getGlareEnableTray().set(check);
        else if (cb == swShadow) mTrayManager.getShadowEnableTray().set(check);
        onView();
    }

    @OnClick({R.id.img_config_ss1, R.id.img_config_ss2}) void onClick(View view) {
        if (view == imConfig1) Utils.openImagePicker(this, "Screen 1", REQ_IMAGE_PIC_SS1);
        else if (view == imConfig2) Utils.openImagePicker(this, "Screen 2", REQ_IMAGE_PIC_SS2);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        final String dataString = data.getDataString();
        EventMainSetImage.WhatImage whatImage = null;
        if (requestCode == REQ_IMAGE_PIC_SS1)
            whatImage = EventMainSetImage.WhatImage.SS1;
        else if (requestCode == REQ_IMAGE_PIC_SS2)
            whatImage = EventMainSetImage.WhatImage.SS2;

        if (whatImage != null) {
            EventBus.getDefault().post(new EventMainSetImage(whatImage, dataString));
        }
    }
}
