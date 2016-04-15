package org.illegaller.ratabb.hishoot2i.view.common;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.utils.DeviceUtils;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(HishootApplication.get(this).getComponent());
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        setupWindowAnimAndTransparentStatusBar();
        setupToolbar((Toolbar) ButterKnife.findById(this, R.id.toolbar));
    }

    private void setupWindowAnimAndTransparentStatusBar() {
        final Window window = getWindow();
        window.setWindowAnimations(R.style.Animation_Hishoot_Window);
        DeviceUtils.setTransparentStatusBar(window);
    }


    @LayoutRes protected abstract int getLayoutRes();

    protected abstract void setupToolbar(Toolbar toolbar);

    protected abstract void setupComponent(AppComponent component);
}
