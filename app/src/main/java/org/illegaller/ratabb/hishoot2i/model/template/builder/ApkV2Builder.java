package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import org.illegaller.ratabb.hishoot2i.model.template.ApkV2Model;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.GsonUtils;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_LOAD;

public class ApkV2Builder extends BaseBuilder {

  public ApkV2Builder(Context context, String packageName) {
    final String errorMessage = String.format(Locale.US, MESSAGE_TEMPLATE_CANT_LOAD, packageName);
    id = packageName;
    type = TemplateType.APK_V2;
    ApkV2Model modelV2 = null;
    try {
      InputStream inputStream = ResUtils.openStreamFromAsset(context, packageName, "template.cfg");
      modelV2 = parsingAndCloseStream(inputStream);
      previewFile = ResUtils.getStringFilePath(context, packageName, "preview");
      frameFile = ResUtils.getStringFilePath(context, packageName, "frame");
      glareFile = ResUtils.getStringFilePath(context, packageName, "glare");
      shadowFile = ResUtils.getStringFilePath(context, packageName, "shadow");
    } catch (IOException | PackageManager.NameNotFoundException e) {
      CrashLog.logError(errorMessage, e);
    }
    if (modelV2 == null) {
      CrashLog.logError(errorMessage, null);
    } else {
      name = modelV2.name;
      author = modelV2.author;
      templatePoint = new Point(modelV2.template_width, modelV2.template_height);
      leftTop = new Point(modelV2.left_top_x, modelV2.left_top_y);
      rightTop = new Point(modelV2.right_top_x, modelV2.right_top_y);
      leftBottom = new Point(modelV2.left_bottom_x, modelV2.left_bottom_y);
      rightBottom = new Point(modelV2.right_bottom_x, modelV2.right_bottom_y);
      isSuccessBuild = true;
    }
  }

  @Override public Template build() throws Exception {
    if (isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  @Nullable private ApkV2Model parsingAndCloseStream(InputStream inputStream) {
    String result = null;
    BufferedReader reader = null;
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    try {
      reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append('\n');
      }
      result = stringBuilder.toString();
    } catch (IOException e) {
      CrashLog.logError("parsingAndCloseStream", e);
    } finally {
      Utils.tryClose(reader, inputStream);
    }
    return GsonUtils.fromJson(result, ApkV2Model.class);
  }
}