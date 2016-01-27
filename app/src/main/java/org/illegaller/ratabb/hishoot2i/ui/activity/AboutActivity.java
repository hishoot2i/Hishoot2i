package org.illegaller.ratabb.hishoot2i.ui.activity;

import org.illegaller.ratabb.hishoot2i.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public class AboutActivity extends AbstractBaseActivity {


    public static Intent getIntent(final Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.HishootTheme);
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

}
