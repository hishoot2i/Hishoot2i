@file:Suppress("unused")

package org.illegaller.ratabb.hishoot2i.di

import common.FileConstants
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.modules.ApplicationContextModule
import dagger.hilt.components.SingletonComponent
import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolverImpl
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolverImpl
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSource
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSourceImpl
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSourceImpl
import org.illegaller.ratabb.hishoot2i.provider.FileConstantsImpl
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(SingletonComponent::class)
interface BindDataModule {
    @Binds
    @Singleton
    fun bindFileFontStorageSource(impl: FileFontSourceImpl): FileFontSource

    @Binds
    @Singleton
    fun bindPackageResolver(impl: PackageResolverImpl): PackageResolver

    @Binds
    @Singleton
    fun bindHtzResolver(impl: HtzResolverImpl): HtzResolver

    @Binds
    @Singleton
    fun bindTemplateDataSource(impl: TemplateSourceImpl): TemplateSource

    @Binds
    @Singleton
    fun bindFileConstants(impl: FileConstantsImpl): FileConstants
}
