package org.illegaller.ratabb.hishoot2i.view.fragment;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DownloadFragment extends BaseFragment {
    public DownloadFragment() {
    }

    public static DownloadFragment newInstance() {
        Bundle args = new Bundle();
        DownloadFragment fragment = new DownloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void setupComponent(AppComponent component) {// TODO:
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }
}
