package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.FontProvider;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.FontUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.fragment.ColorPickerDialog;
import org.illegaller.ratabb.hishoot2i.view.widget.CircleButton;

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

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class BadgeToolFragment extends BaseToolFragment
        implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener, TextWatcher {
    @Bind(R.id.cbHide) SwitchCompat cbHide;
    @Bind(R.id.layout_badge) View vBadge;
    @Bind(R.id.seekBar_BadgeSize) AppCompatSeekBar sbSize;
    @Bind(R.id.spinnerBadgeFont) Spinner spFont;
    @Bind(R.id.etBadge) EditText etBadge;
    @Bind(R.id.cpBadge) CircleButton cpBadge;
    private TrayManager mTrayManager;
    private FontProvider mFontProvider;

    public BadgeToolFragment() {
    }

    public static BadgeToolFragment newInstance() {
        Bundle args = new Bundle();
        BadgeToolFragment fragment = new BadgeToolFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override int getLayoutRes() {
        return R.layout.bottom_tool_badge;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFontProvider = new FontProvider();
    }

    @Override protected void onView() {
        boolean badgeEnable = mTrayManager.getBadgeEnableTray().get();
        cbHide.setChecked(!badgeEnable);
        vBadge.setVisibility(badgeEnable ? View.VISIBLE : View.GONE);
        sbSize.setProgress(mTrayManager.getBadgeSizeTray().get());
        etBadge.setText(mTrayManager.getBadgeTextTray().get());
        sbSize.setOnSeekBarChangeListener(this);
        etBadge.addTextChangedListener(this);
        cpBadge.setColor(mTrayManager.getBadgeColorTray().get());
        viewSpinner();
    }

    @Override protected void setTrayManager(AppComponent appComponent) {
        mTrayManager = appComponent.trayManager();
    }

    @Override public void onDestroyView() {
        etBadge.removeTextChangedListener(this);
        super.onDestroyView();
    }

    private void viewSpinner() {
        List<String> list = mFontProvider.asListName();
        list.add(0, AppConstants.BADGE_TYPEFACE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        String typeface = mTrayManager.getBadgeTypefaceTray().get();
        int position = adapter.getPosition(typeface);
        spFont.setAdapter(adapter);
        spFont.setSelection(position);
        spFont.setOnItemSelectedListener(this);
    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String selected = (String) adapterView.getItemAtPosition(pos);
        if (selected.equalsIgnoreCase(AppConstants.BADGE_TYPEFACE))
            FontUtils.setBadgeTypefaceDefault();
        else {
            final File file = mFontProvider.find(selected);
            if (file != null && file.canRead()) FontUtils.setBadgeTypeface(file);
        }
        mTrayManager.getBadgeTypefaceTray().set(selected);
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {//no-op
    }

    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//no-op
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {//no-op
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        mTrayManager.getBadgeSizeTray().set(seekBar.getProgress());
    }

    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {//no-op
    }

    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {//no-op
    }

    @Override public void afterTextChanged(Editable editable) {
        final String value = editable.toString().trim();
        boolean empty = Utils.isEmpty(value);
        mTrayManager.getBadgeTextTray().set(!empty ? value : AppConstants.BADGE_TEXT);
    }

    @OnCheckedChanged(R.id.cbHide) void onCheckedChanged(CompoundButton cb, boolean checked) {
        mTrayManager.getBadgeEnableTray().set(!checked);
        onView();
    }

    @OnClick(R.id.cpBadge) void onClick(View view) {
        final int c = mTrayManager.getBadgeColorTray().get();
        ColorPickerDialog dialog = new ColorPickerDialog.Builder()
                .initColor(c).listener(new ColorPickerDialog.Listener() {
                    @Override public void onChange(int color) {
                        mTrayManager.getBadgeColorTray().set(color);
                        cpBadge.setColor(color);
                    }
                }).create();
        dialog.show(getFragmentManager(), ColorPickerDialog.TAG);
    }
}
