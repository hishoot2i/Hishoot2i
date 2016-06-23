package org.illegaller.ratabb.hishoot2i.view.fragment.colorpick;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewEditorActionEvent;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.support.v4.view.ViewCompat.setElevation;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.hideSoftKeyboard;

public class ColorPickerDialog extends DialogFragment {
  private static final String KEY_COLOR = "key_color";
  @BindView(R.id.colorPreview) View colorPreview;
  @BindView(R.id.sbColorRed) AppCompatSeekBar sbRed;
  @BindView(R.id.sbColorGreen) AppCompatSeekBar sbGreen;
  @BindView(R.id.sbColorBlue) AppCompatSeekBar sbBlue;
  @BindView(R.id.etHex) EditText etHex;
  @ColorInt @InjectExtra(KEY_COLOR) int mColor;
  @BindView(R.id.btnOk) AppCompatButton mBtnOk;
  @BindView(R.id.btnCancel) AppCompatButton mBtnCancel;
  private ColorChangeListener mListener;
  private Unbinder mUnBinder;
  private Subscription mSubscription;

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

  public void show(FragmentManager fragmentManager) {
    this.show(fragmentManager, "ColorDialog");
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putAll(makeArg(mColor));
    super.onSaveInstanceState(outState);
  }

  @Override public void onDestroyView() {
    mListener = null;
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
    mUnBinder.unbind();
    super.onDestroyView();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    @SuppressLint("InflateParams") View view =
        LayoutInflater.from(getActivity()).inflate(R.layout.dialog_color_picker, null, false);
    mUnBinder = ButterKnife.bind(this, view);
    Dart.inject(this, getArguments());
    setElevation(colorPreview, 4f);
    updateView(mColor);
    mSubscription = subscription();
    return new AlertDialog.Builder(getActivity(), getTheme()).setView(view).create();
  }

  private Subscription subscription() {
    final CompositeSubscription sub = new CompositeSubscription();
    sub.add(RxTextView.editorActionEvents(etHex)
        .subscribe(this::editorActionEvents, CrashLog::logError));

    sub.add(RxView.clicks(mBtnOk).subscribe(click -> {
      if (mListener != null) mListener.onColorChange(ColorPickerDialog.this, mColor);
      ColorPickerDialog.this.dismissAllowingStateLoss();
    }, CrashLog::logError));

    sub.add(RxView.clicks(mBtnCancel).subscribe(click -> {
      ColorPickerDialog.this.dismissAllowingStateLoss();
    }, CrashLog::logError));

    sub.add(seekBarSubscription(sbRed));
    sub.add(seekBarSubscription(sbGreen));
    sub.add(seekBarSubscription(sbBlue));
    return sub;
  }

  private void editorActionEvents(TextViewEditorActionEvent event) {
    final int actionId = event.actionId();
    final KeyEvent keyEvent = event.keyEvent();
    if (actionId == EditorInfo.IME_ACTION_DONE
        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
      final String value = event.view().getText().toString().trim();
      int colorParse = -1; //invalid color
      try {
        colorParse = Color.parseColor("#ff" + value);
      } catch (IllegalArgumentException iae) {
        CrashLog.logError(iae);
      }
      if (colorParse != -1) {
        updateView(colorParse);
        hideSoftKeyboard(etHex);
      }
    }
  }

  private Subscription seekBarSubscription(SeekBar seekBar) {
    return RxSeekBar.changes(seekBar).subscribe(progress -> {
      updateView(Color.rgb(sbRed.getProgress(), sbGreen.getProgress(), sbBlue.getProgress()));
    }, CrashLog::logError);
  }

  private void updateView(@ColorInt int color) {
    etHex.setText(Integer.toHexString(color).substring(2));
    colorPreview.setBackgroundColor(color);
    sbRed.setProgress(Color.red(color));
    sbGreen.setProgress(Color.green(color));
    sbBlue.setProgress(Color.blue(color));
    if (mColor != color) mColor = color;
  }

  private void setListener(ColorChangeListener listener) {
    mListener = listener;
  }

  public interface ColorChangeListener {
    void onColorChange(ColorPickerDialog pickerDialog, @ColorInt int color);
  }

  public static final class Builder {
    private Builder() {
      throw new AssertionError("no instance");
    }

    public static ColorPickerDialog build(@ColorInt int color,
        @NonNull final ColorChangeListener listener) {
      ColorPickerDialog result = ColorPickerDialog.newInstance(color);
      result.setListener(checkNotNull(listener, "listener == null"));
      return result;
    }
  }
}
