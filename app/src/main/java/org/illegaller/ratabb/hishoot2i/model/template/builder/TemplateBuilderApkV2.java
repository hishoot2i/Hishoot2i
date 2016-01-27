package org.illegaller.ratabb.hishoot2i.model.template.builder;

import com.google.gson.Gson;

import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.ModelV2;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.HLog;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplateBuilderApkV2 extends AbstractTemplateBuilder {
    public TemplateBuilderApkV2(Context context, String packageName) {
        id = packageName;
        type = TemplateType.APK_V2;
        try {
            InputStream inputStream = Utils.getAssetsStream(context, packageName, "template.cfg");
            ModelV2 modelV2 = parsingStream(inputStream);
            name = modelV2.name;
            author = modelV2.author;
            templateSizes = Sizes.create(modelV2.template_width, modelV2.template_height);
            leftTop = Sizes.create(modelV2.left_top_x, modelV2.left_top_y);
            rightTop = Sizes.create(modelV2.right_top_x, modelV2.right_top_y);
            leftBottom = Sizes.create(modelV2.left_bottom_x, modelV2.left_bottom_y);
            rightBottom = Sizes.create(modelV2.right_bottom_x, modelV2.right_bottom_y);
            previewFile = Utils.getStringFilePath(context, packageName, "preview");
            frameFile = Utils.getStringFilePath(context, packageName, "frame");
            glareFile = Utils.getStringFilePath(context, packageName, "glare");
            shadowFile = Utils.getStringFilePath(context, packageName, "shadow");
            Utils.tryClose(inputStream);
        } catch (IOException | PackageManager.NameNotFoundException e) {
            String msg = "Template: " + packageName + " can't load";
            HLog.e(msg, e);
        }
    }

    private ModelV2 parsingStream(InputStream inputStream) {
        String result = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) stringBuilder.append(line).append("\n");
            reader.close();
            result = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsingGson(result);
    }

    private ModelV2 parsingGson(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, ModelV2.class);
    }
}
