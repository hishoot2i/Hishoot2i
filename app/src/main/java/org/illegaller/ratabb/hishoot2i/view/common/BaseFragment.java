package org.illegaller.ratabb.hishoot2i.view.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.squareup.leakcanary.RefWatcher;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;

public abstract class BaseFragment extends Fragment {
  protected Unbinder mBinder;
  protected RefWatcher mRefWatcher;

  public Context getContext() {
    return getActivity();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mRefWatcher =
        HishootApplication.get(getContext()).getAppComponent().refWatcher();
  }

  public ActivityComponent getActivityComponent() {
    return ((BaseActivity) getContext()).getActivityComponent();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(layoutRes(), container, false);
    mBinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroy() {
    mRefWatcher.watch(this);
    super.onDestroy();
  }

  @Override public void onDestroyView() {
    mBinder.unbind();
    super.onDestroyView();
  }

  @LayoutRes protected abstract int layoutRes();
}
