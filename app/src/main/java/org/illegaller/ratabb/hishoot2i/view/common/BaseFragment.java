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
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;

public abstract class BaseFragment extends Fragment {
  protected Unbinder unbinder;
  @Inject protected RefWatcher refWatcher;

  public Context context() {
    return getActivity();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ApplicationComponent component = HishootApplication.get(context()).getApplicationComponent();
    component.inject(this);
    setupComponent(component);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(layoutRes(), container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroy() {
    refWatcher.watch(this);
    super.onDestroy();
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @LayoutRes protected abstract int layoutRes();

  protected abstract void setupComponent(ApplicationComponent appComponent);
}
