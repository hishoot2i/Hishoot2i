package org.illegaller.ratabb.hishoot2i.ui.setting

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingActivityModule {
    @Binds
    fun provideAppCompatActivity(activity: SettingActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun provideSettingFragment(): SettingFragment
}