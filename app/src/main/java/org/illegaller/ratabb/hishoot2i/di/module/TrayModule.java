package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
import android.graphics.Color;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import net.grandcentrix.tray.AppPreferences;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.utils.DeviceUtils;

import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.APP_RUNNING_COUNT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_COLOR;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_SIZE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_TEXT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_TYPEFACE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_INT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_RADIUS;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_CROP_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.CRASHLYTIC_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_HEIGHT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_NAME;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_OS;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_WIDTH;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.FRAME_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.GLARE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.SHADOW_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.SS_DOUBLE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.TEMPLATE_FAV;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.TEMPLATE_ID;

@Module public class TrayModule {
  @Provides @Singleton AppPreferences provideTray(Application app) {
    return new AppPreferences(app);
  }

  @Provides @Named(BADGE_COLOR) IntTray provideBadgeColor(AppPreferences tray) {
    return new IntTray(tray, BADGE_COLOR, AppConstants.BADGE_COLOR);
  }

  @Provides @Named(BADGE_ENABLE) BooleanTray provideBadgeEnable(AppPreferences tray) {
    return new BooleanTray(tray, BADGE_ENABLE, true);
  }

  @Provides @Named(BADGE_SIZE) IntTray provideBadgeSize(AppPreferences tray) {
    return new IntTray(tray, BADGE_SIZE, AppConstants.BADGE_SIZE);
  }

  @Provides @Named(BADGE_TEXT) StringTray provideBadgeText(AppPreferences tray) {
    return new StringTray(tray, BADGE_TEXT, AppConstants.BADGE_TEXT);
  }

  @Provides @Named(BADGE_TYPEFACE) StringTray provideBadgeTypeface(AppPreferences tray) {
    return new StringTray(tray, BADGE_TYPEFACE, AppConstants.BADGE_TYPEFACE);
  }

  @Provides @Named(BG_COLOR_ENABLE) BooleanTray provideBackgroundColorEnable(AppPreferences tray) {
    return new BooleanTray(tray, BG_COLOR_ENABLE, true);
  }

  @Provides @Named(BG_COLOR_INT) IntTray provideBackgroundColorInt(AppPreferences tray) {
    return new IntTray(tray, BG_COLOR_INT, Color.CYAN);
  }

  @Provides @Named(BG_IMAGE_BLUR_ENABLE) BooleanTray provideBackgroundImageBlurEnable(
      AppPreferences tray) {
    return new BooleanTray(tray, BG_IMAGE_BLUR_ENABLE, false);
  }

  @Provides @Named(BG_IMAGE_BLUR_RADIUS) IntTray provideBackgroundImageBlurRadius(
      AppPreferences tray) {
    return new IntTray(tray, BG_IMAGE_BLUR_RADIUS, AppConstants.BG_IMAGE_BLUR_RADIUS);
  }

  @Provides @Named(DEVICE_HEIGHT) IntTray provideDeviceHeight(AppPreferences tray) {
    return new IntTray(tray, DEVICE_HEIGHT, DeviceUtils.getDeviceHeight());
  }

  @Provides @Named(DEVICE_WIDTH) IntTray provideDeviceWidth(AppPreferences tray) {
    return new IntTray(tray, DEVICE_WIDTH, DeviceUtils.getDeviceWidth());
  }

  @Provides @Named(DEVICE_NAME) StringTray provideDeviceName(AppPreferences tray) {
    return new StringTray(tray, DEVICE_NAME, DeviceUtils.getDeviceName());
  }

  @Provides @Named(DEVICE_OS) StringTray provideDeviceOS(AppPreferences tray) {
    return new StringTray(tray, DEVICE_OS, DeviceUtils.getDeviceOS());
  }

  @Provides @Named(GLARE_ENABLE) BooleanTray provideGlareEnable(AppPreferences tray) {
    return new BooleanTray(tray, GLARE_ENABLE, false);
  }

  @Provides @Named(SHADOW_ENABLE) BooleanTray provideShadowEnable(AppPreferences tray) {
    return new BooleanTray(tray, SHADOW_ENABLE, false);
  }

  @Provides @Named(SS_DOUBLE_ENABLE) BooleanTray provideDoubleEnable(AppPreferences tray) {
    return new BooleanTray(tray, SS_DOUBLE_ENABLE, false);
  }

  @Provides @Named(TEMPLATE_ID) StringTray provideTemplateID(AppPreferences tray) {
    return new StringTray(tray, TEMPLATE_ID, AppConstants.DEFAULT_TEMPLATE_ID);
  }

  @Provides @Named(TEMPLATE_FAV) StringTray provideTemplateFav(AppPreferences tray) {
    return new StringTray(tray, TEMPLATE_FAV, AppConstants.DEFAULT_TEMPLATE_ID);
  }

  @Provides @Named(BG_IMAGE_CROP_ENABLE) BooleanTray provideBackgroundImageCrop(
      AppPreferences tray) {
    return new BooleanTray(tray, BG_IMAGE_CROP_ENABLE, false);
  }

  @Provides @Named(APP_RUNNING_COUNT) IntTray provideAppRunningCount(AppPreferences tray) {
    return new IntTray(tray, APP_RUNNING_COUNT, 0);
  }

  @Provides @Named(FRAME_ENABLE) BooleanTray provideFrameEnable(AppPreferences tray) {
    return new BooleanTray(tray, FRAME_ENABLE, true);
  }

  @Provides @Named(CRASHLYTIC_ENABLE) BooleanTray provideCrashlyticEnable(AppPreferences tray) {
    return new BooleanTray(tray, CRASHLYTIC_ENABLE, false);
  }
}
