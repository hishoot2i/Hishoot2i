package org.illegaller.ratabb.hishoot2i.model.tray;

import android.content.Context;
import android.graphics.Color;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import net.grandcentrix.tray.AppPreferences;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.utils.DeviceUtils;

@Module public class TrayModule {
  private final AppPreferences mAppPreferences;

  public TrayModule(Context context) {
    this.mAppPreferences = new AppPreferences(context);
  }

  @Provides @Named(IKeyNameTray.BADGE_COLOR) IntTray provideBadgeColor() {
    return new IntTray(mAppPreferences, IKeyNameTray.BADGE_COLOR, AppConstants.BADGE_COLOR);
  }

  @Provides @Named(IKeyNameTray.BADGE_SIZE) IntTray provideBadgeSize() {
    return new IntTray(mAppPreferences, IKeyNameTray.BADGE_SIZE, AppConstants.BADGE_SIZE);
  }

  @Provides @Named(IKeyNameTray.BG_COLOR_INT) IntTray provideBackgroundColorInt() {
    return new IntTray(mAppPreferences, IKeyNameTray.BG_COLOR_INT, Color.CYAN);
  }

  @Provides @Named(IKeyNameTray.BG_IMAGE_BLUR_RADIUS) IntTray provideBackgroundImageBlurRadius() {
    return new IntTray(mAppPreferences, IKeyNameTray.BG_IMAGE_BLUR_RADIUS,
        AppConstants.BG_IMAGE_BLUR_RADIUS);
  }

  @Provides @Named(IKeyNameTray.DEVICE_HEIGHT) IntTray provideDeviceHeight() {
    return new IntTray(mAppPreferences, IKeyNameTray.DEVICE_HEIGHT, DeviceUtils.getDeviceHeight());
  }

  @Provides @Named(IKeyNameTray.DEVICE_WIDTH) IntTray provideDeviceWidth() {
    return new IntTray(mAppPreferences, IKeyNameTray.DEVICE_WIDTH, DeviceUtils.getDeviceWidth());
  }

  @Provides @Named(IKeyNameTray.APP_RUNNING_COUNT) IntTray provideAppRunningCount() {
    return new IntTray(mAppPreferences, IKeyNameTray.APP_RUNNING_COUNT, 0);
  }

  @Provides @Named(IKeyNameTray.BADGE_ENABLE) BooleanTray provideBadgeEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BADGE_ENABLE, true);
  }

  @Provides @Named(IKeyNameTray.BG_COLOR_ENABLE) BooleanTray provideBackgroundColorEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_COLOR_ENABLE, true);
  }

  @Provides @Named(IKeyNameTray.BG_IMAGE_BLUR_ENABLE)
  BooleanTray provideBackgroundImageBlurEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_IMAGE_BLUR_ENABLE, false);
  }

  @Provides @Named(IKeyNameTray.GLARE_ENABLE) BooleanTray provideGlareEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.GLARE_ENABLE, true);
  }

  @Provides @Named(IKeyNameTray.SHADOW_ENABLE) BooleanTray provideShadowEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.SHADOW_ENABLE, true);
  }

  @Provides @Named(IKeyNameTray.FRAME_ENABLE) BooleanTray provideFrameEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.FRAME_ENABLE, true);
  }

  @Provides @Named(IKeyNameTray.SS_DOUBLE_ENABLE) BooleanTray provideScreenDoubleEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.SS_DOUBLE_ENABLE, false);
  }

  @Provides @Named(IKeyNameTray.BG_IMAGE_CROP_ENABLE)
  BooleanTray provideBackgroundImageCropEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_IMAGE_CROP_ENABLE, false);
  }

  @Provides @Named(IKeyNameTray.CRASHLYTIC_ENABLE) BooleanTray provideAnalyticsEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.CRASHLYTIC_ENABLE, false);
  }

  @Provides @Named(IKeyNameTray.BADGE_TEXT) StringTray provideBadgeText() {
    return new StringTray(mAppPreferences, IKeyNameTray.BADGE_TEXT, AppConstants.BADGE_TEXT);
  }

  @Provides @Named(IKeyNameTray.BADGE_TYPEFACE) StringTray provideBadgeTypeface() {
    return new StringTray(mAppPreferences, IKeyNameTray.BADGE_TYPEFACE,
        AppConstants.BADGE_TYPEFACE);
  }

  @Provides @Named(IKeyNameTray.DEVICE_NAME) StringTray provideDeviceName() {
    return new StringTray(mAppPreferences, IKeyNameTray.DEVICE_NAME, DeviceUtils.getDeviceName());
  }

  @Provides @Named(IKeyNameTray.DEVICE_OS) StringTray provideDeviceOS() {
    return new StringTray(mAppPreferences, IKeyNameTray.DEVICE_OS, DeviceUtils.getDeviceOS());
  }

  @Provides @Named(IKeyNameTray.TEMPLATE_ID) StringTray provideTemplateId() {
    return new StringTray(mAppPreferences, IKeyNameTray.TEMPLATE_ID,
        AppConstants.DEFAULT_TEMPLATE_ID);
  }

  @Provides @Named(IKeyNameTray.TEMPLATE_FAV) StringTray provideTemplateFav() {
    return new StringTray(mAppPreferences, IKeyNameTray.TEMPLATE_FAV,
        AppConstants.DEFAULT_TEMPLATE_ID);
  }
}
