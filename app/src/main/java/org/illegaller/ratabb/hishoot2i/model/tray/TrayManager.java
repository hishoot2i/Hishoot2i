package org.illegaller.ratabb.hishoot2i.model.tray;

import android.graphics.Color;
import javax.inject.Inject;
import net.grandcentrix.tray.AppPreferences;
import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.utils.DeviceUtils;

public class TrayManager {
  @Inject AppPreferences mAppPreferences;

  @Inject TrayManager() {
  }

  public IntTray getBadgeColor() {
    return new IntTray(mAppPreferences, IKeyNameTray.BADGE_COLOR, AppConstants.BADGE_COLOR);
  }

  public BooleanTray getBadgeEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BADGE_ENABLE, true);
  }

  public IntTray getBadgeSize() {
    return new IntTray(mAppPreferences, IKeyNameTray.BADGE_SIZE, AppConstants.BADGE_SIZE);
  }

  public StringTray getBadgeText() {
    return new StringTray(mAppPreferences, IKeyNameTray.BADGE_TEXT, AppConstants.BADGE_TEXT);
  }

  public StringTray getBadgeTypeface() {
    return new StringTray(mAppPreferences, IKeyNameTray.BADGE_TYPEFACE,
        AppConstants.BADGE_TYPEFACE);
  }

  public BooleanTray getBackgroundColorEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_COLOR_ENABLE, true);
  }

  public IntTray getBackgroundColorInt() {
    return new IntTray(mAppPreferences, IKeyNameTray.BG_COLOR_INT, Color.CYAN);
  }

  public BooleanTray getBackgroundImageBlurEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_IMAGE_BLUR_ENABLE, false);
  }

  public IntTray getBackgroundImageBlurRadius() {
    return new IntTray(mAppPreferences, IKeyNameTray.BG_IMAGE_BLUR_RADIUS,
        AppConstants.BG_IMAGE_BLUR_RADIUS);
  }

  public IntTray getDeviceHeight() {
    return new IntTray(mAppPreferences, IKeyNameTray.DEVICE_HEIGHT, DeviceUtils.getDeviceHeight());
  }

  public IntTray getDeviceWidth() {
    return new IntTray(mAppPreferences, IKeyNameTray.DEVICE_WIDTH, DeviceUtils.getDeviceWidth());
  }

  public StringTray getDeviceName() {
    return new StringTray(mAppPreferences, IKeyNameTray.DEVICE_NAME, DeviceUtils.getDeviceName());
  }

  public StringTray getDeviceOS() {
    return new StringTray(mAppPreferences, IKeyNameTray.DEVICE_OS, DeviceUtils.getDeviceOS());
  }

  public BooleanTray getGlareEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.GLARE_ENABLE, false);
  }

  public BooleanTray getShadowEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.SHADOW_ENABLE, false);
  }

  public BooleanTray getDoubleEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.SS_DOUBLE_ENABLE, false);
  }

  public StringTray getTemplateID() {
    return new StringTray(mAppPreferences, IKeyNameTray.TEMPLATE_ID,
        AppConstants.DEFAULT_TEMPLATE_ID);
  }

  public StringTray getTemplateFav() {
    return new StringTray(mAppPreferences, IKeyNameTray.TEMPLATE_FAV,
        AppConstants.DEFAULT_TEMPLATE_ID);
  }

  public BooleanTray getBackgroundImageCrop() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.BG_IMAGE_CROP_ENABLE, false);
  }

  public IntTray getAppRunningCount() {
    return new IntTray(mAppPreferences, IKeyNameTray.APP_RUNNING_COUNT, 0);
  }

  public BooleanTray getFrameEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.FRAME_ENABLE, true);
  }

  public BooleanTray getCrashlyticEnable() {
    return new BooleanTray(mAppPreferences, IKeyNameTray.CRASHLYTIC_ENABLE, false);
  }
}
