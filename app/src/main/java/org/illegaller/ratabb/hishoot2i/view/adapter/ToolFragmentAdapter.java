package org.illegaller.ratabb.hishoot2i.view.adapter;

import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BackgroundToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BadgeToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.ScreenToolFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ToolFragmentAdapter extends FragmentStatePagerAdapter {
    public ToolFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return ScreenToolFragment.newInstance();
            case 1:
                return BackgroundToolFragment.newInstance();
            case 2:
                return BadgeToolFragment.newInstance();
        }
    }

    @Override public int getCount() {
        return 3;
    }
}
