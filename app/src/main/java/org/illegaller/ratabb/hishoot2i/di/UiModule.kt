package org.illegaller.ratabb.hishoot2i.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import org.illegaller.ratabb.hishoot2i.ui.crop.CropPresenter
import org.illegaller.ratabb.hishoot2i.ui.crop.CropPresenterImpl
import org.illegaller.ratabb.hishoot2i.ui.main.MainPresenter
import org.illegaller.ratabb.hishoot2i.ui.main.MainPresenterImpl
import org.illegaller.ratabb.hishoot2i.ui.main.SaveNotification
import org.illegaller.ratabb.hishoot2i.ui.main.SaveNotificationImpl
import org.illegaller.ratabb.hishoot2i.ui.template.TemplatePresenter
import org.illegaller.ratabb.hishoot2i.ui.template.TemplatePresenterImpl
import org.illegaller.ratabb.hishoot2i.ui.tools.badge.BadgeToolPresenter
import org.illegaller.ratabb.hishoot2i.ui.tools.badge.BadgeToolPresenterImpl
import org.illegaller.ratabb.hishoot2i.ui.tools.template.TemplateToolPresenter
import org.illegaller.ratabb.hishoot2i.ui.tools.template.TemplateToolPresenterImpl

@Module
@InstallIn(FragmentComponent::class)
interface UiModule {
    @Binds
    @FragmentScoped
    fun bindSaveNotification(impl: SaveNotificationImpl): SaveNotification

    @Binds
    @FragmentScoped
    fun bindMainPresenter(impl: MainPresenterImpl): MainPresenter

    @Binds
    @FragmentScoped
    fun bindCropPresenter(impl: CropPresenterImpl): CropPresenter

    @Binds
    @FragmentScoped
    fun bindTemplatePresenter(impl: TemplatePresenterImpl): TemplatePresenter

    @Binds
    @FragmentScoped
    fun bindBadgeToolPresenter(impl: BadgeToolPresenterImpl): BadgeToolPresenter

    @Binds
    @FragmentScoped
    fun bindTemplateToolPresenter(impl: TemplateToolPresenterImpl): TemplateToolPresenter
}