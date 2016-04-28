package org.illegaller.ratabb.hishoot2i.model.template.builder;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.template.HtzModel;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.GsonUtils;
import org.illegaller.ratabb.hishoot2i.utils.ResUtils;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

public class HtzBuilder extends BaseBuilder {
  public static final String HTZ_FILE_CFG = "template.cfg";
  private static final int BUFFER_SIZE = 1024;
  private String htzName;
  private Context mContext;
  @Nullable private Callback mCallback;

  public HtzBuilder(Context context, @Nullable Callback callback) {
    this.mContext = context;
    this.mCallback = callback;
  }

  public void setHtzName(String htzName) {
    this.htzName = htzName;
    this.id = getTemplateId(htzName);
    init();
  }

  @Override public Template build() {
    if (null == htzName || null == id) throw new RuntimeException("setHtzName(:String) first");
    if (isSuccessBuild) {
      return Template.build(this);
    } else {
      return null;
    }
  }

  private void init() {
    type = TemplateType.HTZ;
    HtzModel modelHtz = getModelHtzFrom(getHtzFileConfig());
    if (modelHtz == null) {
      CrashLog.logError("init htz builder", null);
    } else {
      name = modelHtz.name;
      author = modelHtz.author;
      templatePoint = new Point(modelHtz.template_width, modelHtz.template_height);
      previewFile = ResUtils.getStringFilePathHtz(currentPath(), modelHtz.preview);
      frameFile = ResUtils.getStringFilePathHtz(currentPath(), modelHtz.template_file);
      glareFile = ResUtils.getStringFilePathHtz(currentPath(), modelHtz.overlay_file);
      overlayOffset = new Point(modelHtz.overlay_x, modelHtz.overlay_y);
      leftTop = new Point(modelHtz.screen_x, modelHtz.screen_y);
      rightTop = new Point(modelHtz.screen_x + modelHtz.screen_width, modelHtz.screen_y);
      leftBottom = new Point(modelHtz.screen_x, modelHtz.screen_height + modelHtz.screen_y);
      rightBottom = new Point(modelHtz.screen_width + modelHtz.screen_x,
          modelHtz.screen_height + modelHtz.screen_y);
      isSuccessBuild = true;
    }
  }

  private File currentPath() {
    return new File(AppConstants.getHishootHtzDir(mContext), id);
  }

  private File getHtzFileConfig() {
    return new File(currentPath(), HTZ_FILE_CFG);
  }

  @Nullable private HtzModel getModelHtzFrom(File json) {
    String result = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(json.getAbsolutePath()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) sb.append(line).append("\n");
      result = sb.toString();
    } catch (IOException e) {
      CrashLog.logError("getModelHtzFrom", e);
    } finally {
      Utils.tryClose(reader);
    }
    return GsonUtils.fromJson(result, HtzModel.class);
  }

  private String getTemplateId(String templateName) {
    String result = (templateName.contains(" ")) ? templateName.replace(" ", "_") : templateName;
    return result.toLowerCase(Locale.US).trim();
  }

  public boolean cekHtz(final String fileHtzPath) {
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
          while ((line = reader.readLine()) != null) sb.append(line).append("\n");
          Utils.tryClose(fis, reader, zipFile);
          HtzModel htzModel = GsonUtils.fromJson(sb.toString(), HtzModel.class);
          if (htzModel == null) continue;
          file = new File(AppConstants.getHishootHtzDir(mContext), getTemplateId(htzModel.name));
          if (!file.exists()) file.mkdirs();
          break;
        }
      }
      if (file != null) new UnzipTask(fileHtzPath, file.getAbsolutePath()).execute();
      result = true;
    } catch (IOException e) {
      CrashLog.logError("cekHtz", e);
      result = false;
    } finally {
      Utils.tryClose(zis);
    }
    return result;
  }

  public interface Callback {
    void onDone(final String result);
  }

  class UnzipTask extends AsyncTask<Void, Void, String> {
    private final String htzFile;
    private final String outputFile;

    private UnzipTask(String htzFile, String outputFile) {
      this.htzFile = htzFile;
      this.outputFile = outputFile;
    }

    @Override protected String doInBackground(Void... voids) {
      int size;
      byte[] buffer = new byte[BUFFER_SIZE];
      try {
        ZipInputStream zis =
            new ZipInputStream(new BufferedInputStream(new FileInputStream(htzFile), BUFFER_SIZE));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
          File unzipFile = new File(outputFile, ze.getName());
          FileOutputStream out = new FileOutputStream(unzipFile, false);
          BufferedOutputStream outputStream = new BufferedOutputStream(out, BUFFER_SIZE);
          try {
            while ((size = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
              outputStream.write(buffer, 0, size);
            }
            zis.closeEntry();
          } finally {
            outputStream.flush();
            outputStream.close();
          }
        }
        zis.close();
      } catch (FileNotFoundException e) {
        CrashLog.logError("cekHtz", e);
        return null;
      } catch (IOException e) {
        CrashLog.logError("cekHtz", e);
        return null;
      } catch (NullPointerException e) {
        CrashLog.logError("cekHtz", e);
        return null;
      }
      return outputFile;
    }

    @Override protected void onPostExecute(String result) {
      if (mCallback != null) mCallback.onDone(result);
    }
  }
}
