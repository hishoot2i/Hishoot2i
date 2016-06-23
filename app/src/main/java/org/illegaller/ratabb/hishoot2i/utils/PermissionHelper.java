package org.illegaller.ratabb.hishoot2i.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.lang.ref.WeakReference;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public class PermissionHelper {
  private static volatile PermissionHelper sInstance;

  private String mSinglePermission;
  private int mRequestCode;
  private WeakReference<Object> mWeakObject;
  private WeakReference<Callback> mWeakCallback;
  private boolean mHasInit = false;

  private PermissionHelper() {
  }

  public static Builder storagePermission() {
    return new Builder(0x0001, WRITE_EXTERNAL_STORAGE);
  }

  public static PermissionHelper getInstance() {
    if (sInstance == null) sInstance = InstanceHolder.sINSTANCE;
    return sInstance;
  }

  protected PermissionHelper init(final Builder builder) {
    this.mSinglePermission = builder.mPermission;
    this.mRequestCode = builder.mRequestCode;
    this.mWeakObject = new WeakReference<>(builder.mObject);
    this.mWeakCallback = new WeakReference<>(builder.mCallback);
    this.mHasInit = true;
    return this;
  }

  public void onResult(int requestCode, String[] permissions, int[] grantResults) {
    if (!mHasInit) throw new PermissionHelperException("Builder.build()");
    if (this.mRequestCode == requestCode && getCallback() != null) {
      if (grantResults[0] == PERMISSION_GRANTED) {
        getCallback().allow();
      } else if (getCallback() != null) getCallback().deny(permissions[0]);
    }
  }

  public void runRequest() {
    if (!mHasInit) throw new PermissionHelperException("Builder.build()");
    checkNotNull(getObject(), "Builder.with(Object,Callback)");
    if (SDK_INT >= M && checkSelfPermission()) {
      requestPermission();
    } else if (getCallback() != null) getCallback().allow();
  }

  public void runRequestWithRationale(android.view.View snackView, @StringRes int rationale) {
    if (!mHasInit) throw new PermissionHelperException("Builder.build()");
    checkNotNull(getObject(), "Builder.with(Object,Callback)");
    if (SDK_INT >= M && checkSelfPermission()) {
      if (shouldShowRequestPermissionRationale()) {
        Snackbar.make(snackView, rationale, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok, (v) -> {
              requestPermission();
            })
            .show();
      }
    } else if (getCallback() != null) getCallback().allow();
  }

  private Object getObject() {
    return mWeakObject.get();
  }

  private Callback getCallback() {
    return mWeakCallback.get();
  }

  private boolean checkSelfPermission() {
    return ContextCompat.checkSelfPermission(contextFromObject(), this.mSinglePermission)
        != PERMISSION_GRANTED;
  }

  @TargetApi(M) private void requestPermission() {
    final Object object = getObject();
    if (object instanceof Activity) {
      ActivityCompat.requestPermissions((Activity) object, new String[] { mSinglePermission },
          mRequestCode);
    } else if (object instanceof android.support.v4.app.Fragment) {
      ((android.support.v4.app.Fragment) object).requestPermissions(
          new String[] { mSinglePermission }, mRequestCode);
    } else if (object instanceof android.app.Fragment) {
      ((android.app.Fragment) object).requestPermissions(new String[] { mSinglePermission },
          mRequestCode);
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  @TargetApi(M) private boolean shouldShowRequestPermissionRationale() {
    final Object object = getObject();
    if (object instanceof Activity) {
      return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object,
          this.mSinglePermission);
    } else if (object instanceof android.support.v4.app.Fragment) {
      return ((android.support.v4.app.Fragment) object).shouldShowRequestPermissionRationale(
          this.mSinglePermission);
    } else if (object instanceof android.app.Fragment) {
      return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(
          this.mSinglePermission);
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  private Context contextFromObject() {
    final Object object = getObject();
    if (object instanceof Activity) {
      return (Activity) object;
    } else if (object instanceof android.support.v4.app.Fragment) {
      return ((android.support.v4.app.Fragment) object).getActivity();
    } else if (object instanceof android.app.Fragment) {
      return ((android.app.Fragment) object).getActivity();
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  /* listener */
  public interface Callback {
    void allow();

    void deny(String permission);
  }

  private static class InstanceHolder {
    static final PermissionHelper sINSTANCE = new PermissionHelper();

    private InstanceHolder() {
      throw new AssertionError("no instance");
    }
  }

  /* builder */
  public static class Builder {
    private final int mRequestCode;
    private final String mPermission;
    private Object mObject;
    private Callback mCallback;

    Builder(int requestCode, String permission) {
      this.mRequestCode = requestCode;
      this.mPermission = permission;
    }

    public Builder with(@NonNull final Activity activity, final Callback callback) {
      this.mObject = activity;
      this.mCallback = callback;
      return this;
    }

    public Builder with(@NonNull final android.support.v4.app.Fragment fragment,
        final Callback callback) {
      this.mObject = fragment;
      this.mCallback = callback;
      return this;
    }

    public Builder with(@NonNull final android.app.Fragment fragment, final Callback callback) {
      this.mObject = fragment;
      this.mCallback = callback;
      return this;
    }

    public PermissionHelper build() {
      return PermissionHelper.getInstance().init(this);
    }
  }

  /* exception */
  private static class PermissionHelperException extends RuntimeException {
    PermissionHelperException(String cause) {
      super("PermissionHelper exception " + cause);
    }
  }
}
