package org.illegaller.ratabb.hishoot2i.ui.activity;

import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;
import org.jraf.android.util.activitylifecyclecallbackscompat.MainLifecycleDispatcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class AbstractAppCompatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ApplicationHelper.PRE_ICS)
            MainLifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityStopped(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ApplicationHelper.PRE_ICS)
            MainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityDestroyed(this);
    }
}
