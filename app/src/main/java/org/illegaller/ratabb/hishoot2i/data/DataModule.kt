package org.illegaller.ratabb.hishoot2i.data

import android.content.Context
import com.chibatching.kotpref.Kotpref
import dagger.Module
import dagger.Provides
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcess
import org.illegaller.ratabb.hishoot2i.data.core.CoreProcessImpl
import org.illegaller.ratabb.hishoot2i.data.pref.AppPref
import org.illegaller.ratabb.hishoot2i.data.rx.AppScheduler
import org.illegaller.ratabb.hishoot2i.data.rx.SchedulerProvider
import rbb.hishoot2i.common.FileConstants
import rbb.hishoot2i.common.imageloader.ImageLoader
import rbb.hishoot2i.template.TemplateFactoryManager
import rbb.hishoot2i.template.TemplateFactoryManagerImpl
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
    fun provideFileConstants(wrapper: FileConstantsWrapper): FileConstants = wrapper

    @Provides
    @Singleton
    @JvmStatic
    fun provideImageLoader(wrapper: ImageLoaderWrapper): ImageLoader = wrapper

    @Provides
    @Singleton
    @JvmStatic
    fun provideFactoryManager(impl: TemplateFactoryManagerImpl): TemplateFactoryManager = impl

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