package org.illegaller.ratabb.hishoot2i.ui.template

import android.support.v7.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.favorite.FavoriteFragment
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.installed.InstalledFragment

@Module
interface TemplateManagerActivityModule {
    @Binds
    fun providesAppCompatActivity(activity: TemplateManagerActivity): AppCompatActivity

    @ContributesAndroidInjector
    fun contributeInstalledFragment(): InstalledFragment

    @ContributesAndroidInjector
    fun contributeFavoriteFragment(): FavoriteFragment

    @ContributesAndroidInjector
    fun contributeSortTemplateDialog(): SortTemplateDialog
}