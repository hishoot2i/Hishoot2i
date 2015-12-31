package org.illegaller.ratabb.hishoot2i.model.template.builder;

import com.google.gson.Gson;

import com.nostra13.universalimageloader.utils.L;

import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TemplateBuilderApkV2 extends AbstractTemplateBuilder {
    public TemplateBuilderApkV2(Context context, String packageName) {
        super(context);
        id = packageName;
        type = TemplateType.APK_V2;
        InputStream inputStream = null;
        try {
            inputStream = Utils.getAssetsStream(context, packageName, "template.cfg");
            ModelV2 modelV2 = parsing(inputStream);
            name = modelV2.name;
            author = modelV2.author;
            templateSizes = Sizes.create(modelV2.template_width, modelV2.template_height);
            screenSizes = Sizes.create(modelV2.screen_width, modelV2.screen_height);
            offset = Sizes.create(modelV2.screen_x, modelV2.screen_y);
            isCompatible = screenSizes.equals(userDeviceScreenSizes);
            previewFile = UILHelper.stringTemplateApp(packageName,
                    Utils.getResIdDrawableTemplate(context, packageName, "preview"));
            frameFile = UILHelper.stringTemplateApp(packageName,
                    Utils.getResIdDrawableTemplate(context, packageName, "frame"));
            glareFile = UILHelper.stringTemplateApp(packageName,
                    Utils.getResIdDrawableTemplate(context, packageName, "glare"));
            shadowFile = UILHelper.stringTemplateApp(packageName,
                    Utils.getResIdDrawableTemplate(context, packageName, "shadow"));

        } catch (IOException | PackageManager.NameNotFoundException e) {
            String msg = "Template: " + packageName + " can't load";
            L.e(e, msg);
        } finally {
            Utils.tryClose(inputStream);
        }

    }

    ModelV2 parsing(InputStream inputStream) {
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
        return parsing(result);
    }

    ModelV2 parsing(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, ModelV2.class);
    }

    class ModelV2 {
        public String name;
        public String author;
        public int screen_width;
        public int screen_height;
        public int screen_x;
        public int screen_y;
        public int template_width;
        public int template_height;
    }
}
