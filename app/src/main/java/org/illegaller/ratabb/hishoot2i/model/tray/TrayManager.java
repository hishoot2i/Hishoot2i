package org.illegaller.ratabb.hishoot2i.model.tray;

import javax.inject.Inject;
import javax.inject.Named;

public class TrayManager {

  @Inject @Named(IKeyNameTray.BADGE_COLOR) IntTray mBadgeColor;
  @Inject @Named(IKeyNameTray.BADGE_SIZE) IntTray mBadgeSize;
  @Inject @Named(IKeyNameTray.BG_COLOR_INT) IntTray mBackgroundColorInt;
  @Inject @Named(IKeyNameTray.BG_IMAGE_BLUR_RADIUS) IntTray mBackgroundImageBlurRadius;
  @Inject @Named(IKeyNameTray.DEVICE_HEIGHT) IntTray mDeviceHeight;
  @Inject @Named(IKeyNameTray.DEVICE_WIDTH) IntTray mDeviceWidth;
  @Inject @Named(IKeyNameTray.APP_RUNNING_COUNT) IntTray mAppRunningCount;
  @Inject @Named(IKeyNameTray.BADGE_ENABLE) BooleanTray mBadgeEnable;
  @Inject @Named(IKeyNameTray.BG_COLOR_ENABLE) BooleanTray mBackgroundColorEnable;
  @Inject @Named(IKeyNameTray.BG_IMAGE_BLUR_ENABLE) BooleanTray mBackgroundImageBlurEnable;
  @Inject @Named(IKeyNameTray.GLARE_ENABLE) BooleanTray mGlareEnable;
  @Inject @Named(IKeyNameTray.SHADOW_ENABLE) BooleanTray mShadowEnable;
  @Inject @Named(IKeyNameTray.FRAME_ENABLE) BooleanTray mFrameEnable;
  @Inject @Named(IKeyNameTray.SS_DOUBLE_ENABLE) BooleanTray mScreenDoubleEnable;
  @Inject @Named(IKeyNameTray.BG_IMAGE_CROP_ENABLE) BooleanTray mBackgroundImageCropEnable;
  @Inject @Named(IKeyNameTray.CRASHLYTIC_ENABLE) BooleanTray mAnalyticsEnable;
  @Inject @Named(IKeyNameTray.BADGE_TEXT) StringTray mBadgeText;
  @Inject @Named(IKeyNameTray.BADGE_TYPEFACE) StringTray mBadgeTypeface;
  @Inject @Named(IKeyNameTray.DEVICE_NAME) StringTray mDeviceName;
  @Inject @Named(IKeyNameTray.DEVICE_OS) StringTray mDeviceOS;
  @Inject @Named(IKeyNameTray.TEMPLATE_ID) StringTray mTemplateId;
  @Inject @Named(IKeyNameTray.TEMPLATE_FAV) StringTray mTemplateFav;

  @Inject public TrayManager() {
  }

  public IntTray getBadgeColor() {
    return mBadgeColor;
  }

  public BooleanTray getBadgeEnable() {
    return mBadgeEnable;
  }

  public IntTray getBadgeSize() {
    return mBadgeSize;
  }

  public StringTray getBadgeText() {
    return mBadgeText;
  }

  public StringTray getBadgeTypeface() {
    return mBadgeTypeface;
  }

  public BooleanTray getBackgroundColorEnable() {
    return mBackgroundColorEnable;
  }

  public IntTray getBackgroundColorInt() {
    return mBackgroundColorInt;
  }

  public BooleanTray getBackgroundImageBlurEnable() {
    return mBackgroundImageBlurEnable;
  }

  public IntTray getBackgroundImageBlurRadius() {
    return mBackgroundImageBlurRadius;
  }

  public IntTray getDeviceHeight() {
    return mDeviceHeight;
  }

  public IntTray getDeviceWidth() {
    return mDeviceWidth;
  }

  public StringTray getDeviceName() {
    return mDeviceName;
  }

  public StringTray getDeviceOS() {
    return mDeviceOS;
  }

  public BooleanTray getGlareEnable() {
    return mGlareEnable;
  }

  public BooleanTray getShadowEnable() {
    return mShadowEnable;
  }

  public BooleanTray getDoubleEnable() {
    return mScreenDoubleEnable;
  }

  public StringTray getTemplateID() {
    return mTemplateId;
  }

  public StringTray getTemplateFav() {
    return mTemplateFav;
  }

  public BooleanTray getBackgroundImageCrop() {
    return mBackgroundImageCropEnable;
  }

  public IntTray getAppRunningCount() {
    return mAppRunningCount;
  }

  public BooleanTray getFrameEnable() {
    return mFrameEnable;
  }

  public BooleanTray getAnalyticsEnable() {
    return mAnalyticsEnable;
  }
}
