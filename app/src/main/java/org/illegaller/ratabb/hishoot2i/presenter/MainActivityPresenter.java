package org.illegaller.ratabb.hishoot2i.presenter;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;

import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.events.EventMainPerform;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.AnimUtils;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.MainActivityView;
import org.illegaller.ratabb.hishoot2i.view.adapter.ToolFragmentAdapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import butterknife.ButterKnife;

public class MainActivityPresenter implements IPresenter<MainActivityView>,
        OnTabClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private BottomBar mBottomBar;
    private View flBottom;
    private MainActivityView mView;
    private DialogTask mTask;

    public void onSaveInstanceState(Bundle outState) {
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override public void attachView(MainActivityView view) {
        this.mView = view;
    }

    @Override public void detachView() {
        this.mViewPager.removeOnPageChangeListener(this);
        if (this.mTask != null) {
            this.mTask.cancel(true);
            this.mTask = null;
        }
        this.mBottomBar = null;
        this.mViewPager = null;
        this.flBottom = null;
        this.mView = null;
    }

    public void setup(AppCompatActivity activity, Bundle bundle) {
        this.flBottom = ButterKnife.findById(activity, R.id.flBottom);
        this.mViewPager = ButterKnife.findById(activity, R.id.viewPager);
        this.mViewPager.setAdapter(new ToolFragmentAdapter(activity.getSupportFragmentManager()));
        this.mViewPager.addOnPageChangeListener(this);
        this.mBottomBar = bottomBar(activity, bundle);
        openTool();
    }

    ///////////////////////////////////////////////////////
    BottomBar bottomBar(AppCompatActivity activity, Bundle bundle) {
        final int[][] sResource = {
                {R.drawable.ic_phone_android_black_24dp, R.string.screen},
                {R.drawable.ic_image_black_24dp, R.string.background},
                {R.drawable.ic_style_black_24dp, R.string.badge}
        };
        final int count = sResource.length;
        BottomBarTab[] barTabs = new BottomBarTab[count];
        for (int i = 0; i < count; i++) {
            Drawable icon = ResUtils.getVectorDrawable(activity, sResource[i][0]);
            barTabs[i] = new BottomBarTab(icon, sResource[i][1]);
        }
        BottomBar bottomBar = BottomBar.attach(activity, bundle);
        bottomBar.setItems(barTabs);
        bottomBar.setOnTabClickListener(this);
        bottomBar.selectTabAtPosition(0, false);
        return (bottomBar);
    }

    public void handleImageReceive(Intent intent, final String templateId, Template template) {
        if (null != intent && Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            String stringUri = Utils.getStringFromUri(mView.context(), imageUri);
            final String sImagePath = UILHelper.stringFiles(new File(stringUri));
            mTask = new DialogTask(sImagePath, templateId);
            mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            mView.setMainFragment(null, template);
        }
    }

    @Override public void onTabSelected(int position) {
        if (position != mViewPager.getCurrentItem()) mViewPager.setCurrentItem(position);
        if (!isToolOpen()) openTool();
    }

    @Override public void onTabReSelected(int position) {
        if (!isToolOpen()) openTool();
    }

    @Override public void onPageSelected(int position) {
        mBottomBar.selectTabAtPosition(position, true);
    }

    @Override public void onPageScrollStateChanged(int state) {//no-op
    }

    /////////////////////////

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//no-op
    }

    public boolean isToolOpen() {
        return flBottom.getTranslationY() == 0;
    }

    public void closeTool() {
        AnimUtils.translateY(flBottom, 0, flBottom.getHeight());
        showShadowBottomBar();
        EventBus.getDefault().post(new EventMainPerform(true));
    }

    void openTool() {
        AnimUtils.translateY(flBottom, flBottom.getHeight(), 0);
        mBottomBar.hideShadow();
        this.mView.fabSow(false);
    }

    void showShadowBottomBar() {
        View view = ButterKnife.findById(mBottomBar, R.id.bb_bottom_bar_shadow);
        if (view != null) view.setVisibility(View.VISIBLE);
    }

    ///////////////////////////
    void showDialog(final String sImagePath,@NonNull final Template template) {
        AlertDialog dialog = new AlertDialog.Builder(mView.context()).create();
        dialog.setTitle("What type of Image is this?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Background", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                mView.setMainFragment(new ImageReceive(sImagePath, true), template);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Screen", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
                mView.setMainFragment(new ImageReceive(sImagePath, false), template);
            }
        });
        dialog.show();
    }

    class DialogTask extends AsyncTask<Void, Void, Template> {
        private final String sImagePath;
        private final String templateId;

        DialogTask(String image, String templateId) {
            this.sImagePath = image;
            this.templateId = templateId;
        }

        @Override protected Template doInBackground(Void... voids) {
            TemplateProvider provider = new TemplateProvider(mView.context());
            return provider.findById(templateId);
        }

        @Override protected void onPostExecute(Template template) {
            showDialog(sImagePath, template);
        }
    }

}
