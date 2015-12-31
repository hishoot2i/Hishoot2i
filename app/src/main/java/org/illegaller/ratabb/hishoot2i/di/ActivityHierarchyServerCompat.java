package org.illegaller.ratabb.hishoot2i.di;

import org.jraf.android.util.activitylifecyclecallbackscompat.ActivityLifecycleCallbacksCompat;

import android.app.Activity;
import android.os.Bundle;

public interface ActivityHierarchyServerCompat extends ActivityLifecycleCallbacksCompat {
    ActivityHierarchyServerCompat NONE = new ActivityHierarchyServerCompat() {
        @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override public void onActivityStarted(Activity activity) {
        }

        @Override public void onActivityResumed(Activity activity) {
        }

        @Override public void onActivityPaused(Activity activity) {
        }

        @Override public void onActivityStopped(Activity activity) {
        }

        @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override public void onActivityDestroyed(Activity activity) {
        }
    };
}
