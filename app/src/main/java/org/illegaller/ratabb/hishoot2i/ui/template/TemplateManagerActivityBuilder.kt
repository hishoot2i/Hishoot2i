package org.illegaller.ratabb.hishoot2i.ui.template

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface TemplateManagerActivityBuilder {
    @ContributesAndroidInjector(modules = [TemplateManagerActivityModule::class])
    fun contributeTemplateManagerActivity(): TemplateManagerActivity
}