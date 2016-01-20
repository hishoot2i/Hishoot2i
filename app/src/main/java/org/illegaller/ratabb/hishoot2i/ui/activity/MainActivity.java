package org.illegaller.ratabb.hishoot2i.ui.activity;

import com.f2prateek.dart.InjectExtra;
import com.f2prateek.dart.Optional;
import com.squareup.otto.Subscribe;

import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.ir.TemplateUsedID;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.ui.fragment.BaseFragment;
import org.illegaller.ratabb.hishoot2i.ui.fragment.ConfigurationFragment;
import org.illegaller.ratabb.hishoot2i.ui.fragment.ListTemplateFragment;
import org.illegaller.ratabb.hishoot2i.ui.navigation.BusProvider;
import org.illegaller.ratabb.hishoot2i.ui.navigation.EventHishoot;
import org.illegaller.ratabb.hishoot2i.ui.navigation.Navigation;
import org.illegaller.ratabb.hishoot2i.ui.navigation.NavigationFragment;
import org.illegaller.ratabb.hishoot2i.utils.HLog;
import org.illegaller.ratabb.hishoot2i.utils.PermissionHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements PermissionHelper.Callback {
    private static final String EXTRA_IMAGE_RECEIVE = "extra.image.receive.main";

    @Inject @TemplateUsedID StringPreference templateUsedIdPref;
    @Optional @InjectExtra(EXTRA_IMAGE_RECEIVE) ImageReceive mImageReceive;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.toolbar_progressbar) ProgressBar mProgressBar;
    private NavigationFragment navFragment;
    private DataImagePath dataPath;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @TargetApi(11)
    public static void handleImageReceive(final Activity activity, @NonNull ImageReceive imageReceive) {
        Intent intent = MainActivity.getIntent(activity);
        intent.putExtra(EXTRA_IMAGE_RECEIVE, imageReceive);
        if (Utils.isHoneycomb()) intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.HishootTheme);
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_main);
        BusProvider.getInstance().register(this);

        navFragment = new NavigationFragment(getSupportFragmentManager(), R.id.flContent);
        navFragment.setRoot(ListTemplateFragment.newInstance());

        if (mImageReceive != null) onNavigation(ConfigurationFragment.newInstance(mImageReceive));

        PermissionHelper.writeExternalStorage()
                .with(this, this)
                .build().runRequest();
        hideFab();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.getInstance().onResult(requestCode, permissions, grantResults);
    }

    @Override protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        navFragment = null;
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        if (!navFragment.isEmpty()) {
            navFragment.goOneBack();
            hideFab();
        } else super.onBackPressed();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            Navigation.startAboutActivity(this);
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void showFab(DataImagePath dataImagePath) {
        if (dataImagePath == null) return;
        this.dataPath = dataImagePath;
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                showProgress();
                HishootService.start(MainActivity.this, dataPath);
                onBackPressed();
            }
        });
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void hideFab() {
        fab.hide();
        fab.setVisibility(View.GONE);
    }

    @Override public void allow() {
        HLog.d("Permission allow");
    }

    @Override public void deny(String permission) {
        HLog.e("Permission " + permission + " deny");
    }

    private void onNavigation(BaseFragment fragment) {
        navFragment.goTo(fragment);
    }

    @Subscribe public void onEvent(final EventHishoot.EventFab event) {
        if (event.dataPaths != null) showFab(event.dataPaths);
        else hideFab();
    }

    @Subscribe public void onEvent(final EventHishoot.EventProcessDone event) {
        dismissProgress();
        Utils.galleryAddPic(this, event.uri);
        View flContent = ButterKnife.findById(this, R.id.flContent);
        Snackbar snackbar = Snackbar.make(flContent, "Hishoot has saved", Snackbar.LENGTH_LONG);
        snackbar.setAction("Open", new View.OnClickListener() {
            @Override public void onClick(View view) {
                Navigation.openImageView(MainActivity.this, event.uri);
            }
        });
        snackbar.show();
    }

    @Subscribe public void onEvent(final EventHishoot.EventSetTemplateUse event) {
        templateUsedIdPref.set(event.templateID);
        onNavigation(ConfigurationFragment.newInstance(null));
    }

}
