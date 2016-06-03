package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

public class ScreenToolFragment extends BaseFragment {
  static final int REQ_IMAGE_PIC_SS1 = 0x01;
  static final int REQ_IMAGE_PIC_SS2 = 0x02;
  @BindView(R.id.cbfScreen1) CircleButton cbfScreen1;
  @BindView(R.id.cbfScreen2) CircleButton cbfScreen2;
  @BindView(R.id.scDoubleSS) SwitchCompat scDoubleSS;
  @BindView(R.id.scShadow) SwitchCompat scShadow;
  @BindView(R.id.scGlare) SwitchCompat scGlare;
  @BindView(R.id.scFrame) SwitchCompat scFrame;
  @BindView(R.id.layoutSS2) View layoutSS2;
  @Inject TrayManager mTrayManager;
  private BooleanTray mDoubleEnableTray;
  private BooleanTray mGlareEnableTray;
  private BooleanTray mShadowEnableTray;
  private BooleanTray mFrameEnableTray;

  public ScreenToolFragment() {
  }

  public static ScreenToolFragment newInstance() {
    Bundle args = new Bundle();
    ScreenToolFragment fragment = new ScreenToolFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected int layoutRes() {
    return R.layout.bottom_tool_screen;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    mDoubleEnableTray = mTrayManager.getDoubleEnable();
    mGlareEnableTray = mTrayManager.getGlareEnable();
    mShadowEnableTray = mTrayManager.getShadowEnable();
    mFrameEnableTray = mTrayManager.getFrameEnable();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean isDouble = mDoubleEnableTray.isValue();
    scDoubleSS.setChecked(isDouble);
    layoutSS2.setVisibility(isDouble ? View.VISIBLE : View.GONE);
    scGlare.setChecked(mGlareEnableTray.isValue());
    scShadow.setChecked(mShadowEnableTray.isValue());
    scFrame.setChecked(mFrameEnableTray.isValue());
  }

  @OnCheckedChanged({
      R.id.scDoubleSS, R.id.scGlare, R.id.scShadow, R.id.scFrame
  }) void onCheckedChanged(CompoundButton cb, boolean check) {
    if (cb == scDoubleSS) {
      mDoubleEnableTray.setValue(check);
      if (check) {
        AnimUtils.fadeIn(layoutSS2);
      } else {
        AnimUtils.fadeOut(layoutSS2);
      }
    } else if (cb == scGlare) {
      mGlareEnableTray.setValue(check);
    } else if (cb == scShadow) {
      mShadowEnableTray.setValue(check);
    } else if (cb == scFrame) mFrameEnableTray.setValue(check);
  }

  @OnClick({ R.id.cbfScreen1, R.id.cbfScreen2 }) void onClick(View v) {
    if (v == cbfScreen1) {
      Utils.openImagePicker(this, R.string.screen_1, REQ_IMAGE_PIC_SS1);
    } else if (v == cbfScreen2) Utils.openImagePicker(this, R.string.screen_2, REQ_IMAGE_PIC_SS2);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    final String dataString = data.getDataString();
    EventImageSet.Type wImage;
    switch (requestCode) {
      case REQ_IMAGE_PIC_SS1:
        wImage = EventImageSet.Type.SS1;
        break;
      case REQ_IMAGE_PIC_SS2:
        wImage = EventImageSet.Type.SS2;
        break;
      default:
        wImage = null;
        break;
    }
    if (wImage != null) EventBus.getDefault().post(new EventImageSet(wImage, dataString));
  }
}
