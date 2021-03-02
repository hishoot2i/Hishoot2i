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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.HtzResolverImpl
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolver
import org.illegaller.ratabb.hishoot2i.data.resolver.PackageResolverImpl
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSource
import org.illegaller.ratabb.hishoot2i.data.source.FileFontSourceImpl
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSource
import org.illegaller.ratabb.hishoot2i.data.source.TemplateSourceImpl

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @ViewModelScoped
    fun bindPackageResolver(impl: PackageResolverImpl): PackageResolver

    @Binds
    @ViewModelScoped
    fun bindHtzResolver(impl: HtzResolverImpl): HtzResolver

    @Binds
    @ViewModelScoped
    fun bindFileFontSource(impl: FileFontSourceImpl): FileFontSource

    @Binds
    @ViewModelScoped
    fun bindTemplateSource(impl: TemplateSourceImpl): TemplateSource

    @Binds
    @ViewModelScoped
    fun bindMixTemplate(impl: MixTemplateImpl): MixTemplate

    @Binds
    @ViewModelScoped
    fun bindSaveResult(impl: SaveResultImpl): SaveResult

    @Binds
    @ViewModelScoped
    fun bindCoreRequest(impl: CoreRequestImpl): CoreRequest

    @Binds
    @ViewModelScoped
    fun bindCoreProcess(impl: CoreProcessImpl): CoreProcess
}
