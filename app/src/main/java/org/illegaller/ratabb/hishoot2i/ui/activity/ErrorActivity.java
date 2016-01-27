package org.illegaller.ratabb.hishoot2i.ui.activity;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.PermissionHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import static org.illegaller.ratabb.hishoot2i.AppConstants.getHishootDir;

public class ErrorActivity extends AbstractBaseActivity implements PermissionHelper.Callback {
    public static final String sLOG_TAG = "ErrorActivity";
    @Bind(R.id.errorText) TextView errorText;
    @Bind(R.id.restartActivityButton) Button restartButton;
    private Class<? extends Activity> restartActivity;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.HishootTheme);
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_custom_error);

        errorText.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent()));
        restartActivity = CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent());
        if (restartActivity != null) restartButton.setText(R.string.restart);

        PermissionHelper.writeExternalStorage().with(this, this).build();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.restartActivityButton) void onRestartButtonClick(View view) {
        if (restartActivity == null) CustomActivityOnCrash.closeApplication(ErrorActivity.this);
        else CustomActivityOnCrash.restartApplicationWithIntent(ErrorActivity.this,
                new Intent(this, restartActivity));
    }


    @OnClick(R.id.saveErrorButton) void onSaveErrorButtonClick(View view) {
        PermissionHelper.getInstance().runRequest();
    }

    @OnClick(R.id.copyErrorButton) void onCopyErrorButtonClick(View view) {
        copyToClipBoard();
    }

    @SuppressWarnings("deprecated")
    @TargetApi(11) private void copyToClipBoard() {
        if (Utils.isHoneycomb()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Error log", errorText.getText());
            clipboard.setPrimaryClip(clipData);
        } else {
            @SuppressWarnings("deprecated") android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(errorText.getText());
        }
        Utils.makeLongToast(this, "Error log copy to clipboard");
    }

    private void doSave() {
        String errorMessage = errorText.getText().toString();
        File errorFile = new File(getHishootDir(), "hishoot2i-error.log");
        new SaveErrorTask().execute(new ErrorModel(errorMessage, errorFile));
    }

    @Override public void allow() {
        doSave();
    }

    @Override public void deny(String permission) {
        Log.e(sLOG_TAG, "Permission " + permission + " has deny");
        //we don't have permission Write External Storage;  hide SaveErrorButton
        LinearLayout layoutButton = (LinearLayout) restartButton.getParent();
        layoutButton.setWeightSum(2);
        Button saveButton = ButterKnife.findById(layoutButton, R.id.saveErrorButton);
        saveButton.setVisibility(View.GONE);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().onResult(requestCode, permissions, grantResults);
    }


    class SaveErrorTask extends AsyncTask<ErrorModel, Void, String> {
        @Override protected String doInBackground(ErrorModel... errorModels) {
            final ErrorModel model = errorModels[0];
            if (Utils.saveTextToFile(model.errorMassage, model.errorFile))
                return model.errorFile.getAbsolutePath();
            else return null;
        }

        @Override protected void onPostExecute(String result) {
            if (result != null) Utils.makeToast(ErrorActivity.this, "File error:\n" + result);
        }
    }

    class ErrorModel {
        final String errorMassage;
        final File errorFile;

        public ErrorModel(String errorMassage, File errorFile) {
            this.errorMassage = errorMassage;
            this.errorFile = errorFile;
        }
    }

}
