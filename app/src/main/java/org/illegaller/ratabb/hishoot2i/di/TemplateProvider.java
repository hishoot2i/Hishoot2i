package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.builder.AbstractTemplateBuilder;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderApkV1;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderApkV2;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderDefault;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderHtz;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateProvider {

    private final Context mContext;
    private final PackageManager mPackageManager;
    private final Map<String, Template> mapTemplate;
    private List<Template> templateList;

    public TemplateProvider(@NonNull Context context) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mapTemplate = new HashMap<>();

        provideTemplateApk();
        provideTemplateHtz();
        provideTemplateDefault();
    }


    public Template findById(@NonNull final String templateId) {
        Template result = mapTemplate.get(templateId);
        if (result == null) throw new RuntimeException("findById: " + templateId);
        return result;
    }

    // TODO: do we need this?
   /*public List<Template> asListTemplateV1() {
        List<Template> result = new ArrayList<>();
        for (Template template : asList()) {
            if (template.type == TemplateType.APK_V1) result.add(template);
        }
        return result;
    }

    public List<Template> asListTemplateHtz() {
        List<Template> result = new ArrayList<>();
        for (Template template : asList()) {
            if (template.type == TemplateType.HTZ) result.add(template);
        }
        return result;
    }

    public List<Template> asListTemplateV2() {
        List<Template> result = new ArrayList<>();
        for (Template template : asList()) {
            if (template.type == TemplateType.APK_V2) result.add(template);
        }
        return result;
    }*/
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
    private void provideTemplateDefault() {
        TemplateBuilderDefault builder = new TemplateBuilderDefault(mContext);
        mapTemplate.put(builder.id, builder.build());
    }

    private void provideTemplateApk() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(AppConstants.CATEGORY_TEMPLATE_APK);
        List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Bundle metaData = activityInfo.metaData;
            AbstractTemplateBuilder builder = null;
            if (metaData != null) {//v2
                int templateVersion = metaData.getInt(AppConstants.META_DATA_TEMPLATE);
                if (templateVersion == 2)
                    builder = new TemplateBuilderApkV2(mContext, activityInfo.packageName);
            } else { //v1
                builder = new TemplateBuilderApkV1(mContext, activityInfo.packageName);
            }

            if (builder != null) mapTemplate.put(builder.id, builder.build());
        }
    }

    private void provideTemplateHtz() {
        File htzDir = AppConstants.getHishootHtzDir(mContext);
        if (htzDir.listFiles() == null) return;
        for (File htzFile : htzDir.listFiles()) {
            if (htzFile.isDirectory()) {
                File cfg = new File(htzFile, TemplateBuilderHtz.HTZ_FILE_CFG);
                if (cfg.exists()) {
                    TemplateBuilderHtz builder = new TemplateBuilderHtz(mContext);
                    builder.setHtzName(htzFile.getName());
                    mapTemplate.put(builder.id, builder.build());
                }
            }
        }
    }//END Provide Template

}
