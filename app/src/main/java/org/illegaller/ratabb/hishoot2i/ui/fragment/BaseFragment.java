package org.illegaller.ratabb.hishoot2i.ui.fragment;

import com.f2prateek.dart.Dart;
import com.squareup.leakcanary.RefWatcher;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {


    WeakReference<FragmentActivity> weakActivity = null;
    @Inject RefWatcher mRefWatcher;


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakActivity = new WeakReference<>(getActivity());
        HishootApplication.get(weakActivity.get()).inject(this);
        Dart.inject(this, getArguments());
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy() {
        Utils.fixInputMethodManager(getActivity());
        mRefWatcher.watch(this);
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        //fix crash issues #86
//        super.onSaveInstanceState(outState);
    }
}
