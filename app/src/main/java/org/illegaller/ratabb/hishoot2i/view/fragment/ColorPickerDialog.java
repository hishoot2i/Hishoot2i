package org.illegaller.ratabb.hishoot2i.view.fragment;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ColorPickerDialog extends DialogFragment
        implements SeekBar.OnSeekBarChangeListener, TextView.OnEditorActionListener {
    public static final String TAG = "ColorDialog";
    private static final String KEY_COLOR = "key_color";
    @Bind(R.id.colorPreview) View colorPreview;
    @Bind(R.id.sbColorRed) AppCompatSeekBar sbRed;
    @Bind(R.id.sbColorGreen) AppCompatSeekBar sbGreen;
    @Bind(R.id.sbColorBlue) AppCompatSeekBar sbBlue;
    @Bind(R.id.etHex) EditText etHex;
    private @ColorInt int mColor;
    private Listener mListener;

    public ColorPickerDialog() {
    }

    private static ColorPickerDialog newInstance(@ColorInt int initColor) {
        ColorPickerDialog fragment = new ColorPickerDialog();
        fragment.setArguments(makeArg(initColor));
        return fragment;
    }

    private static Bundle makeArg(@ColorInt int color) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_COLOR, color);
        return bundle;
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putAll(makeArg(mColor));
        super.onSaveInstanceState(outState);
    }

    @Override public void onDestroyView() {
        mListener = null;
        super.onDestroyView();
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_color_picker, null, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState == null) mColor = getArguments().getInt(KEY_COLOR);
        else mColor = savedInstanceState.getInt(KEY_COLOR);
        return createDialog(getActivity(), view, mColor);
    }

    Dialog createDialog(Context context, View view, @ColorInt int color) {
        colorPreview.setBackgroundColor(color);
        ViewCompat.setElevation(colorPreview, 4f);
        sbRed.setProgress(Color.red(color));
        sbGreen.setProgress(Color.green(color));
        sbBlue.setProgress(Color.blue(color));
        etHex.setText(getHexRGB(color));
        sbRed.setOnSeekBarChangeListener(this);
        sbGreen.setOnSeekBarChangeListener(this);
        sbBlue.setOnSeekBarChangeListener(this);
        etHex.setOnEditorActionListener(this);
        return new AlertDialog.Builder(context, getTheme()).setView(view).create();
    }

    @Override public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            String value = textView.getText().toString().trim();
            int parseColor = -1;
            try {
                parseColor = Color.parseColor("#ff" + value);
            } catch (IllegalArgumentException ignore) {/*no-op*/
            }
            if (parseColor != -1) {
                updateView(parseColor);
                Utils.hideSoftKeyboard(getActivity(), etHex.getWindowToken());
            }
            return true;
        }
        return false;
    }

    void updateView(@ColorInt int color) {
        etHex.setText(getHexRGB(color));
        colorPreview.setBackgroundColor(color);
        sbRed.setProgress(Color.red(color));
        sbGreen.setProgress(Color.green(color));
        sbBlue.setProgress(Color.blue(color));
        mColor = color;
    }

    String getHexRGB(@ColorInt int color) {
        String result = Integer.toHexString(color);
        return result.substring(2);
    }

    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        updateView(Color.rgb(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()));
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {/*no-op*/
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {/*no-op*/
    }

    @OnClick({R.id.btnOk, R.id.btnCancel}) void onClick(View view) {
        if (mListener != null && view.getId() == R.id.btnOk) mListener.onChange(mColor);
        dismiss();
    }

    void setListener(Listener listener) {
        mListener = listener;
    }


    public interface Listener {
        void onChange(int color);
    }

    public static class Builder {
        private @ColorInt int initColor = Color.CYAN;
        private Listener listener = null;

        public Builder initColor(@ColorInt int initColor) {
            this.initColor = initColor;
            return this;
        }

        public Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public ColorPickerDialog create() {
            ColorPickerDialog dialog = ColorPickerDialog.newInstance(initColor);
            dialog.setListener(listener);
            return dialog;
        }
    }
}
