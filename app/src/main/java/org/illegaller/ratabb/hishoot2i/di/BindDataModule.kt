package org.illegaller.ratabb.hishoot2i.di

import common.FileConstants
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.internal.modules.ApplicationContextModule
import org.illegaller.ratabb.hishoot2i.data.PackageResolver
import org.illegaller.ratabb.hishoot2i.data.PackageResolverImpl
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.MixTemplate
import org.illegaller.ratabb.hishoot2i.data.core.SaveResult
import org.illegaller.ratabb.hishoot2i.data.core.impl.CoreProcessImpl
import org.illegaller.ratabb.hishoot2i.data.core.impl.MixTemplateImpl
import org.illegaller.ratabb.hishoot2i.data.core.impl.SaveResultImpl
import org.illegaller.ratabb.hishoot2i.data.source.FileFontStorageSource
import org.illegaller.ratabb.hishoot2i.data.source.FileFontStorageSourceImpl
import org.illegaller.ratabb.hishoot2i.data.source.TemplateDataSource
import org.illegaller.ratabb.hishoot2i.data.source.TemplateDataSourceImpl
import org.illegaller.ratabb.hishoot2i.provider.FileConstantsImpl
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(ApplicationComponent::class)
interface BindDataModule {
    @Binds
    @Singleton
    fun bindFileFontStorageSource(impl: FileFontStorageSourceImpl): FileFontStorageSource

    @Binds
    @Singleton
    fun bindPackageResolver(impl: PackageResolverImpl): PackageResolver

    @Binds
    @Singleton
    fun bindTemplateDataSource(impl: TemplateDataSourceImpl): TemplateDataSource

    @Binds
    @Singleton
    fun bindMixTemplate(impl: MixTemplateImpl): MixTemplate

    @Binds
    @Singleton
    fun bindSaveResult(impl: SaveResultImpl): SaveResult

    @Binds
    @Singleton
    fun bindCoreProcess(impl: CoreProcessImpl): CoreProcess

    @Binds
    @Singleton
    fun bindFileConstants(impl: FileConstantsImpl): FileConstants
}
