package org.illegaller.ratabb.hishoot2i.di;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_FIND;

public class TemplateProvider {
  public static final Comparator<Template> TEMPLATE_NAME_COMPARATOR =
      (t0, t1) -> Collator.getInstance().compare(t0.name, t1.name);
  @Inject Context mContext;
  private Template mTemplateDefault;
  private Map<String, Template> mTemplateMap;
  private List<Template> mTemplateList;

  @Inject public TemplateProvider() {
  }

  public Template getTemplateDefault() {
    if (mTemplateDefault == null) mTemplateDefault = findById(AppConstants.DEFAULT_TEMPLATE_ID);
    return mTemplateDefault;
  }

  @Nullable public Template findById(String templateID) {
    Utils.checkNotNull(mTemplateMap, "TemplateProvider.provideTemplate() before call this method");
    if (mTemplateMap.containsKey(templateID)) {
      return mTemplateMap.get(templateID);
    } else {
      CrashLog.logError(String.format(Locale.US, MESSAGE_TEMPLATE_CANT_FIND, templateID), null);
      return null;
    }
  }

  public List<Template> asList() {
    Utils.checkNotNull(mTemplateMap, "TemplateProvider.provideTemplate() before call this method");
    if (mTemplateList == null) mTemplateList = new ArrayList<>(mTemplateMap.values());
    return mTemplateList;
  }

  public void provideTemplate() throws Exception {
    mTemplateMap = new HashMap<>();
    provideTemplateDefault();
    provideTemplateApk();
    provideTemplateHtz();
    provideTemplateApkV2();
  }

  //START Providing Template
  private void provideTemplateDefault()throws Exception  {
    final DefaultBuilder builder = new DefaultBuilder(mContext);
    putToTemplateMap(builder.id, builder.build());
  }

  private void provideTemplateApk()throws Exception  {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(AppConstants.CATEGORY_TEMPLATE_APK);
    final PackageManager manager = mContext.getPackageManager();
    List<ResolveInfo> ifs = manager.queryIntentActivities(intent, 0);
    for (ResolveInfo info : ifs) {
      final ApkV1Builder builder = new ApkV1Builder(mContext, info.activityInfo.packageName);
      putToTemplateMap(builder.id, builder.build());
    }
  }

  private void provideTemplateApkV2()throws Exception  {
    List<ApplicationInfo> apps = null;
    try {
      apps = Utils.getInstalledApplications(mContext, PackageManager.GET_META_DATA);
    } catch (Exception e) {
      CrashLog.logError("provideTemplateApkV2", e);
    }
    if (apps == null) return;
    for (ApplicationInfo app : apps) {
      Bundle metaData = app.metaData;
      if (metaData != null && metaData.containsKey(AppConstants.META_DATA_TEMPLATE)) {
        int version = metaData.getInt(AppConstants.META_DATA_TEMPLATE);
        if (version == 2) {
          final ApkV2Builder builder = new ApkV2Builder(mContext, app.packageName);
          putToTemplateMap(builder.id, builder.build());
        }
      }
    }
  }

  private void provideTemplateHtz()throws Exception  {
    File htzDir = AppConstants.getHishootHtzDir(mContext);
    final File[] lisFiles = htzDir.listFiles();
    if (lisFiles == null) return;
    for (File htzFile : lisFiles) {
      if (htzFile.isDirectory()) {
        File cfg = new File(htzFile, HtzBuilder.HTZ_FILE_CFG);
        if (cfg.exists()) {
          HtzBuilder builder = new HtzBuilder(mContext, null);
          builder.setHtzName(htzFile.getName());
          putToTemplateMap(builder.id, builder.build());
        }
      }
    }
  }
  //END Provide Template

  private void putToTemplateMap(String templateID, Template template) {
        /* avoid duplicate template or failed build template*/
    if (mTemplateMap.containsKey(templateID) || template == null) return;
    mTemplateMap.put(templateID, template);
  }
}
