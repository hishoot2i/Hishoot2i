package org.illegaller.ratabb.hishoot2i.di

import core.CoreProcess
import core.CoreRequest
import core.MixTemplate
import core.SaveResult
import core.impl.CoreProcessImpl
import core.impl.CoreRequestImpl
import core.impl.MixTemplateImpl
import core.impl.SaveResultImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.modules.ApplicationContextModule
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(SingletonComponent::class)
interface BindCoreModule {
    @Binds
    @Singleton
    fun bindMixTemplate(impl: MixTemplateImpl): MixTemplate

    @Binds
    @Singleton
    fun bindSaveResult(impl: SaveResultImpl): SaveResult

    @Binds
    @Singleton
    fun bindCoreRequest(impl: CoreRequestImpl): CoreRequest

    @Binds
    @Singleton
    fun bindCoreProcess(impl: CoreProcessImpl): CoreProcess
}
