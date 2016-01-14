package org.illegaller.ratabb.hishoot2i.ui.fragment;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.ui.activity.HtzFilePickActivity;
import org.illegaller.ratabb.hishoot2i.ui.activity.ImportHtzActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.OnClick;

public class AboutFragment extends BaseFragment {
    private static final int REQ_HTZ = 0x0004;
    @Inject NotificationManager mNotificationManager;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.forceClearNotification) void onForceClearNotification(View view) {
        mNotificationManager.cancel(HishootService.HISHOOT_NOTIFICATION_ID);
    }

    @OnClick(R.id.importTemplateHtz) void onImportTemplateHtz(View view) {
        // TODO: startActivityForResult pickFile htz, then startActivity ImportHtzActivity
        Intent i = new Intent(weakActivity.get(), HtzFilePickActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, REQ_HTZ);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQ_HTZ) {
            Uri uri = data.getData();
            //TODO:
            Intent intent = ImportHtzActivity.getIntent(weakActivity.get());
            intent.setData(uri);
            startActivity(intent);
            weakActivity.get().finish();
        }
    }
}
