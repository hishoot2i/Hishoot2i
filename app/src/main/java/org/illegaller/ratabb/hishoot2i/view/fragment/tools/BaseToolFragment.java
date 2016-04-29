package org.illegaller.ratabb.hishoot2i.view.fragment.tools;

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
import org.illegaller.ratabb.hishoot2i.di.compenent.ToolFragmentComponent;

public abstract class BaseToolFragment extends Fragment {
  protected Unbinder unbinder;
  @Inject protected RefWatcher refWatcher;

  @LayoutRes abstract int getLayoutRes();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ToolFragmentComponent component =
        HishootApplication.get(getActivity()).getApplicationComponent().plus();
    component.inject(this);
    setupComponent(component);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutRes(), container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    refWatcher.watch(this);
    super.onDestroy();
  }

  protected abstract void setupComponent(ToolFragmentComponent component);
}
