package org.illegaller.ratabb.hishoot2i.ui.fragment;

import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.ui.navigation.Navigation;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.illegaller.ratabb.hishoot2i.AppConstants.REQ_PICK_HTZ;

public class AboutFragment extends BaseFragment {

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
        Navigation.openHtzPicker(this, REQ_PICK_HTZ);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQ_PICK_HTZ) {
            final Activity activity = weakActivity.get();
            if (activity == null) return;
            Navigation.startImportHtz(activity, data.getData());
            activity.finish();
        }
    }
}
