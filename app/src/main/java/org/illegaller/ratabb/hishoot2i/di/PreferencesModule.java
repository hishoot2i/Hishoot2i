package org.illegaller.ratabb.hishoot2i.di;

import com.securepreferences.SecurePreferences;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.ir.AppRunningCount;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundColorInt;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BackgroundImageBlurRadius;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeColor;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeText;
import org.illegaller.ratabb.hishoot2i.di.ir.BadgeTypeface;
import org.illegaller.ratabb.hishoot2i.di.ir.ForApplicationContext;
import org.illegaller.ratabb.hishoot2i.di.ir.ForDefaultDisplay;
import org.illegaller.ratabb.hishoot2i.di.ir.ForWindowManager;
import org.illegaller.ratabb.hishoot2i.di.ir.GlareEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.ScreenDoubleEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.ShadowEnable;
import org.illegaller.ratabb.hishoot2i.di.ir.TemplateUsedID;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceDensity;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceName;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceOS;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenHeight;
import org.illegaller.ratabb.hishoot2i.di.ir.UserDeviceScreenWidth;
import org.illegaller.ratabb.hishoot2i.model.pref.BooleanPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.IntPreference;
import org.illegaller.ratabb.hishoot2i.model.pref.StringPreference;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.WindowManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class PreferencesModule {


    @Provides @Singleton SharedPreferences provideSharedPreferences(@ForApplicationContext Context context) {
        return new SecurePreferences(context);
    }


    @Provides @Singleton @AppRunningCount IntPreference provideAppRunningCount(SharedPreferences pref) {
        return new IntPreference(pref, "app_running_count", 0);
    }

    @Provides @Singleton @ScreenDoubleEnable BooleanPreference provideScreenShootCount(SharedPreferences pref) {
        return new BooleanPreference(pref, "screen_double", false);
    }

    /** Template */
    @Provides @Singleton @TemplateUsedID StringPreference provideTemplateUsedID(SharedPreferences pref) {
        return new StringPreference(pref, "template_used_id", AppConstants.DEFAULT_TEMPLATE_ID);
    }

    @Provides @Singleton @GlareEnable BooleanPreference provideGlareEnable(SharedPreferences pref) {
        return new BooleanPreference(pref, "glare_enable", false);
    }

    @Provides @Singleton @ShadowEnable BooleanPreference provideShadowEnable(SharedPreferences pref) {
        return new BooleanPreference(pref, "shadow_enable", false);
    }

    /** Background */
    @Provides @Singleton @BackgroundColorEnable BooleanPreference provideBackgroundColorEnable(SharedPreferences pref) {
        return new BooleanPreference(pref, "background_color_enable", true);
    }

    @Provides @Singleton @BackgroundColorInt IntPreference provideBackgroundColorInt(
            @ForApplicationContext Context context, SharedPreferences pref) {
        return new IntPreference(pref, "background_color_int", Utils.getColorInt(context, R.color.colorPrimaryDark));
    }


    @Provides @Singleton @BackgroundImageBlurEnable BooleanPreference provideBackgroundImageBlurEnable(SharedPreferences pref) {
        return new BooleanPreference(pref, "background_image_blur_enable", false);
    }

    @Provides @Singleton @BackgroundImageBlurRadius IntPreference provideBackgroundImageBlurRadius(SharedPreferences pref) {
        return new IntPreference(pref, "background_image_blur_radius",
                AppConstants.sBACKGROUND_IMAGE_BLUR_RADIUS);
    }

    /** Badge */
    @Provides @Singleton @BadgeEnable BooleanPreference provideBadgeEnable(SharedPreferences pref) {
        return new BooleanPreference(pref, "badge_enable", true);
    }

    @Provides @Singleton @BadgeColor IntPreference provideBadgeColor(SharedPreferences pref) {
        return new IntPreference(pref, "badge_color", AppConstants.sBADGE_COLOR);
    }

    @Provides @Singleton @BadgeText StringPreference provideBadgeText(SharedPreferences pref) {
        return new StringPreference(pref, "badge_text", AppConstants.sBADGE_TEXT);
    }

    @Provides @Singleton @BadgeTypeface StringPreference provideBadgeTypeface(SharedPreferences pref) {
        return new StringPreference(pref, "badge_typeface", AppConstants.sBADGE_TYPEFACE);
    }

    /* User Info */
    @Provides @Singleton @UserDeviceName StringPreference provideUserDeviceName(SharedPreferences pref) {
        return new StringPreference(pref, "user_device_name", Utils.getDeviceName());
    }

    @Provides @Singleton @UserDeviceOS StringPreference provideUserDeviceOS(SharedPreferences pref) {
        return new StringPreference(pref, "user_device_os", Utils.getDeviceOS());
    }

    @Provides @Singleton @ForDefaultDisplay Display provideDefaultDisplay(@ForWindowManager WindowManager wm) {
        return wm.getDefaultDisplay();
    }

    @Provides @Singleton @UserDeviceScreenWidth IntPreference provideUserDeviceScreenWidth(
            SharedPreferences pref, @ForDefaultDisplay Display display) {
        return new IntPreference(pref, "user_device_screen_width", Utils.getDeviceWidth(display));
    }

    @Provides @Singleton @UserDeviceScreenHeight IntPreference provideUserDeviceScreenHeight(
            SharedPreferences pref, @ForDefaultDisplay Display display) {
        return new IntPreference(pref, "user_device_screen_height", Utils.getDeviceHeight(display));
    }

    @Provides @Singleton @UserDeviceDensity IntPreference provideUserDeviceDensity(
            SharedPreferences pref, @ForDefaultDisplay Display display) {
        return new IntPreference(pref, "user_device_density", Utils.getDensity(display));
    }
}
