package org.illegaller.ratabb.hishoot2i.di

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import common.FileConstants
import common.egl.MaxTexture
import common.egl.MaxTextureCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.modules.ApplicationContextModule
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import imageloader.ImageLoader
import imageloader.coil.CoilImageLoaderImpl
import org.illegaller.ratabb.hishoot2i.provider.FileConstantsImpl
import template.TemplateFactoryManager
import template.TemplateFactoryManagerImpl
import javax.inject.Singleton

@Module(includes = [ApplicationContextModule::class])
@InstallIn(SingletonComponent::class)
object ProvideAppModule {

    @Provides
    @Singleton
    fun provideMaxTexture(): MaxTexture = MaxTextureCompat

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader = CoilImageLoaderImpl(context, isDebugLog = false)

    @Provides
    @Singleton
    fun bindFileConstants(
        @ApplicationContext context: Context,
    ): FileConstants = FileConstantsImpl(context)

    @Provides
    @Singleton
    fun provideFactoryManager(
        @ApplicationContext context: Context,
        fileConstants: FileConstants
    ): TemplateFactoryManager = TemplateFactoryManagerImpl(context, fileConstants)

    @Provides
    @Singleton
    fun provideNotificationManagerCompat(
        @ApplicationContext context: Context
    ): NotificationManagerCompat = NotificationManagerCompat.from(context)

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager = context.packageManager

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver = context.contentResolver
}
