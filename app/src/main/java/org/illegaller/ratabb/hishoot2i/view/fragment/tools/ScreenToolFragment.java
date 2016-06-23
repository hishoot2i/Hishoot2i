package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import butterknife.BindView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.fadeIn;
import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.fadeOut;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.openImagePicker;

public class ScreenToolFragment extends BaseFragment {
  private static final int REQ_IMAGE_PIC_SS1 = 0x01;
  private static final int REQ_IMAGE_PIC_SS2 = 0x02;
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
  private Subscription mSubscription;

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
    mDoubleEnableTray = mTrayManager.getDoubleEnable();
    mGlareEnableTray = mTrayManager.getGlareEnable();
    mShadowEnableTray = mTrayManager.getShadowEnable();
    mFrameEnableTray = mTrayManager.getFrameEnable();
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean isDouble = mDoubleEnableTray.isValue();
    scDoubleSS.setChecked(isDouble);
    layoutSS2.setVisibility(isDouble ? View.VISIBLE : View.GONE);
    scGlare.setChecked(mGlareEnableTray.isValue());
    scShadow.setChecked(mShadowEnableTray.isValue());
    scFrame.setChecked(mFrameEnableTray.isValue());
    mSubscription = viewSubscription();
  }

  @Override public void onDestroyView() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
    super.onDestroyView();
  }

  private Subscription viewSubscription() {
    final CompositeSubscription sub = new CompositeSubscription();
    sub.add(RxView.clicks(cbfScreen1)
        .subscribe(click -> openImagePicker(this, R.string.screen_1, REQ_IMAGE_PIC_SS1),
            CrashLog::logError));

    sub.add(RxView.clicks(cbfScreen2)
        .subscribe(click -> openImagePicker(this, R.string.screen_2, REQ_IMAGE_PIC_SS2),
            CrashLog::logError));

    sub.add(RxCompoundButton.checkedChanges(scDoubleSS)
        .subscribe(this::ssDoubleChecked, CrashLog::logError));

    sub.add(RxCompoundButton.checkedChanges(scFrame)
        .subscribe(checked -> mFrameEnableTray.setValue(checked), CrashLog::logError));

    sub.add(RxCompoundButton.checkedChanges(scGlare)
        .subscribe(checked -> mGlareEnableTray.setValue(checked), CrashLog::logError));

    sub.add(RxCompoundButton.checkedChanges(scShadow)
        .subscribe(checked -> mShadowEnableTray.setValue(checked), CrashLog::logError));
    return sub;
  }

  private void ssDoubleChecked(boolean checked) {
    mDoubleEnableTray.setValue(checked);
    if (checked) {
      fadeIn(layoutSS2);
    } else {
      fadeOut(layoutSS2);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != Activity.RESULT_OK) return;
    final String dataString = data.getDataString();
    if (requestCode == REQ_IMAGE_PIC_SS1) {
      EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.SS1, dataString));
    } else if (requestCode == REQ_IMAGE_PIC_SS2) {
      EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.SS2, dataString));
    }
  }
}
