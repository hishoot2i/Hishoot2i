package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import com.chibatching.kotpref.Kotpref
import common.FileConstants
import common.egl.MaxTexture
import common.egl.MaxTextureCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.internal.modules.ApplicationContextModule
import dagger.hilt.android.qualifiers.ApplicationContext
import imageloader.uil.UilImageLoaderImpl
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcessImpl
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.AppScheduler
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import template.TemplateFactoryManagerImpl
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(ApplicationComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppPref(@ApplicationContext context: Context): AppPref {
        Kotpref.init(context)
        return AppPref()
    }

    @Provides
    @Singleton
    fun provideScheduler(): SchedulerProvider = AppScheduler

    @Provides
    @Singleton
    fun provideMaxTexture(): MaxTexture = MaxTextureCompat

    @Provides
    @Singleton
    fun provideFileConstants(impl: FileConstantsImpl): FileConstants = impl

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): imageloader.ImageLoader = UilImageLoaderImpl(context)

    @Provides
    @Singleton
    fun provideFactoryManager(
        @ApplicationContext context: Context,
        fileConstants: FileConstants
    ): template.TemplateFactoryManager = TemplateFactoryManagerImpl(context, fileConstants)

    @Provides
    @Singleton
    fun provideFileFontStorageSource(impl: FileFontStorageSourceImpl): FileFontStorageSource = impl

    @Provides
    @Singleton
    fun providePackageResolver(impl: PackageResolverImpl): PackageResolver = impl

    @Provides
    @Singleton
    fun provideTemplateDataSource(impl: TemplateDataSourceImpl): TemplateDataSource = impl

    @Provides
    @Singleton
    fun provideCoreProcess(impl: CoreProcessImpl): CoreProcess = impl
}
