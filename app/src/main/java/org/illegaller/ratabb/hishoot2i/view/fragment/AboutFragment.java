package org.illegaller.ratabb.hishoot2i.view.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.format.Formatter;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import java.util.HashMap;
import java.util.Map;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import rx.Observable;

public class AboutFragment extends PreferenceFragmentCompat {
  //private BooleanTray mAnalyticTray;

  public AboutFragment() {
  }

  public static AboutFragment newInstance() {
    Bundle args = new Bundle();
    AboutFragment fragment = new AboutFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.about);
    final Context context = getActivity();
    //mAnalyticTray = HishootApplication.get(context).getTrayManager().getAnalyticsEnable();
    fixVectorIcon(context);

    findPreference("pref_libs").setOnPreferenceClickListener(pref -> startAboutLibs(context));

    findPreference("pref_check_update").setOnPreferenceClickListener(pref -> updateCheck(context));

    findPreference("pref_clear_cache").setSummary(sizeCache(context));
    findPreference("pref_clear_cache").setOnPreferenceClickListener(pref -> {
      clearCache(context).compose(SimpleSchedule.schedule())
          .subscribe(pref::setSummary, throwable -> CrashLog.logError("cache", throwable));
      return true;
    });

    findPreference("pref_check_update").setSummary(
        getString(R.string.current_version, getString(R.string.app_version),
            getString(R.string.app_build)));

    SwitchPreferenceCompat spcAnalytic =
        (SwitchPreferenceCompat) findPreference("crashlytic_enable");
    //spcAnalytic.setChecked(mAnalyticTray.isValue());
    //spcAnalytic.setOnPreferenceChangeListener((preference, o) -> {
    //  if (o instanceof Boolean) mAnalyticTray.setValue((boolean) o);
    //  return true;
    //});
    //remove Fabric
    spcAnalytic.setChecked(false);
    spcAnalytic.setEnabled(false);
  }

  /* d' uglier */
  private void fixVectorIcon(final Context context) {
    final Map<String, Integer> keyIcons = new HashMap<>();
    keyIcons.put("pref_check_update", R.drawable.ic_sync_black_24dp);
    keyIcons.put("crashlytic_enable", R.drawable.ic_bug_report_black_24dp);
    keyIcons.put("pref_dcsms", R.drawable.ic_face_black_24dp);
    keyIcons.put("pref_rbb", R.drawable.ic_face_black_24dp);
    keyIcons.put("pref_fb_group", R.drawable.ic_group_work_black_24dp);
    keyIcons.put("pref_gplus_community", R.drawable.ic_group_work_black_24dp);
    keyIcons.put("pref_source", R.drawable.ic_cloud_circle_black_24dp);
    keyIcons.put("pref_template", R.drawable.ic_cloud_circle_black_24dp);
    keyIcons.put("pref_libs", R.drawable.ic_receipt_black_24dp);
    keyIcons.put("pref_clear_cache", R.drawable.ic_clear_all_black_24dp);
    for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
      PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().getPreference(i);
      for (int j = 0; j < category.getPreferenceCount(); j++) {
        Preference preference = category.getPreference(j);
        if (keyIcons.containsKey(preference.getKey())) {
          Drawable icon = ResUtils.getVectorDrawable(context, keyIcons.get(preference.getKey()));
          preference.setIcon(icon);
        }
      }
    }
  }

  private boolean startAboutLibs(final Context context) {
    final String[] libs = new String[] {
        "AboutLibraries", "Android-Universal-Image-Loader", "Butter Knife", /*"Crashlytics",*/
        "Dagger 2", "Eventbus", "Gson", "SmoothProgressBar", "BottomBar", "LeakCanary",
        "CustomActivityOnCrash", "Dart", "RecyclerView Animators", "MaterialSearchView", "Tray",
        "Android StackBlur", "RxAndroid", "Support Library", "SwipeRevealLayout"
    };
    new LibsBuilder().withLicenseDialog(true)
        .withLicenseShown(true)
        .withAboutVersionShownName(true)
        .withAutoDetect(false)
        .withSortEnabled(true)
        .withFields(R.string.class.getFields())
        .withLibraries(libs)
        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
        .withActivityTitle(context.getString(R.string.libraries))
        .start(context);
    return true;
  }

  private boolean updateCheck(final Context context) {
    new AppUpdater(context).setGitHubUserAndRepo("ratabb", "Hishoot2i")
        .setUpdateFrom(UpdateFrom.GITHUB)
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start();
    return true;
  }

  private String sizeCache(final Context context) {
    return Formatter.formatFileSize(context, UILHelper.sizeCache());
  }

  private Observable<String> clearCache(final Context context) {
    return Observable.create(subscriber -> {
      try {
        UILHelper.clearCache();
        subscriber.onNext(sizeCache(context));
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }
}
