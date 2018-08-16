package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import com.chibatching.kotpref.Kotpref
import common.FileConstants
import common.egl.MaxTexture
import common.egl.MaxTextureCompat
import dagger.Module
import dagger.Provides
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcessImpl
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.AppScheduler
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import javax.inject.Singleton

@Module
object DataModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideAppPref(context: Context): AppPref {
        Kotpref.init(context)
        return AppPref()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideScheduler(): SchedulerProvider = AppScheduler

    @Provides
    @Singleton
    @JvmStatic
    fun provideMaxTexture(): MaxTexture = MaxTextureCompat

    @Provides
    @Singleton
    @JvmStatic
    fun provideFileConstants(impl: FileConstantsImpl): FileConstants = impl

    @Provides
    @Singleton
    @JvmStatic
    fun provideImageLoader(
        impl: imageloader.uil.UilImageLoaderImpl
    ): imageloader.ImageLoader = impl

    @Provides
    @Singleton
    @JvmStatic
    fun provideFactoryManager(
        impl: template.TemplateFactoryManagerImpl
    ): template.TemplateFactoryManager = impl

    @Provides
    @Singleton
    @JvmStatic
    fun provideFileFontStorageSource(impl: FileFontStorageSourceImpl): FileFontStorageSource = impl

    @Provides
    @Singleton
    @JvmStatic
    fun providePackageResolver(impl: PackageResolverImpl): PackageResolver = impl

    @Provides
    @Singleton
    @JvmStatic
    fun provideTemplateDataSource(impl: TemplateDataSourceImpl): TemplateDataSource = impl

    @Provides
    @Singleton
    @JvmStatic
    fun provideCoreProcess(impl: CoreProcessImpl): CoreProcess = impl
}