package org.illegaller.ratabb.hishoot2i.ui.activity;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.io.File;

import butterknife.OnClick;

public class ReceiverActivity extends AbstractBaseActivity {

    private static String sImagePath;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.HishootTheme);
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_receiver);
        Intent intent = getIntent();
        if (null != intent && Intent.ACTION_SEND.equals(intent.getAction())) {
            final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            final String stringUri = Utils.getStringFromUri(this, imageUri);
            sImagePath = UILHelper.stringFiles(new File(stringUri));
        } else {
            finish();
        }
    }

    @OnClick({R.id.btnScreen, R.id.btnBackground}) void onClick(View view) {
        final int btnID = view.getId();
        if (btnID == R.id.btnScreen) onReceiveImage(ImageReceive.TYPE_SS);
        else if (btnID == R.id.btnBackground) onReceiveImage(ImageReceive.TYPE_BG);

    }

    private void onReceiveImage(int imageType) {
        if (sImagePath == null) {
            Log.e("ReceiverActivity", "failed get image path");
            finish();
            return;
        }
        ImageReceive imageReceive = new ImageReceive(sImagePath, imageType);
        MainActivity.handleImageReceive(this, imageReceive);

    }

}
