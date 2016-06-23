package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.content.pm.PackageManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_LOAD;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.logError;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getPointBitmapTemplate;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getStringFilePath;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.openStreamFromAsset;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.createPoint;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.tryClose;

public class ApkV1Builder extends BaseBuilder {
  public ApkV1Builder(Context context, String packageName) {
    super(context);
    final String errorMessage = String.format(Locale.US, MESSAGE_TEMPLATE_CANT_LOAD, packageName);
    this.id = packageName;
    this.type = TemplateType.APK_V1;
    TemplateXmlReader reader = null;
    InputStream inputStream = null;
    try {
      inputStream = openStreamFromAsset(getContext(), packageName, "keterangan.xml");
      reader = new TemplateXmlReader(inputStream);
      this.templatePoint = getPointBitmapTemplate(getContext(), packageName, "skin");
      this.previewFile = this.frameFile = getStringFilePath(getContext(), packageName, "skin");
    } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
      logError(errorMessage, e);
    } finally {
      tryClose(inputStream);
    }
    if (reader != null) {
      this.name = reader.device;
      this.author = reader.author;
      this.leftTop = createPoint(reader.tx, reader.ty);
      this.rightTop = createPoint(templatePoint.x - reader.bx, reader.ty);
      this.leftBottom = createPoint(reader.tx, templatePoint.y - reader.by);
      this.rightBottom = createPoint(templatePoint.x - reader.bx, templatePoint.y - reader.by);
      this.isSuccessBuild = true;
    } else {
      logError(errorMessage, null);
    }
  }

  @Override public Template build() throws Exception {
    if (this.isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  private final class TemplateXmlReader {
    String device, author = null;
    int tx, ty, bx, by;
    //int densType;

    TemplateXmlReader(InputStream inputStream) throws IOException, XmlPullParserException {
      final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      final XmlPullParser xpp = factory.newPullParser();
      xpp.setInput(inputStream, null);
      int eventType = xpp.getEventType();
      String value = null;
      while (eventType != XmlPullParser.END_DOCUMENT) {
        String xppName = xpp.getName();
        switch (eventType) {
          case XmlPullParser.START_DOCUMENT: // no-op
            break;
          case XmlPullParser.TEXT:
            value = xpp.getText();
            break;
          case XmlPullParser.END_TAG:
            if (null == value) continue;
            if (xppName.equalsIgnoreCase("device")) {
              this.device = value;
            } else if (xppName.equalsIgnoreCase("author")) {
              this.author = value;
            } else if (xppName.equalsIgnoreCase("topx")) {
              this.tx = parseInt(value);
            } else if (xppName.equalsIgnoreCase("topy")) {
              this.ty = parseInt(value);
            } else if (xppName.equalsIgnoreCase("botx")) {
              this.bx = parseInt(value);
            } else if (xppName.equalsIgnoreCase("boty")) this.by = parseInt(value);
            /*else if (xppName.equalsIgnoreCase("deviceDpi")) this.densType = parseInt(value);*/
            break;
          default:
            break;
        }
        eventType = xpp.nextToken();
      }
    }

    int parseInt(String value) {
      return Integer.parseInt(value);
    }
  }
}
