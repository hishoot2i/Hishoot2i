package org.illegaller.ratabb.hishoot2i.ui.setting

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface SettingActivityBuilder {
    @ContributesAndroidInjector(modules = [SettingActivityModule::class])
    fun contibuteSettingActivity(): SettingActivity
}