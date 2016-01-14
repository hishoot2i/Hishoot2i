package org.illegaller.ratabb.hishoot2i.ui.fragment;

import com.f2prateek.dart.InjectExtra;
import com.f2prateek.dart.Optional;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.FontProvider;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorInt;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurRadius;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeColor;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeText;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeTypeface;
import org.illegaller.ratabb.hishoot2i.di.ir.ScreenDoubleEnable;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.pref.BooleanPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.ui.navigation.BusProvider;
import org.illegaller.ratabb.hishoot2i.ui.navigation.EventHishoot;
import org.illegaller.ratabb.hishoot2i.ui.navigation.Navigation;
import org.illegaller.ratabb.hishoot2i.ui.widget.BadgeDrawable;
import org.illegaller.ratabb.hishoot2i.utils.FontUtils;
import org.illegaller.ratabb.hishoot2i.utils.HLog;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;


public class ConfigurationFragment extends BaseFragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener, TextWatcher, AdapterView.OnItemSelectedListener,
        ColorPicker.OnColorChangedListener {

    private static final String EXTRA_IMAGE_RECEIVE = "extra.image.receive.config";
    private static final int REQ_IMG_SS1 = 0x0001;
    private static final int REQ_IMG_SS2 = 0x0002;
    private static final int REQ_IMG_BG = 0x0003;
    @Inject @ScreenDoubleEnable BooleanPreference screenDoubleTray;
    @Inject @BackgroundColorEnable BooleanPreference bgColorEnableTray;
    @Inject @BackgroundImageBlurEnable BooleanPreference bgImageBlurEnableTray;
    @Inject @BackgroundImageBlurRadius IntPreference bgImageBlurRadiusTray;
    @Inject @BackgroundColorInt IntPreference bgColorIntTray;
    @Inject @BadgeEnable BooleanPreference badgeEnableTray;
    @Inject @BadgeText StringPreference badgeTextTray;
    @Inject @BadgeColor IntPreference badgeColorTray;

    @Bind(R.id.colorPickBg) ColorPicker colorPickerBg;
    @Bind(R.id.svBarBg) SVBar svBarBg;

    @Bind(R.id.colorPickBadge) ColorPicker colorPickerBadge;
    @Bind(R.id.svBarBadge) SVBar svBarBadge;

    @Bind(R.id.textInputLayoutBadge) TextInputLayout tilBadge;
    @Bind(R.id.etBadge) EditText etBadge;
    @Bind(R.id.img_config_ss1) ImageView imgSS1;
    @Bind(R.id.img_config_ss2) ImageView imgSS2;
    @Bind(R.id.img_config_bg) ImageView imgBg;
    @Bind(R.id.img_config_badge) ImageView imgBadge;
    @Bind(R.id.radioButton_bg_color) RadioButton rbBgColor;
    @Bind(R.id.radioButton_bg_image) RadioButton rbBgImage;
    @Bind(R.id.switch_doubleSS) SwitchCompat swDoubleSS;
    @Bind(R.id.switch_blurBg) SwitchCompat swBlurBg;
    @Bind(R.id.switch_badgeHide) SwitchCompat swBadgeHide;
    @Bind(R.id.seekBar_blur_radius) SeekBar sbBlurRadius;
    @Bind(R.id.layout_bg_color) View colorBgLayout;
    @Bind(R.id.layout_bg_image) View imageBgLayout;
    @Bind(R.id.layout_badge) View badgeLayout;
    @Optional @InjectExtra(EXTRA_IMAGE_RECEIVE) ImageReceive mImageReceive;

    @Bind(R.id.spinnerBadgeFont) Spinner spinnerFontBadge;
    FontProvider fontProvider = new FontProvider();
    @Inject @BadgeTypeface StringPreference badgeTypeFacePref;
    private String pathImageSS1;
    private String pathImageSS2;
    private String pathImageBg;

    public ConfigurationFragment() {
    }

    public static ConfigurationFragment newInstance(@Nullable ImageReceive imageReceive) {
        Bundle args = new Bundle();
        ConfigurationFragment fragment = new ConfigurationFragment();
        if (imageReceive != null) args.putParcelable(EXTRA_IMAGE_RECEIVE, imageReceive);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                 Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configuration, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleImageReceive();
        screenView();
        backgroundView();
        badgeView();
        badgeFontView();

        //////

        @ColorInt int colorEditText = ContextCompat.getColor(view.getContext(), R.color.colorAccent);
        etBadge.setTextColor(colorEditText);
        etBadge.setHintTextColor(colorEditText);

        colorPickerBg.addSVBar(svBarBg);
        colorPickerBg.setColor(bgColorIntTray.get());
        colorPickerBg.setShowOldCenterColor(false);

        colorPickerBadge.addSVBar(svBarBadge);
        colorPickerBadge.setColor(badgeColorTray.get());
        colorPickerBadge.setShowOldCenterColor(false);

        sbBlurRadius.setProgress(bgImageBlurRadiusTray.get());
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HLog.setTAG(this);
        setHasOptionsMenu(true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        (menu.findItem(R.id.action_search)).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume() {
        super.onResume();
        setListener();
    }

    @Override public void onPause() {
        setNullListener();
        super.onPause();
    }

    @Override public void onDestroyView() {
        setNullListener();
        super.onDestroyView();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        final String dataString = data.getDataString();

        if (requestCode == REQ_IMG_SS1) setImageData(imgSS1, dataString);
        else if (requestCode == REQ_IMG_SS2) setImageData(imgSS2, dataString);
        else if (requestCode == REQ_IMG_BG) setImageData(imgBg, dataString);

        checkDataPath();
    }

    //Image pick listener
    @Override public void onClick(View view) {
        if (view == imgSS1) Navigation.openImagePicker(this, "Screen 1", REQ_IMG_SS1);
        else if (view == imgSS2) Navigation.openImagePicker(this, "Screen 2", REQ_IMG_SS2);
        else if (view == imgBg) Navigation.openImagePicker(this, "Background", REQ_IMG_BG);
    }

    //Start Spinner listener
    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String selected = (String) adapterView.getItemAtPosition(pos);
        if (selected.equals(AppConstants.sBADGE_TYPEFACE)) FontUtils.setBadgeTypefaceDefault();
        else {
            final File file = fontProvider.find(selected);
            if (file != null && file.exists()) FontUtils.setBadgeTypeface(file);
        }

        badgeTypeFacePref.set(selected);
        badgeView();
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {//no-op
    } //End Spinner listener

    //Start Switch & RadioButton listener
    @Override public void onCheckedChanged(CompoundButton cb, boolean check) {
        if (cb == swDoubleSS) {
            screenDoubleTray.set(check);
            screenView();
            checkDataPath();
        } else if (cb == rbBgColor) {
            bgColorEnableTray.set(check);
            backgroundView();
            badgeBackgroundView();
            checkDataPath();
        } else if (cb == swBlurBg) {
            bgImageBlurEnableTray.set(check);
            backgroundView();
        } else if (cb == swBadgeHide) {
            badgeEnableTray.set(!check);
            badgeView();
        }
    } //End Switch & RadioButton listener

    //Start SeekBar listener
    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//no-op
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {//no-op
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbBlurRadius) {
            bgImageBlurRadiusTray.set(seekBar.getProgress());
        }
    }//End SeekBar listener

    //Start EditText listener
    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {//no-op
    }

    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {//no-op
    }

    @Override public void afterTextChanged(Editable editable) {
        final String text = editable.toString().trim();
        final boolean badgeHide = TextUtils.isEmpty(text) || text.equals(" ");
        badgeEnableTray.set(!badgeHide);
        badgeTextTray.set(!badgeHide ? text : "hishoot");
        badgeView();
    } //End EditText listener

    //Start ColorPickerView listener
    @Override public void onColorChanged(View view, int color) {
        if (view == colorPickerBg) {
            bgColorIntTray.set(color);
            badgeBackgroundView();
        } else if (view == colorPickerBadge) {
            badgeColorTray.set(color);
            setImageBadge(badgeTextTray.get(), color);
        }
    }//End ColorPickerView listener

    private void setListener() {
        swDoubleSS.setOnCheckedChangeListener(this);
        rbBgColor.setOnCheckedChangeListener(this);
        swBlurBg.setOnCheckedChangeListener(this);
        swBadgeHide.setOnCheckedChangeListener(this);
        sbBlurRadius.setOnSeekBarChangeListener(this);
        imgSS1.setOnClickListener(this);
        imgSS2.setOnClickListener(this);
        imgBg.setOnClickListener(this);

        colorPickerBg.setOnColorChangedListener(this);
        colorPickerBadge.setOnColorChangedListener(this);
        etBadge.addTextChangedListener(this);
        spinnerFontBadge.setOnItemSelectedListener(this);
    }


    private void setNullListener() {
        etBadge.removeTextChangedListener(this);
        colorPickerBg.setOnColorChangedListener(null);
        colorPickerBadge.setOnColorChangedListener(null);
        spinnerFontBadge.setOnItemSelectedListener(null);

    }

    private void screenView() {
        final boolean isDoubleSS = screenDoubleTray.get();
        swDoubleSS.setChecked(isDoubleSS);
        imgSS2.setVisibility(isDoubleSS ? View.VISIBLE : View.GONE);
    }

    private void backgroundView() {
        final boolean isBgColor = bgColorEnableTray.get();
        final boolean isBgImageBlur = bgImageBlurEnableTray.get();

        rbBgColor.setChecked(isBgColor);
        rbBgImage.setChecked(!isBgColor);
        swBlurBg.setChecked(isBgImageBlur);
        colorBgLayout.setVisibility(isBgColor ? View.VISIBLE : View.GONE);
        imageBgLayout.setVisibility(!isBgColor ? View.VISIBLE : View.GONE);
        sbBlurRadius.setVisibility(isBgImageBlur ? View.VISIBLE : View.GONE);

    }

    private void badgeView() {
        final boolean badgeEnable = badgeEnableTray.get();
        final String badgeText = badgeTextTray.get();

        swBadgeHide.setChecked(!badgeEnable);
        badgeLayout.setVisibility(badgeEnable ? View.VISIBLE : View.GONE);
        tilBadge.setHint(badgeText);
        etBadge.setHint(badgeText);

        if (badgeEnable) {
            setImageBadge(badgeText, badgeColorTray.get());
            badgeBackgroundView();
        }
    }

    private void setImageBadge(String text, int color) {
        final Context context = weakActivity.get();
        imgBadge.setImageDrawable(new BadgeDrawable(context, text, color));
    }

    private void badgeBackgroundView() {
        View view = (View) imgBadge.getParent();
        int colorTransparent = ContextCompat.getColor(view.getContext(), android.R.color.transparent);
        if (bgColorEnableTray.get()) view.setBackgroundColor(bgColorIntTray.get());
        else view.setBackgroundColor(colorTransparent);
    }

    private void badgeFontView() {// TODO: send info if only Default
        List<String> list = fontProvider.asListName();
        list.add(0, AppConstants.sBADGE_TYPEFACE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(weakActivity.get(),
                android.R.layout.simple_spinner_item, list);
        String badgeTypeFace = badgeTypeFacePref.get();
        int post = adapter.getPosition(badgeTypeFace);
        spinnerFontBadge.setAdapter(adapter);
        spinnerFontBadge.setSelection(post);
    }

    private void handleImageReceive() {
        if (mImageReceive == null) return;

        if (mImageReceive.imageType == ImageReceive.TYPE_SS)
            setImageData(imgSS1, mImageReceive.imagePath);
        else if (mImageReceive.imageType == ImageReceive.TYPE_BG) {
            setImageData(imgBg, mImageReceive.imagePath);
            bgColorEnableTray.set(false);
        }
    }

    private void setImageData(final ImageView imageView, final String dataString) {
        if (imageView == imgSS1) pathImageSS1 = dataString;
        else if (imageView == imgSS2) pathImageSS2 = dataString;
        else if (imageView == imgBg) pathImageBg = dataString;

        if (Utils.isLollipop()) imageView.setImageTintList(null);
        else imageView.setColorFilter(null);

        if (Utils.isJellyBean()) imageView.setBackground(null);

        UILHelper.displayPreview(imageView, dataString);
    }

    private void checkDataPath() {
        boolean isValid = pathImageSS1 != null;
        if (screenDoubleTray.get()) isValid &= pathImageSS2 != null;
        if (!bgColorEnableTray.get()) isValid &= pathImageBg != null;

        final DataImagePath dataPath = isValid ?
                new DataImagePath(pathImageSS1, pathImageSS2, pathImageBg) : null;
        BusProvider.getInstance().post(new EventHishoot.EventFab(dataPath));
        HLog.d("dataPath: " + (isValid ? dataPath.toString() : "<null>"));
    }

}
