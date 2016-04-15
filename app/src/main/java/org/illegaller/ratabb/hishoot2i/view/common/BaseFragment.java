package org.illegaller.ratabb.hishoot2i.view.common;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    public Context context() {
        return getActivity();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(HishootApplication.get(context()).getComponent());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override public void onDestroy() {
       /* Utils.fixInputMethodManager(getActivity());*/
        HishootApplication.get(context()).getWatcher().watch(this);
        super.onDestroy();
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected abstract void setupComponent(AppComponent component);

}
