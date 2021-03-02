@file:Suppress("unused")

package org.illegaller.ratabb.hishoot2i.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import org.illegaller.ratabb.hishoot2i.ui.main.SaveNotification
import org.illegaller.ratabb.hishoot2i.ui.main.SaveNotificationImpl

@Module
@InstallIn(FragmentComponent::class)
interface UiModule {
    @Binds
    @FragmentScoped
    fun bindSaveNotification(impl: SaveNotificationImpl): SaveNotification
}
