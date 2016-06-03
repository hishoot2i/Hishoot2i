package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.FontProvider;
import org.illegaller.ratabb.hishoot2i.events.EventImageSet;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.FontUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.fragment.colorpick.ColorPickerDialogBuilder;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

public class BadgeToolFragment extends BaseFragment
    implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener, TextWatcher {
  @BindView(R.id.cbHide) SwitchCompat cbHide;
  @BindView(R.id.layout_badge) View vBadge;
  @BindView(R.id.seekBar_BadgeSize) AppCompatSeekBar sbSize;
  @BindView(R.id.spinnerBadgeFont) Spinner spFont;
  @BindView(R.id.etBadge) EditText etBadge;
  @BindView(R.id.cpBadge) CircleButton cpBadge;
  @Inject TrayManager mTrayManager;
  @Inject FontProvider mFontProvider;
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
    getActivityComponent().inject(this);
    mBadgeEnableTray = mTrayManager.getBadgeEnable();
    mBadgeColorTray = mTrayManager.getBadgeColor();
    mBadgeSizeTray = mTrayManager.getBadgeSize();
    mBadgeTextTray = mTrayManager.getBadgeText();
    mBadgeTypefaceTray = mTrayManager.getBadgeTypeface();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    boolean badgeEnable = mBadgeEnableTray.isValue();
    cbHide.setChecked(!badgeEnable);
    vBadge.setVisibility(badgeEnable ? View.VISIBLE : View.GONE);
    sbSize.setProgress(mBadgeSizeTray.getValue());
    etBadge.setText(mBadgeTextTray.getValue());
    sbSize.setOnSeekBarChangeListener(this);
    etBadge.addTextChangedListener(this);
    cpBadge.setColor(mBadgeColorTray.getValue());
    viewSpinner();
  }

  @Override public void onDestroyView() {
    etBadge.removeTextChangedListener(this);
    super.onDestroyView();
  }

  private void viewSpinner() {
    List<String> list = mFontProvider.asListName();
    list.add(0, AppConstants.BADGE_TYPEFACE);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
    String typeface = mBadgeTypefaceTray.getValue();
    int position = adapter.getPosition(typeface);
    spFont.setAdapter(adapter);
    spFont.setSelection(position);
    spFont.setOnItemSelectedListener(this);
  }

  @Override public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
    String selected = (String) adapterView.getItemAtPosition(pos);
    if (selected.equalsIgnoreCase(AppConstants.BADGE_TYPEFACE)) {
      FontUtils.setBadgeTypefaceDefault();
    } else {
      final File file = mFontProvider.find(selected);
      if (file != null && file.canRead()) FontUtils.setBadgeTypeface(file);
    }
    mBadgeTypefaceTray.setValue(selected);
  }

  @Override public void onNothingSelected(AdapterView<?> adapterView) { /*no-op*/ }

  @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) { /*no-op*/ }

  @Override public void onStartTrackingTouch(SeekBar seekBar) { /*no-op*/ }

  @Override public void onStopTrackingTouch(SeekBar seekBar) {
    mBadgeSizeTray.setValue(seekBar.getProgress());
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /*no-op*/ }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { /*no-op*/ }

  @Override public void afterTextChanged(Editable editable) {
    final String value = editable.toString().trim();
    boolean empty = Utils.isEmpty(value);
    mBadgeTextTray.setValue(!empty ? value : AppConstants.BADGE_TEXT);
  }

  @OnCheckedChanged(R.id.cbHide) void onCheckedChanged(CompoundButton cb, boolean checked) {
    mBadgeEnableTray.setValue(!checked);
    if (checked) {
      AnimUtils.fadeOut(vBadge);
    } else {
      AnimUtils.fadeIn(vBadge);
    }
  }

  @OnClick(R.id.cpBadge) void onClick(View view) {
    ColorPickerDialogBuilder.create()
        .colorInit(mBadgeColorTray.getValue())
        .listener((dialog, color) -> {
          mBadgeColorTray.setValue(color);
          cpBadge.setColor(color);
          EventBus.getDefault().post(new EventImageSet(EventImageSet.Type.NONE, ""));
        })
        .build()
        .show(getFragmentManager(), ColorPickerDialog.TAG);
  }
}
