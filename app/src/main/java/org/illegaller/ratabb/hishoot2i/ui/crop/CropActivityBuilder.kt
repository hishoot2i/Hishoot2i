package org.illegaller.ratabb.hishoot2i.ui.crop

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CropActivityBuilder {
    @ContributesAndroidInjector(modules = [CropActivityModule::class])
    fun contributeCropActivity(): CropActivity
}