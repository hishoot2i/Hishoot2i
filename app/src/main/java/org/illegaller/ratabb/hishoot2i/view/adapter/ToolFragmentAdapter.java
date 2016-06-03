package org.illegaller.ratabb.hishoot2i.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BackgroundToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BadgeToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.ScreenToolFragment;

public class ToolFragmentAdapter extends FragmentStatePagerAdapter {
  private final Fragment[] mFragments;

  public ToolFragmentAdapter(FragmentManager fm) {
    super(fm);
    mFragments = new Fragment[] {
        ScreenToolFragment.newInstance(), BackgroundToolFragment.newInstance(),
        BadgeToolFragment.newInstance()
    };
  }

  @Override public Fragment getItem(int position) {
    return mFragments[position];
  }

  @Override public int getCount() {
    return mFragments.length;
  }
}
