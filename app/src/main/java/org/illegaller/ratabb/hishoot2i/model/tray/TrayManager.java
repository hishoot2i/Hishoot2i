package org.illegaller.ratabb.hishoot2i.model.tray;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Named;

public class TrayManager {
    @Inject @Named(IKeyNameTray.BADGE_COLOR) IntTray badgeColorTray;
    @Inject @Named(IKeyNameTray.BADGE_ENABLE) BooleanTray badgeEnableTray;
    @Inject @Named(IKeyNameTray.BADGE_SIZE) IntTray badgeSizeTray;
    @Inject @Named(IKeyNameTray.BADGE_TEXT) StringTray badgeTextTray;
    @Inject @Named(IKeyNameTray.BADGE_TYPEFACE) StringTray badgeTypefaceTray;
    @Inject @Named(IKeyNameTray.BG_COLOR_ENABLE) BooleanTray bgColorEnableTray;
    @Inject @Named(IKeyNameTray.BG_COLOR_INT) IntTray bgColorIntTray;
    @Inject @Named(IKeyNameTray.BG_IMAGE_BLUR_ENABLE) BooleanTray bgImageBlurEnableTray;
    @Inject @Named(IKeyNameTray.BG_IMAGE_BLUR_RADIUS) IntTray bgImageBlurRadiusTray;
    @Inject @Named(IKeyNameTray.SS_DOUBLE_ENABLE) BooleanTray ssDoubleEnableTray;
    @Inject @Named(IKeyNameTray.TEMPLATE_ID) StringTray templateIdTray;
    @Inject @Named(IKeyNameTray.GLARE_ENABLE) BooleanTray glareEnableTray;
    @Inject @Named(IKeyNameTray.SHADOW_ENABLE) BooleanTray shadowEnableTray;
    @Inject @Named(IKeyNameTray.DEVICE_HEIGHT) IntTray deviceHeightTray;
    @Inject @Named(IKeyNameTray.DEVICE_WIDTH) IntTray deviceWidthTray;
    @Inject @Named(IKeyNameTray.APP_RUNNING_COUNT) IntTray appRunningCountTray;
    @Inject @Named(IKeyNameTray.DEVICE_NAME) StringTray deviceNameTray;
    @Inject @Named(IKeyNameTray.DEVICE_OS) StringTray deviceOSTray;
    @Inject @Named(IKeyNameTray.TEMPLATE_FAV) StringTray templateFavTray;
    @Inject @Named(IKeyNameTray.BG_IMAGE_CROP_ENABLE) BooleanTray bgImageCropEnableTray;

    public TrayManager(Context context) {
        AppComponent component = HishootApplication.get(context).getComponent();
        DaggerTrayComponent.builder()
                .trayModule(new TrayModule(component.application(), component.windowManager()))
                .build().inject(this);
    }

    public StringTray getTemplateFavTray() {
        return templateFavTray;
    }

    public BooleanTray getBgImageCropEnableTray() {
        return bgImageCropEnableTray;
    }

    public StringTray getBadgeTypefaceTray() {
        return badgeTypefaceTray;
    }

    public IntTray getDeviceHeightTray() {
        return deviceHeightTray;
    }

    public IntTray getDeviceWidthTray() {
        return deviceWidthTray;
    }

    public IntTray getAppRunningCountTray() {
        return appRunningCountTray;
    }

    public StringTray getDeviceNameTray() {
        return deviceNameTray;
    }

    public StringTray getDeviceOSTray() {
        return deviceOSTray;
    }

    public IntTray getBadgeColorTray() {
        return badgeColorTray;
    }

    public BooleanTray getBadgeEnableTray() {
        return badgeEnableTray;
    }

    public IntTray getBadgeSizeTray() {
        return badgeSizeTray;
    }

    public StringTray getBadgeTextTray() {
        return badgeTextTray;
    }

    public BooleanTray getBgColorEnableTray() {
        return bgColorEnableTray;
    }

    public IntTray getBgColorIntTray() {
        return bgColorIntTray;
    }

    public BooleanTray getBgImageBlurEnableTray() {
        return bgImageBlurEnableTray;
    }

    public IntTray getBgImageBlurRadiusTray() {
        return bgImageBlurRadiusTray;
    }

    public BooleanTray getSsDoubleEnableTray() {
        return ssDoubleEnableTray;
    }

    public StringTray getTemplateIdTray() {
        return templateIdTray;
    }

    public BooleanTray getGlareEnableTray() {
        return glareEnableTray;
    }

    public BooleanTray getShadowEnableTray() {
        return shadowEnableTray;
    }
}
