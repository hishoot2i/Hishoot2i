package org.illegaller.ratabb.hishoot2i.ui.navigation;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;


public class NavigationFragment {

    private static WeakReference<FragmentManager> sWeakFragmentManager = null;
    @IdRes private final int mDefContainer;

    public NavigationFragment(@NonNull FragmentManager fragmentManager, @IdRes int defContainer) {
        sWeakFragmentManager = new WeakReference<>(fragmentManager);
        this.mDefContainer = defContainer;
    }

    protected String getName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    protected void replace(final Fragment fragment) {
        sWeakFragmentManager.get().beginTransaction()
                .replace(mDefContainer, fragment, getName(fragment))
                .commit();
        sWeakFragmentManager.get().executePendingTransactions();
    }

    protected void clear() {
        while (sWeakFragmentManager.get().popBackStackImmediate()) ;
    }

    public int getSize() {
        final FragmentManager fragmentManager = sWeakFragmentManager.get();
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
        sWeakFragmentManager.get().beginTransaction().addToBackStack(getName(fragment))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(mDefContainer, fragment, getName(fragment))
                .commitAllowingStateLoss();
        sWeakFragmentManager.get().executePendingTransactions();
    }

    public void goOneBack() {
        sWeakFragmentManager.get().popBackStackImmediate();
    }

    public Fragment getCurrent() {
        if (isEmpty()) return null;
        String tag = sWeakFragmentManager.get().getBackStackEntryAt(getSize() - 1).getName();
        return sWeakFragmentManager.get().findFragmentByTag(tag);
    }
}
