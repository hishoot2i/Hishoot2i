package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.HishootApplication;

import android.app.Activity;
import android.view.ViewGroup;

import static butterknife.ButterKnife.findById;

public interface AppContainer {
    AppContainer DEFAULT = new AppContainer() {
        @Override public ViewGroup get(Activity activity, HishootApplication application) {
            return findById(activity, android.R.id.content);
        }
    };

    ViewGroup get(Activity activity, HishootApplication application);
}
