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

import java.lang.ref.WeakReference;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;

public class PermissionHelper {
    protected static PermissionHelper sINSTANCE = new PermissionHelper();

    private String singlePermission;
    private int requestCode;
    private WeakReference<Object> weakObject;
    private WeakReference<Callback> weakCallback;

    private boolean hasInit = false;

    private PermissionHelper() {        //no instance
    }

    public static PermissionHelper.Builder writeExternalStorage() {
        return new PermissionHelper.Builder(0x0001, WRITE_EXTERNAL_STORAGE);
    }

    public static PermissionHelper getInstance() {
        synchronized (PermissionHelper.class) {
            if (sINSTANCE == null) sINSTANCE = new PermissionHelper();
        }
        return sINSTANCE;
    }


    protected PermissionHelper init(final Builder builder) {
        this.singlePermission = builder.mPermission;
        this.requestCode = builder.mRequestCode;
        this.weakObject = new WeakReference<>(builder.mObject);
        this.weakCallback = new WeakReference<>(builder.mCallback);
        this.hasInit = true;
        return this;
    }

    public void onResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!hasInit) throw new PermissionHelperException("Builder.build()");
        if (this.requestCode == requestCode && weakCallback.get() != null) {
            if (grantResults[0] == PERMISSION_GRANTED) weakCallback.get().allow();
            else weakCallback.get().deny(permissions[0]);
        }
    }

    public void runRequest() {
        if (!hasInit) throw new PermissionHelperException("Builder.build()");
        if (weakObject.get() == null)
            throw new PermissionHelperException("Builder.with(Object,Callback)");
        if (SDK_INT >= M && checkSelfPermission()) requestPermission();
        else if (weakCallback.get() != null) weakCallback.get().allow();
    }

    public void runRequestWithRationale(android.view.View snackView, @StringRes int rationale) {
        if (!hasInit) throw new PermissionHelperException("Builder.build()");
        if (weakObject.get() == null)
            throw new PermissionHelperException("Builder.with(Object,Callback)");
        if (SDK_INT >= M && checkSelfPermission()) {
            if (shouldShowRequestPermissionRationale()) {
                Snackbar.make(snackView, rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new android.view.View.OnClickListener() {
                            @Override public void onClick(android.view.View view) {
                                requestPermission();
                            }
                        })
                        .show();
            }
        } else if (weakCallback.get() != null) weakCallback.get().allow();
    }

    /* internal */
    protected boolean checkSelfPermission() {
        return ContextCompat.checkSelfPermission(contextFromObject(), this.singlePermission)
                != PERMISSION_GRANTED;
    }

    @TargetApi(M) protected void requestPermission() {
        if (weakObject.get() instanceof Activity)
            ActivityCompat.requestPermissions(objectToActivity(),
                    new String[]{singlePermission}, requestCode);
        else if (weakObject.get() instanceof Fragment)
            objectToFragment().requestPermissions(new String[]{singlePermission}, requestCode);
        else throw new PermissionHelperException("Builder.with(Object,Callback)");
    }

    @TargetApi(M) protected boolean shouldShowRequestPermissionRationale() {
        if (weakObject.get() instanceof Activity)
            return ActivityCompat.shouldShowRequestPermissionRationale(objectToActivity(),
                    this.singlePermission);
        else if (weakObject.get() instanceof Fragment)
            return objectToFragment().shouldShowRequestPermissionRationale(this.singlePermission);
        else throw new PermissionHelperException("Builder.with(Object,Callback)");
    }

    protected Context contextFromObject() {
        if (weakObject.get() instanceof Activity) return objectToActivity();
        else if (weakObject.get() instanceof Fragment)
            return objectToFragment().getActivity();
        else throw new PermissionHelperException("Builder.with(Object,Callback)");
    }

    protected Activity objectToActivity() {
        return (Activity) weakObject.get();
    }

    protected Fragment objectToFragment() {
        return (Fragment) weakObject.get();
    }

    /* listener */
    public interface Callback {
        void allow();

        void deny(String permission);
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
