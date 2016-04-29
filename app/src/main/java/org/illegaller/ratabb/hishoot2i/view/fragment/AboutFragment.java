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
import javax.inject.Inject;
import javax.inject.Named;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.SimpleObserver;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import rx.Observable;
import rx.Subscriber;

public class AboutFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
  @Inject @Named(IKeyNameTray.CRASHLYTIC_ENABLE) BooleanTray analyticTray;

  public AboutFragment() {
  }

  public static AboutFragment newInstance() {
    Bundle args = new Bundle();
    AboutFragment fragment = new AboutFragment();
    fragment.setArguments(args);
    return fragment;
  }

  boolean startAboutLibs(Context context) {
    final String[] libs = new String[] {
        "AboutLibraries", "Android-Universal-Image-Loader", "Butter Knife", "Crashlytics",
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

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.about);
    HishootApplication.get(getActivity()).getApplicationComponent().inject(this);
    fixVectorIcon();
    findPreference("pref_libs").setOnPreferenceClickListener(this);
    findPreference("pref_clear_cache").setOnPreferenceClickListener(this);
    findPreference("pref_clear_cache").setSummary(sizeCache());
    findPreference("pref_check_update").setOnPreferenceClickListener(this);
    findPreference("pref_check_update").setSummary(
        getString(R.string.current_version, getString(R.string.app_version),
            getString(R.string.app_build)));
    SwitchPreferenceCompat spcAnalytic =
        (SwitchPreferenceCompat) findPreference("crashlytic_enable");
    spcAnalytic.setChecked(analyticTray.get());
    spcAnalytic.setOnPreferenceChangeListener(this);
  }

  @Override public boolean onPreferenceClick(final Preference preference) {
    if (preference.getKey().equals("pref_libs")) {
      return startAboutLibs(getActivity());
    } else if (preference.getKey().equals("pref_check_update")) {
      updateCheck();
      return true;
    } else if (preference.getKey().equals("pref_clear_cache")) {
      clearCache().subscribe(new SimpleObserver<String>() {
        @Override public void onNext(String s) {
          preference.setSummary(s);
        }

        @Override public void onError(Throwable e) {
          CrashLog.logError("clear cache", e);
        }
      });
      return true;
    } else {
      return false;
    }
  }

  @Override public boolean onPreferenceChange(Preference preference, Object o) {
    if (preference.getKey().equals("crashlytic_enable")) {
      if (o instanceof Boolean) {
        boolean check = (boolean) o;
        analyticTray.set(check);
      }
      return true;
    } else {
      return false;
    }
  }

  /* d' uglier */
  void fixVectorIcon() {
    Map<String, Integer> keyIcons = new HashMap<>();
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
          Drawable icon =
              ResUtils.getVectorDrawable(getActivity(), keyIcons.get(preference.getKey()));
          preference.setIcon(icon);
        }
      }
    }
  }

  void updateCheck() {
    new AppUpdater(getActivity()).setGitHubUserAndRepo("ratabb", "Hishoot2i")
        .setUpdateFrom(UpdateFrom.GITHUB)
        .setDisplay(Display.DIALOG)
        .showAppUpdated(true)
        .start();
  }

  String sizeCache() {
    long cache = UILHelper.sizeCache();
    return Formatter.formatFileSize(getActivity(), cache);
  }

  Observable<String> clearCache() {
    SimpleSchedulers schedulers = new SimpleSchedulers();
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(Subscriber<? super String> subscriber) {
        try {
          UILHelper.clearCache();
          subscriber.onNext(sizeCache());
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    }).subscribeOn(schedulers.backgroundThread()).observeOn(schedulers.mainThread());
  }
}
