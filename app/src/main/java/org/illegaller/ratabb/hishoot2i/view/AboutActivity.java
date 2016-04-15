package org.illegaller.ratabb.hishoot2i.view;

import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.OnClick;

public class AboutActivity extends BaseActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }

    @Override protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    @Override protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override protected void setupComponent(AppComponent component) {
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentAboutLibs();
    }

    private void setFragmentAboutLibs() {
        LibsSupportFragment libsSupportFragment = new LibsBuilder()
                .withLicenseDialog(true)
                .withLicenseShown(true)
                .withAboutVersionShownName(true)
                .withAutoDetect(false)
                .withSortEnabled(true)
                .withFields(R.string.class.getFields())
                .withLibraries(
                        "AboutLibraries",
                        "Android-Universal-Image-Loader",
                        "Butter Knife",
                        "Crashlytics",
                        "Dagger 2",
                        "Eventbus",
                        "Gson",
                        "SmoothProgressBar",
                        "BottomBar",
                        "CustomActivityOnCrash",
                        "Dart",
                        "RecyclerView Animators",
                        "MaterialSearchView",
                        "Tray",
                        "Android StackBlur"
                )
                .withExcludedLibraries("Smoothprogressbar") //duplicate ?
                .supportFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flAboutLib, libsSupportFragment).commitAllowingStateLoss();
    }

    @OnClick(R.id.cbUpdateApp) void onClick(View view) {// TODO: 10/04/2016
        Toast.makeText(AboutActivity.this, "coming soon 7o7", Toast.LENGTH_SHORT).show();
    }
            /*AppUpdate snippet*/
        /*new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("ratabb", "Hishoot2i")
                .setDisplay(Display.DIALOG)
                .setDialogTitleWhenUpdateAvailable("Update available")
                .setDialogDescriptionWhenUpdateAvailable("Check out the latest version available of my app!")
                .setDialogButtonUpdate("Update now?")
                .setDialogButtonDoNotShowAgain("Huh, not interested")
                .setDialogTitleWhenUpdateNotAvailable("Update not available")
                .setDialogDescriptionWhenUpdateNotAvailable("No update available. Check for updates again later!")
                .start();*/
}
