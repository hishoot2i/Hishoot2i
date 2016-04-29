package org.illegaller.ratabb.hishoot2i.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public class PermissionHelper {
  private static PermissionHelper sInstance = null;

  private String singlePermission;
  private int requestCode;
  private java.lang.ref.WeakReference<Object> weakObject;
  private java.lang.ref.WeakReference<Callback> weakCallback;

  private boolean hasInit = false;

  private PermissionHelper() { /*no instance*/ }

  public static Builder storagePermission() {
    return new Builder(0x0001, WRITE_EXTERNAL_STORAGE);
  }

  public static synchronized PermissionHelper getInstance() {
    if (sInstance == null) sInstance = ClassHolder.instance();
    return sInstance;
  }

  protected PermissionHelper init(final Builder builder) {
    this.singlePermission = builder.mPermission;
    this.requestCode = builder.mRequestCode;
    this.weakObject = new java.lang.ref.WeakReference<>(builder.mObject);
    this.weakCallback = new java.lang.ref.WeakReference<>(builder.mCallback);
    this.hasInit = true;
    return this;
  }

  public void onResult(int requestCode, String[] permissions, int[] grantResults) {
    if (!hasInit) throw new PermissionHelperException("Builder.build()");
    if (this.requestCode == requestCode && getCallback() != null) {
      if (grantResults[0] == PERMISSION_GRANTED) {
        getCallback().allow();
      } else if (getCallback() != null) getCallback().deny(permissions[0]);
    }
  }

  public void runRequest() {
    if (!hasInit) throw new PermissionHelperException("Builder.build()");
    checkNotNull(getObject(), "Builder.with(Object,Callback)");
    if (SDK_INT >= M && checkSelfPermission()) {
      requestPermission();
    } else if (getCallback() != null) getCallback().allow();
  }

  public void runRequestWithRationale(android.view.View snackView, @StringRes int rationale) {
    if (!hasInit) throw new PermissionHelperException("Builder.build()");
    checkNotNull(getObject(), "Builder.with(Object,Callback)");
    if (SDK_INT >= M && checkSelfPermission()) {
      if (shouldShowRequestPermissionRationale()) {
        Snackbar.make(snackView, rationale, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok, (android.view.View view) -> {
              requestPermission();
            })
            .show();
      }
    } else if (getCallback() != null) getCallback().allow();
  }

  protected Object getObject() {
    return weakObject.get();
  }

  protected Callback getCallback() {
    return weakCallback.get();
  }

  protected boolean checkSelfPermission() {
    return ContextCompat.checkSelfPermission(contextFromObject(), this.singlePermission)
        != PERMISSION_GRANTED;
  }

  @TargetApi(M) protected void requestPermission() {
    final Object object = getObject();
    if (object instanceof Activity) {
      ActivityCompat.requestPermissions((Activity) object, new String[] { singlePermission },
          requestCode);
    } else if (object instanceof Fragment) {
      ((Fragment) object).requestPermissions(new String[] { singlePermission }, requestCode);
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  @TargetApi(M) protected boolean shouldShowRequestPermissionRationale() {
    final Object object = getObject();
    if (object instanceof Activity) {
      return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object,
          this.singlePermission);
    } else if (object instanceof Fragment) {
      return ((Fragment) object).shouldShowRequestPermissionRationale(this.singlePermission);
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  protected Context contextFromObject() {
    final Object object = getObject();
    if (object instanceof Activity) {
      return (Activity) object;
    } else if (object instanceof Fragment) {
      return ((Fragment) object).getActivity();
    } else {
      throw new PermissionHelperException("Builder.with(Object,Callback)");
    }
  }

  /* listener */
  public interface Callback {
    void allow();

    void deny(String permission);
  }

  static class ClassHolder {
    static PermissionHelper instance() {
      return new PermissionHelper();
    }
  }

  /* builder */
  public static class Builder {
    private final int mRequestCode;
    private final String mPermission;
    private Object mObject;
    private Callback mCallback;

    public Builder(int requestCode, String permission) {
      this.mRequestCode = requestCode;
      this.mPermission = permission;
    }

    public Builder with(@NonNull final Activity activity, final Callback callback) {
      this.mObject = activity;
      this.mCallback = callback;
      return this;
    }

    public Builder with(@NonNull final Fragment fragment, final Callback callback) {
      this.mObject = fragment;
      this.mCallback = callback;
      return this;
    }

    public PermissionHelper build() {
      return PermissionHelper.getInstance().init(this);
    }
  }

  /* exception */
  protected static class PermissionHelperException extends RuntimeException {
    PermissionHelperException(String cause) {
      super("PermissionHelper exception " + cause);
    }
  }
}
