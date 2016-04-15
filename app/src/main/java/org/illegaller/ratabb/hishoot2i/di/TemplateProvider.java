package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.builder.ApkV1Builder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.ApkV2Builder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.HtzBuilder;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_FIND;

public class TemplateProvider {
    public static final Comparator<Template> TEMPLATE_NAME_COMPARATOR = new Comparator<Template>() {
        private final Collator mCollator = Collator.getInstance();

        @Override public int compare(Template t0, Template t1) {
            return mCollator.compare(t0.name, t1.name);
        }
    };
    private final Context mContext;
    private Map<String, Template> templateMap;
    private List<Template> templateList;

    public TemplateProvider(Context context) {
        this.mContext = context.getApplicationContext();
        templateMap = new HashMap<>();
        provideTemplateDefault();
        provideTemplateApk();
        provideTemplateHtz();
        provideTemplateApkV2();
    }

    @Nullable public Template findById(String templateID) {
        if (templateMap.containsKey(templateID)) return templateMap.get(templateID);
        else {
            CrashLog.logError(
                    String.format(Locale.US, MESSAGE_TEMPLATE_CANT_FIND, templateID),
                    null
            );
            return null;
        }
    }

    public List<Template> asList() {
        if (templateMap == null) throw new RuntimeException("provideTemplate()");
        if (templateList == null) templateList = new ArrayList<>(templateMap.values());
        final Template DEFAULT = findById(AppConstants.DEFAULT_TEMPLATE_ID);
        templateList.remove(DEFAULT);
        Collections.sort(templateList, TEMPLATE_NAME_COMPARATOR);
        templateList.add(0, DEFAULT);
        return templateList;
    }

    //START Provide Template
    void provideTemplateDefault() {
        final DefaultBuilder builder = new DefaultBuilder(mContext);
        putToTemplateMap(builder.id, builder.build());
    }

    void provideTemplateApk() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(AppConstants.CATEGORY_TEMPLATE_APK);
        final PackageManager manager = mContext.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo info : infos) {
            final ApkV1Builder builder = new ApkV1Builder(mContext, info.activityInfo.packageName);
            putToTemplateMap(builder.id, builder.build());
        }
    }

    void provideTemplateApkV2() {
        final List<ApplicationInfo> apps = Utils.getInstalledApplications(mContext,
                PackageManager.GET_META_DATA);
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

    void provideTemplateHtz() {
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
    }//END Provide Template

    void putToTemplateMap(String templateID, Template template) {
        /* avoid duplicate template or failed build template*/
        if (templateMap.containsKey(templateID) || template == null) return;
        templateMap.put(templateID, template);
    }
}
