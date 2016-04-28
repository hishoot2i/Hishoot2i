package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import static org.illegaller.ratabb.hishoot2i.AppConstants.MESSAGE_TEMPLATE_CANT_LOAD;

public class ApkV1Builder extends BaseBuilder {
  public ApkV1Builder(Context context, String packageName) {
    final String errorMessage = String.format(Locale.US, MESSAGE_TEMPLATE_CANT_LOAD, packageName);
    id = packageName;
    type = TemplateType.APK_V1;
    TemplateXmlReader reader = null;
    InputStream inputStream = null;
    try {
      inputStream = ResUtils.openStreamFromAsset(context, packageName, "keterangan.xml");
      reader = new TemplateXmlReader(inputStream);
      templatePoint = ResUtils.getPointBitmapTemplate(context, packageName, "skin");
      previewFile = frameFile = ResUtils.getStringFilePath(context, packageName, "skin");
    } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
      CrashLog.logError(errorMessage, e);
    } finally {
      Utils.tryClose(inputStream);
    }
    if (reader == null) {
      CrashLog.logError(errorMessage, null);
    } else {
      name = reader.device;
      author = reader.author;
      leftTop = new Point(reader.tx, reader.ty);
      rightTop = new Point(templatePoint.x - reader.bx, reader.ty);
      leftBottom = new Point(reader.tx, templatePoint.y - reader.by);
      rightBottom = new Point(templatePoint.x - reader.bx, templatePoint.y - reader.by);
      isSuccessBuild = true;
    }
  }

  @Override public Template build() {
    if (isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  private class TemplateXmlReader {
    String device, author = null;
    int tx, ty, bx, by;
    //int densType;

    TemplateXmlReader(InputStream inputStream) throws IOException, XmlPullParserException {
      String value = null;
      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      XmlPullParser xpp = factory.newPullParser();
      xpp.setInput(inputStream, null);
      int eventType = xpp.getEventType();
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
