package org.illegaller.ratabb.hishoot2i.di;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.builder.ApkV1Builder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.ApkV2Builder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.HtzBuilder;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_FIND;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.logError;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.avoidUiThread;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.getInstalledApplications;

public class TemplateProvider {
  @Inject Context mContext;
  private Template mTemplateDefault;
  private Map<String, Template> mTemplateMap;
  private List<Template> mTemplateList;

  @Inject TemplateProvider() {
  }

  Template getTemplateDefault() {
    if (mTemplateDefault == null) mTemplateDefault = findById(AppConstants.DEFAULT_TEMPLATE_ID);
    return mTemplateDefault;
  }

  @Nullable Template findById(String templateID) {
    checkNotNull(mTemplateMap, "TemplateProvider.provideTemplate() before call this method");
    if (mTemplateMap.containsKey(templateID)) {
      return mTemplateMap.get(templateID);
    }
    final String msg = String.format(Locale.US, MESSAGE_TEMPLATE_CANT_FIND, templateID);
    logError("TemplateProvider.findById", new RuntimeException(msg));
    return null;
  }

  List<Template> asList() {
    checkNotNull(mTemplateMap, "TemplateProvider.provideTemplate() before call this method");
    if (mTemplateList == null) mTemplateList = new ArrayList<>(mTemplateMap.values());
    return mTemplateList;
  }

  void provideTemplate() throws Exception {
    avoidUiThread("provideTemplate on main thread");
    mTemplateMap = new HashMap<>();
    provideTemplateDefault();
    provideTemplateApk();
    provideTemplateHtz();
    provideTemplateApkV2();
  }

  //START Providing Template
  private void provideTemplateDefault() throws Exception {
    final DefaultBuilder builder = new DefaultBuilder(mContext);
    putToMap(builder.id, builder.build());
  }

  private void provideTemplateApk() throws Exception {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(AppConstants.CATEGORY_TEMPLATE_APK);
    final PackageManager manager = mContext.getPackageManager();
    List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
    for (ResolveInfo info : infos) {
      final ApkV1Builder builder = new ApkV1Builder(mContext, info.activityInfo.packageName);
      putToMap(builder.id, builder.build());
    }
  }

  private void provideTemplateApkV2() throws Exception {
    List<ApplicationInfo> apps = null;
    try {
      apps = getInstalledApplications(mContext, PackageManager.GET_META_DATA);
    } catch (Exception e) {
      logError("getInstalledApplications", e);
    }
    if (apps == null) return;
    for (ApplicationInfo app : apps) {
      Bundle metaData = app.metaData;
      if (metaData != null && metaData.containsKey(AppConstants.META_DATA_TEMPLATE)) {
        int version = metaData.getInt(AppConstants.META_DATA_TEMPLATE);
        if (version == 2) {
          final ApkV2Builder builder = new ApkV2Builder(mContext, app.packageName);
          putToMap(builder.id, builder.build());
        }
      }
    }
  }

  private void provideTemplateHtz() throws Exception {
    final File htzDir = AppConstants.getHishootHtzDir(mContext);
    final File[] lisFiles = htzDir.listFiles();
    if (lisFiles == null) return;
    for (File htzFile : lisFiles) {
      if (htzFile.isDirectory()) {
        final File cfg = new File(htzFile, HtzBuilder.HTZ_FILE_CFG);
        if (cfg.exists()) {
          final HtzBuilder builder = new HtzBuilder(mContext);
          builder.setHtzName(htzFile.getName());
          putToMap(builder.id, builder.build());
        }
      }
    }
  }
  //END Provide Template

  private void putToMap(String templateID, Template template) {
        /* avoid duplicate template or failed build template*/
    if (mTemplateMap.containsKey(templateID) || template == null) return;
    mTemplateMap.put(templateID, template);
  }
}
