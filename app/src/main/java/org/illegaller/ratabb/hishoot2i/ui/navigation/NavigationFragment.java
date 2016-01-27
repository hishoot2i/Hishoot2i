package org.illegaller.ratabb.hishoot2i.ui.navigation;

import org.illegaller.ratabb.hishoot2i.utils.HLog;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;


public class NavigationFragment {

    @IdRes private final int mDefContainer;
    private WeakReference<FragmentManager> weakFragmentManager = null;

    public NavigationFragment(@NonNull FragmentManager fragmentManager, @IdRes int defContainer) {
        this.weakFragmentManager = new WeakReference<>(fragmentManager);
        this.mDefContainer = defContainer;
    }

    protected FragmentManager getFragmentManager() {
        return weakFragmentManager.get();
    }

    protected String getName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    protected void replace(final Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(mDefContainer, fragment, getName(fragment))
                .commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
    }

    protected void clear() {
        while (getFragmentManager().popBackStackImmediate()) {
            //no-op
            HLog.d("");
        }
    }

    public int getSize() {
        final FragmentManager fragmentManager = getFragmentManager();
        if (null == fragmentManager) return 0;
        else return fragmentManager.getBackStackEntryCount();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void setRoot(final Fragment fragment) {
        if (!isEmpty()) clear();
        replace(fragment);
    }

    public void goTo(final Fragment fragment) {
        getFragmentManager().beginTransaction().addToBackStack(getName(fragment))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(mDefContainer, fragment, getName(fragment))
                .commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
    }

    public void goOneBack() {
        getFragmentManager().popBackStackImmediate();
    }

//    public Fragment getCurrent() {
//        if (isEmpty()) return null;
//        String tag = getFragmentManager().getBackStackEntryAt(getSize() - 1).getName();
//        return getFragmentManager().findFragmentByTag(tag);
//    }
}
