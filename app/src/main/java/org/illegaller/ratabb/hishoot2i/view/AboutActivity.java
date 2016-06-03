package org.illegaller.ratabb.hishoot2i.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.AboutFragment;

public class AboutActivity extends BaseActivity {

  public static boolean start(Context context) {
    Intent starter = new Intent(context, AboutActivity.class);
    context.startActivity(starter);
    return true;
  }

  @Override protected int getToolbarId() {
    return R.id.toolbar;
  }

  @Override protected int layoutRes() {
    return R.layout.activity_about;
  }

  @Override protected void setupToolbar(ActionBar actionBar) {
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();
    if (itemId == android.R.id.home) {
      onBackPressed();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.flContent, AboutFragment.newInstance())
        .commitAllowingStateLoss();
  }
}
