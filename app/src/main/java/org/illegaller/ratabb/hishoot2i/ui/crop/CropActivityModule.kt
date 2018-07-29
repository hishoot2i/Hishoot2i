package org.illegaller.ratabb.hishoot2i.ui.crop

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.illegaller.ratabb.hishoot2i.ui.common.BaseFragment

@Module
interface CropActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: CropActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun contributeUnusedFragment(): BaseFragment
}