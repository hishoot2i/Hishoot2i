package org.illegaller.ratabb.hishoot2i.ui.activity;

import com.f2prateek.dart.Dart;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.AppContainer;
import org.illegaller.ratabb.hishoot2i.utils.HLog;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class AbstractBaseActivity extends AppCompatActivity {
    @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;
    @Inject AppContainer appContainer;
    ViewGroup container;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HishootApplication application = HishootApplication.get(this);
        application.inject(this);
        Dart.inject(this);
        container = appContainer.get(this, application);
        Utils.setTransparentStatusBar(getWindow());
        HLog.setTAG(this);
    }

    protected void inflateView(@LayoutRes int layoutID) {
        getLayoutInflater().inflate(layoutID, container);
        ButterKnife.bind(this);
        setToolbar();
    }

    @Override protected void onDestroy() {
        ImageLoader.getInstance().clearMemoryCache();
        Utils.fixInputMethodManager(this);
        Utils.unbindDrawables(container);
        super.onDestroy();
    }

    protected void setToolbar() {
        if (mToolbar == null) return;
        setSupportActionBar(mToolbar);
    }
}
