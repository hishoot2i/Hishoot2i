package org.illegaller.ratabb.hishoot2i.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.internal.modules.ApplicationContextModule
import org.illegaller.ratabb.hishoot2i.data.pref.BackgroundToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.BadgeToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.ScreenToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplatePref
import org.illegaller.ratabb.hishoot2i.data.pref.TemplateToolPref
import org.illegaller.ratabb.hishoot2i.data.pref.impl.BackgroundToolPrefImpl
import org.illegaller.ratabb.hishoot2i.data.pref.impl.BadgeToolPrefImpl
import org.illegaller.ratabb.hishoot2i.data.pref.impl.ScreenToolPrefImpl
import org.illegaller.ratabb.hishoot2i.data.pref.impl.SettingPrefImpl
import org.illegaller.ratabb.hishoot2i.data.pref.impl.TemplatePrefImpl
import org.illegaller.ratabb.hishoot2i.data.pref.impl.TemplateToolPrefImpl
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(ApplicationComponent::class)
interface PrefModule {
    @Binds
    @Singleton
    fun bindBackgroundToolPref(impl: BackgroundToolPrefImpl): BackgroundToolPref

    @Binds
    @Singleton
    fun bindBadgeToolPref(impl: BadgeToolPrefImpl): BadgeToolPref

    @Binds
    @Singleton
    fun bindScreenToolPref(impl: ScreenToolPrefImpl): ScreenToolPref

    @Binds
    @Singleton
    fun bindSettingPref(impl: SettingPrefImpl): SettingPref

    @Binds
    @Singleton
    fun bindTemplatePref(impl: TemplatePrefImpl): TemplatePref

    @Binds
    @Singleton
    fun bindTemplateToolPref(impl: TemplateToolPrefImpl): TemplateToolPref
}
