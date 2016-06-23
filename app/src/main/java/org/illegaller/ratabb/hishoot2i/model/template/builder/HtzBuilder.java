package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.illegaller.ratabb.hishoot2i.model.template.HtzModel;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;

import static org.illegaller.ratabb.hishoot2i.AppConstants.getHishootHtzDir;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.log;
import static org.illegaller.ratabb.hishoot2i.utils.CrashLog.logError;
import static org.illegaller.ratabb.hishoot2i.utils.GsonUtils.fromJson;
import static org.illegaller.ratabb.hishoot2i.utils.ResUtils.getStringFilePathHtz;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.createPoint;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.tryClose;

public class HtzBuilder extends BaseBuilder {
  public static final String HTZ_FILE_CFG = "template.cfg";
  private static final int BUFFER_SIZE = 1024;
  private String mHtzName;
  /** for import Htz */
  private Callback mCallback;

  public HtzBuilder(Context context) {
    super(context);
  }

  public void setCallback(Callback mCallback) {
    this.mCallback = mCallback;
  }

  public void setHtzName(String mHtzName) {
    this.mHtzName = mHtzName;
    init();
  }

  @Override public Template build() throws Exception {
    checkNotNull(this.mHtzName, "HtzName == null");
    if (this.isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  private void init() {
    this.id = getTemplateHtzId(mHtzName);
    this.type = TemplateType.HTZ;
    final HtzModel htz = getModelHtzFrom(getHtzFileConfig());
    if (htz != null) {
      this.name = htz.name;
      this.author = htz.author;
      this.templatePoint = createPoint(htz.template_width, htz.template_height);
      this.previewFile = getStringFilePathHtz(currentPath(), htz.preview);
      this.frameFile = getStringFilePathHtz(currentPath(), htz.template_file);
      this.glareFile = getStringFilePathHtz(currentPath(), htz.overlay_file);
      this.overlayOffset = createPoint(htz.overlay_x, htz.overlay_y);
      this.leftTop = createPoint(htz.screen_x, htz.screen_y);
      this.rightTop = createPoint(htz.screen_x + htz.screen_width, htz.screen_y);
      this.leftBottom = createPoint(htz.screen_x, htz.screen_height + htz.screen_y);
      this.rightBottom =
          createPoint(htz.screen_width + htz.screen_x, htz.screen_height + htz.screen_y);
      this.isSuccessBuild = true;
    } else {
      logError("init htz builder", null);
    }
  }

  private File currentPath() {
    return new File(getHishootHtzDir(getContext()), id);
  }

  private File getHtzFileConfig() {
    return new File(currentPath(), HTZ_FILE_CFG);
  }

  @Nullable private HtzModel getModelHtzFrom(File json) {
    String result = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(json), "iso-8859-1"));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line).append('\n');
      }
      result = sb.toString();
    } catch (IOException e) {
      logError("getModelHtzFrom", e);
    } finally {
      tryClose(reader);
    }
    return fromJson(result, HtzModel.class);
  }

  private String getTemplateHtzId(String templateName) {
    String result = (templateName.contains(" ")) ? templateName.replace(" ", "_") : templateName;
    return result.toLowerCase(Locale.US).trim();
  }

  /** for import Htz */
  public boolean importHtz(final String fileHtzPath) {
    if (!fileHtzPath.endsWith(".htz")) return false;
    boolean result;
    File file = null;
    ZipInputStream zis = null;
    try {
      zis = new ZipInputStream(
          new BufferedInputStream(new FileInputStream(fileHtzPath), BUFFER_SIZE));
      ZipEntry ze;
      while ((ze = zis.getNextEntry()) != null) {
        if (ze.getName().equals(HTZ_FILE_CFG)) {
          ZipFile zipFile = new ZipFile(fileHtzPath);
          InputStream fis = zipFile.getInputStream(ze);
          BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "iso-8859-1"), 8);
          StringBuilder sb = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
          }
          tryClose(fis, reader, zipFile);
          HtzModel htzModel = fromJson(sb.toString(), HtzModel.class);
          if (htzModel == null) continue;
          file = new File(getHishootHtzDir(getContext()), getTemplateHtzId(htzModel.name));
          if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            log(String.valueOf(mkdirs));
          }
          break;
        }
      }
      if (file != null) new UnzipTask(fileHtzPath, file.getAbsolutePath()).execute();
      result = true;
    } catch (IOException e) {
      logError("importHtz", e);
      result = false;
    } finally {
      tryClose(zis);
    }
    return result;
  }

  interface Callback {
    void onDone(final String result);
  }

  private class UnzipTask extends AsyncTask<Void, Void, String> {
    private final String mHtzFile;
    private final String mOutputFile;

    private UnzipTask(String mHtzFile, String mOutputFile) {
      this.mHtzFile = mHtzFile;
      this.mOutputFile = mOutputFile;
    }

    @Override protected String doInBackground(Void... voids) {
      int size;
      byte[] buffer = new byte[BUFFER_SIZE];
      try {
        final ZipInputStream zis =
            new ZipInputStream(new BufferedInputStream(new FileInputStream(mHtzFile), BUFFER_SIZE));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
          final File unzipFile = new File(mOutputFile, ze.getName());
          final FileOutputStream out = new FileOutputStream(unzipFile, false);
          final BufferedOutputStream outputStream = new BufferedOutputStream(out, BUFFER_SIZE);
          try {
            while ((size = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
              outputStream.write(buffer, 0, size);
            }
            zis.closeEntry();
          } finally {
            try {
              outputStream.flush();
              outputStream.close();
            } catch (IOException e) {
              logError("stream flush close", e);
            }
          }
        }
        zis.close();
      } catch (FileNotFoundException e) {
        logError("importHtz", e);
        return null;
      } catch (IOException e) {
        logError("importHtz", e);
        return null;
      } catch (NullPointerException e) {
        logError("importHtz", e);
        return null;
      }
      return mOutputFile;
    }

    @Override protected void onPostExecute(String result) {
      if (mCallback != null) mCallback.onDone(result);
    }
  }
}
