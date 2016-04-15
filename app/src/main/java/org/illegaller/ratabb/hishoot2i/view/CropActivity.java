package org.illegaller.ratabb.hishoot2i.view;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import org.illegaller.ratabb.hishoot2i.view.widget.CropImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CropActivity extends AppCompatActivity {
    private static final String KEY_PATH_IMAGE = "path_image";
    private static final String KEY_POINT_RATIO = "point_ratio";
    @InjectExtra(KEY_PATH_IMAGE) String pathImage;
    @InjectExtra(KEY_POINT_RATIO) Point pointRatio;
    @Bind(R.id.cropImageVIew) CropImageView mCropImageView;

    public static Intent getIntent(Context context, String path, Point ratio) {
        Intent starter = new Intent(context, CropActivity.class);
        starter.putExtra(KEY_PATH_IMAGE, path);
        starter.putExtra(KEY_POINT_RATIO, ratio);
        starter.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        return starter;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);
        Dart.inject(this);
        mCropImageView.setCustomRatio(pointRatio.x, pointRatio.y);
        UILHelper.displayPreview(mCropImageView, pathImage);
    }

    @OnClick({R.id.btnOkCrop, R.id.btnCancelCrop}) void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.btnOkCrop) doSaveCrop();
        else if (viewId == R.id.btnCancelCrop) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    void doSaveCrop() {
        final Bitmap bitmap = mCropImageView.getCroppedBitmap();
        new AsyncTask<Void, Void, Uri>() {
            @Override protected Uri doInBackground(Void... voids) {
                try {
                    File file = Utils.saveTempBackgroundCrop(CropActivity.this, bitmap);
                    return Uri.fromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override protected void onPostExecute(Uri uri) {
                Intent data = new Intent();
                data.setData(uri);
                CropActivity.this.setResult(Activity.RESULT_OK, data);
                CropActivity.this.finish();
            }
        }.execute();

    }
}
