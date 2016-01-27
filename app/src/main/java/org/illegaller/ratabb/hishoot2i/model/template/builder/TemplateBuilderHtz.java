package org.illegaller.ratabb.hishoot2i.model.template.builder;

import com.google.gson.Gson;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.ModelHtz;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class TemplateBuilderHtz extends AbstractTemplateBuilder {
    public static final String HTZ_FILE_CFG = "template.cfg";
    private static final int BUFFER_SIZE = 1024;
    private String htzName;
    private Context mContext;
    @Nullable private Callback mCallback;

    public TemplateBuilderHtz(Context context, @Nullable Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    public void setHtzName(String htzName) {
        this.htzName = htzName;
        this.id = getTemplateId(htzName);
    }

    @Override public Template build() {
        if (null == htzName || null == id) throw new RuntimeException("setHtzName(String) first");
        init();
        return super.build();
    }

    private void init() {
        type = TemplateType.HTZ;
        ModelHtz modelHtz = getModelHtzFrom(getHtzFileConfig());
        name = modelHtz.name;
        author = modelHtz.author;
        templateSizes = Sizes.create(modelHtz.template_width, modelHtz.template_height);
        previewFile = Utils.getStringFilePathHtz(currentPath(), modelHtz.preview);
        frameFile = Utils.getStringFilePathHtz(currentPath(), modelHtz.template_file);
        glareFile = Utils.getStringFilePathHtz(currentPath(), modelHtz.overlay_file);
        overlayOffset = Sizes.create(modelHtz.overlay_x, modelHtz.overlay_y);
        leftTop = Sizes.create(modelHtz.screen_x, modelHtz.screen_y);
        rightTop = Sizes.create(modelHtz.screen_x + modelHtz.screen_width, modelHtz.screen_y);
        leftBottom = Sizes.create(modelHtz.screen_x,
                modelHtz.screen_height + modelHtz.screen_y);
        rightBottom = Sizes.create(modelHtz.screen_width + modelHtz.screen_x,
                modelHtz.screen_height + modelHtz.screen_y);
    }

    private File currentPath() {
        return new File(AppConstants.getHishootHtzDir(mContext), id);
    }


    private File getHtzFileConfig() {
        return new File(currentPath(), HTZ_FILE_CFG);
    }


    private ModelHtz getModelHtzFrom(File json) {
        String result = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(json.getAbsolutePath()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            result = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getModelHtzFrom(result);
    }

    private ModelHtz getModelHtzFrom(final String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ModelHtz.class);
    }

    private String getTemplateId(String templateName) {
        String result = (templateName.contains(" ")) ? templateName.replace(" ", "_")
                : templateName;
        return result.toLowerCase(Locale.US).trim();
    }

    public boolean cekHtz(final String fileHtzPath) {
        if (!fileHtzPath.endsWith(".htz")) return false;
        boolean result;
        File file = null;
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                    new FileInputStream(fileHtzPath), BUFFER_SIZE));
            ZipEntry ze;
            InputStream fis;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equals(HTZ_FILE_CFG)) {
                    ZipFile zipFile = new ZipFile(fileHtzPath);
                    fis = zipFile.getInputStream(ze);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    ModelHtz htzModel = getModelHtzFrom(sb.toString());
                    fis.close();
                    reader.close();
                    zipFile.close();

                    file = new File(AppConstants.getHishootHtzDir(mContext), getTemplateId(htzModel.name));
                    if (!file.exists()) {
                        boolean ignored = file.mkdirs();
                    }
                    break;
                }
            }

            zis.close();

            if (file != null) new UnzipTask(fileHtzPath, file.getAbsolutePath()).execute();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
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
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(htzFile), BUFFER_SIZE));
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
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
            return outputFile;
        }

        @Override protected void onPostExecute(String result) {
            if (mCallback != null) mCallback.onDone(result);
        }
    }
}
