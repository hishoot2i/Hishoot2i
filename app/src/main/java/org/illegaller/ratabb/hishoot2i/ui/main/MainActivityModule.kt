package org.illegaller.ratabb.hishoot2i.ui.main

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.illegaller.ratabb.hishoot2i.ui.main.tools.background.BackgroundTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.badge.BadgeTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.screen.ScreenTool
import org.illegaller.ratabb.hishoot2i.ui.main.tools.template.TemplateTool

@Module
interface MainActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: MainActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun contributeTemplateTool(): TemplateTool

    @ContributesAndroidInjector
    fun contributeScreenTool(): ScreenTool

    @ContributesAndroidInjector
    fun contributeBackgroundTool(): BackgroundTool

    @ContributesAndroidInjector
    fun contributeBadgeTool(): BadgeTool

    @ContributesAndroidInjector
    fun contributeColorMix(): ColorMixDialog
}