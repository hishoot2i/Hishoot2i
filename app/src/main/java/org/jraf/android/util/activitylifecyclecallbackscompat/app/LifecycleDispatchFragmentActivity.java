/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jraf.android.util.activitylifecyclecallbackscompat.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.jraf.android.util.activitylifecyclecallbackscompat.ApplicationHelper;
import org.jraf.android.util.activitylifecyclecallbackscompat.MainLifecycleDispatcher;

/**
 * Extension of {@link FragmentActivity} that dispatches its life cycle calls to registered listeners.
 */
public class LifecycleDispatchFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityCreated(this, savedInstanceState);
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
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivitySaveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ApplicationHelper.PRE_ICS) MainLifecycleDispatcher.get().onActivityDestroyed(this);
    }
}
