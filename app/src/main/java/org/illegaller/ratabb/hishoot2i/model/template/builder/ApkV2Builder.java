package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import org.illegaller.ratabb.hishoot2i.model.template.ApkV2Model;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_LOAD;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.logError;
import static org.illegaller.ratabb.hishoot2i.utils.GsonUtils.fromJson;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getStringFilePath;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.openStreamFromAsset;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.createPoint;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.tryClose;

public class ApkV2Builder extends BaseBuilder {

  public ApkV2Builder(Context context, String packageName) {
    super((context));
    final String errorMessage = String.format(Locale.US, MESSAGE_TEMPLATE_CANT_LOAD, packageName);
    this.id = packageName;
    this.type = TemplateType.APK_V2;
    ApkV2Model modelV2 = null;
    try {
      final InputStream stream = openStreamFromAsset(getContext(), packageName, "template.cfg");
      modelV2 = parsingAndCloseStream(stream);
      this.previewFile = getStringFilePath(getContext(), packageName, "preview");
      this.frameFile = getStringFilePath(getContext(), packageName, "frame");
      this.glareFile = getStringFilePath(getContext(), packageName, "glare");
      this.shadowFile = getStringFilePath(getContext(), packageName, "shadow");
    } catch (IOException | PackageManager.NameNotFoundException e) {
      logError(errorMessage, e);
    }
    if (modelV2 != null) {
      this.name = modelV2.name;
      this.author = modelV2.author;
      this.templatePoint = createPoint(modelV2.template_width, modelV2.template_height);
      this.leftTop = createPoint(modelV2.left_top_x, modelV2.left_top_y);
      this.rightTop = createPoint(modelV2.right_top_x, modelV2.right_top_y);
      this.leftBottom = createPoint(modelV2.left_bottom_x, modelV2.left_bottom_y);
      this.rightBottom = createPoint(modelV2.right_bottom_x, modelV2.right_bottom_y);
      this.isSuccessBuild = true;
    } else {
      logError(errorMessage, new RuntimeException(errorMessage));
    }
  }

  @Override public Template build() throws Exception {
    if (this.isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  @Nullable private ApkV2Model parsingAndCloseStream(InputStream inputStream) {
    String result = null;
    BufferedReader reader = null;
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      String line;
      reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append('\n');
      }
      result = stringBuilder.toString();
    } catch (IOException e) {
      logError("parsingAndCloseStream", e);
    } finally {
      tryClose(reader, inputStream);
    }
    return fromJson(result, ApkV2Model.class);
  }
}