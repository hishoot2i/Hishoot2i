package org.illegaller.ratabb.hishoot2i.ui.activity;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.ir.TemplateUsedID;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.builder.TemplateBuilderHtz;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;

public class ImportHtzActivity extends AbstractBaseActivity implements TemplateBuilderHtz.Callback {
    @Bind(R.id.ivTemplate) ImageView mImageView;
    @Bind(R.id.tvTemplateName) TextView mTemplateName;
    @Bind(R.id.tvTemplateAuthor) TextView mTemplateAuthor;

    @Inject @TemplateUsedID StringPreference templateUsedIdPref;
    private TemplateBuilderHtz builder;
    private boolean hasChecked;

    public static Intent getIntent(final Context context) {
        return new Intent(context, ImportHtzActivity.class);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_import_htz);
        checkData();
    }

    @Override protected void setToolbar() {
        super.setToolbar();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void checkData() {
        Uri uri = getIntent().getData();
        final String dataPath = uri.getPath();
        if (!dataPath.endsWith(".htz")) finish();
        else {
            builder = new TemplateBuilderHtz(this, this);
            hasChecked = builder.cekHtz(dataPath);
        }
    }


    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        if (builder != null) templateUsedIdPref.set(builder.id);
        super.onBackPressed();
    }


    @Override public void onDone(final String result) {
        if (!hasChecked) {
            finish();
            return;
        }
        builder.setHtzName(new File(result).getName());
        Template template = builder.build();
        mTemplateName.setText(getString(R.string.names, template.name));
        mTemplateAuthor.setText(getString(R.string.authors, template.author));

        UILHelper.displayPreview(mImageView, template.previewFile);
    }


}
