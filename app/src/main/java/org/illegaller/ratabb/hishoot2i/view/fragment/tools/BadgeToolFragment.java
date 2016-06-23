package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.AdapterViewSelectionEvent;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewEditorActionEvent;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.FontProvider;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.fadeIn;
import static org.illegaller.ratabb.hishoot2i.utils.AnimUtils.fadeOut;
import static org.illegaller.ratabb.hishoot2i.utils.FontUtils.setBadgeTypeface;
import static org.illegaller.ratabb.hishoot2i.utils.FontUtils.setBadgeTypefaceDefault;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.hideSoftKeyboard;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.isEmpty;

public class BadgeToolFragment extends BaseFragment {
  @BindView(R.id.cbHide) SwitchCompat cbHide;
  @BindView(R.id.layout_badge) View vBadge;
  @BindView(R.id.seekBar_BadgeSize) AppCompatSeekBar sbSize;
  @BindView(R.id.spinnerBadgeFont) Spinner spFont;
  @BindView(R.id.etBadge) EditText etBadge;
  @BindView(R.id.cpBadge) CircleButton cpBadge;
  @Inject FontProvider mFontProvider;
  @Inject TrayManager mTrayManager;
  private Subscription mSubscription;
  private BooleanTray mBadgeEnableTray;
  private IntTray mBadgeColorTray;
  private IntTray mBadgeSizeTray;
  private StringTray mBadgeTextTray;
  private StringTray mBadgeTypefaceTray;

  public BadgeToolFragment() {
  }

  public static BadgeToolFragment newInstance() {
    Bundle args = new Bundle();
    BadgeToolFragment fragment = new BadgeToolFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected int layoutRes() {
    return R.layout.bottom_tool_badge;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBadgeEnableTray = mTrayManager.getBadgeEnable();
    mBadgeColorTray = mTrayManager.getBadgeColor();
    mBadgeSizeTray = mTrayManager.getBadgeSize();
    mBadgeTextTray = mTrayManager.getBadgeText();
    mBadgeTypefaceTray = mTrayManager.getBadgeTypeface();
  }

  @Override protected void injectComponent(ActivityComponent activityComponent) {
    activityComponent.inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean badgeEnable = mBadgeEnableTray.isValue();
    cbHide.setChecked(!badgeEnable);
    vBadge.setVisibility(badgeEnable ? View.VISIBLE : View.GONE);
    sbSize.setProgress(mBadgeSizeTray.getValue());
    etBadge.setText(mBadgeTextTray.getValue());
    cpBadge.setColor(mBadgeColorTray.getValue());

    final List<String> list = mFontProvider.asListName();
    list.add(0, AppConstants.BADGE_TYPEFACE);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
    String typeface = mBadgeTypefaceTray.getValue();
    int position = adapter.getPosition(typeface);
    spFont.setAdapter(adapter);
    spFont.setSelection(position);
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
    sub.add(RxTextView.editorActionEvents(etBadge).subscribe(this::textBadge, CrashLog::logError));

    sub.add(RxSeekBar.changes(sbSize).subscribe(this::sizeBadge, CrashLog::logError));

    sub.add(RxAdapterView.selectionEvents(spFont).subscribe(this::fontBadge, CrashLog::logError));

    sub.add(RxView.clicks(cpBadge).subscribe(click -> colorBadge(), CrashLog::logError));

    sub.add(RxCompoundButton.checkedChanges(cbHide).subscribe(this::hideBadge, CrashLog::logError));
    return sub;
  }

  private void textBadge(TextViewEditorActionEvent event) {
    final int actionId = event.actionId();
    final KeyEvent keyEvent = event.keyEvent();
    final TextView view = event.view();
    if (actionId == EditorInfo.IME_ACTION_DONE
        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
      final String value = view.getText().toString().trim();
      boolean empty = isEmpty(value);
      mBadgeTextTray.setValue(!empty ? value : AppConstants.BADGE_TEXT);
      hideSoftKeyboard(view);
    }
  }

  private void sizeBadge(int progress) {
    mBadgeSizeTray.setValue(progress);
  }

  private void fontBadge(AdapterViewSelectionEvent event) {
    final String selected = (String) event.view().getSelectedItem();
    if (selected.equalsIgnoreCase(AppConstants.BADGE_TYPEFACE)) {
      setBadgeTypefaceDefault();
    } else {
      final File fontFile = mFontProvider.find(selected);
      if (fontFile != null && fontFile.canRead()) setBadgeTypeface(fontFile);
    }
    mBadgeTypefaceTray.setValue(selected);
  }

  private void colorBadge() {
    ColorPickerDialog.Builder.build(mBadgeColorTray.getValue(), ((dialog, color) -> {
      mBadgeColorTray.setValue(color);
      cpBadge.setColor(color);
      EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.NONE, ""));
    })).show(getFragmentManager());
  }

  private void hideBadge(boolean checked) {
    mBadgeEnableTray.setValue(!checked);
    if (checked) {
      fadeOut(vBadge);
    } else {
      fadeIn(vBadge);
    }
  }
}
