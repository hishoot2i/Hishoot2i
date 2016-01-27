package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.builder.AbstractTemplateBuilder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderApkV1;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderApkV2;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderDefault;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderHtz;
import org.illegaller.ratabb.hishoot2i.utils.HLog;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateProvider {

    private final Map<String, Template> mapTemplate;
    private List<Template> templateList;

    public TemplateProvider(@NonNull final Context context) {
        if (Utils.isMainThread())
            throw new RuntimeException("use background thread");//avoid main/ui thread
        long startMs = System.currentTimeMillis();
        this.mapTemplate = new HashMap<>();
        final Context mContext = context.getApplicationContext();
        provideTemplateApk(mContext);
        provideTemplateApkV2(mContext);
        provideTemplateHtz(mContext);
        provideTemplateDefault(mContext);
        HLog.d("TemplateProvider construction time: "
                + (System.currentTimeMillis() - startMs)
                + "ms");
    }

    @Nullable public Template findById(@NonNull final String templateId) {
        return mapTemplate.get(templateId);
    }

    public List<Template> asList() {
        if (templateList == null) {
            templateList = new ArrayList<>(mapTemplate.values());
        }
        Template DEFAULT = findById(AppConstants.DEFAULT_TEMPLATE_ID);
        templateList.remove(DEFAULT);
        Collections.sort(templateList, new Comparator<Template>() {
            @Override public int compare(Template t0, Template t1) {
                return t0.name.compareToIgnoreCase(t1.name);
            }
        });
        templateList.add(0, DEFAULT);
        return templateList;
    }

    //START Provide Template
    private void provideTemplateDefault(@NonNull final Context mContext) {
        TemplateBuilderDefault builder = new TemplateBuilderDefault(mContext);
        putMap(builder.id, builder.build());
    }

    private void provideTemplateApk(@NonNull final Context mContext) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(AppConstants.CATEGORY_TEMPLATE_APK);
        final PackageManager manager = mContext.getPackageManager();
        List<ResolveInfo> resolveInfoList = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            AbstractTemplateBuilder builder = new TemplateBuilderApkV1(mContext, activityInfo.packageName);
            putMap(builder.id, builder.build());
        }
    }

    private void provideTemplateApkV2(@NonNull final Context mContext) {
        final PackageManager manager = mContext.getPackageManager();
        List<PackageInfo> installedPackages = manager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            Bundle metaData = applicationInfo.metaData;
            if (metaData != null && metaData.containsKey(AppConstants.META_DATA_TEMPLATE)) {
                int version = metaData.getInt(AppConstants.META_DATA_TEMPLATE);
                if (version == 2) {
                    AbstractTemplateBuilder builder = new TemplateBuilderApkV2(mContext, applicationInfo.packageName);
                    putMap(builder.id, builder.build());
                }
            }
        }
    }

    private void provideTemplateHtz(@NonNull final Context mContext) {
        File htzDir = AppConstants.getHishootHtzDir(mContext);
        if (htzDir.listFiles() == null) return;
        for (File htzFile : htzDir.listFiles()) {
            if (htzFile.isDirectory()) {
                File cfg = new File(htzFile, TemplateBuilderHtz.HTZ_FILE_CFG);
                if (cfg.exists()) {
                    TemplateBuilderHtz builder = new TemplateBuilderHtz(mContext, null);
                    builder.setHtzName(htzFile.getName());
                    putMap(builder.id, builder.build());
                }
            }
        }
    }//END Provide Template

    private void putMap(@NonNull final String templateID, @NonNull final Template template) {
        if (mapTemplate.containsKey(templateID)) return;
        mapTemplate.put(templateID, template);
    }
}
