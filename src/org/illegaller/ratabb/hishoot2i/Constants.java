package org.illegaller.ratabb.hishoot2i;

import android.support.annotation.IntDef;

public class Constants {
	public static final String ACTION_UPDATE = "org.illegaller.ratabb.hishoot2i.ACTION_UPDATE";
	public static final String ACTION_TEMPLATE_COUNT = "org.illegaller.ratabb.hishoot2i.ACTION_TEMPLATE_COUNT";

	public static final String EXTRA_TEMPLATE_COUNT = "org.illegaller.ratabb.hishoot2i.EXTRA_TEMPLATE_COUNT";
	public static final String EXTRA_FILE_SAVE = "savefile";

	public static final String DEFAULT_TEMPLATE_DESC = "Device: Default Template\nAuthor: DCSMS";

	public static final String CACHE_IMAGE_CROP = "__wallcrop.png";
	public static final String KEY_FIRSTRUN = "nofirstrun";

	public static final String KEY_PREF_REAL_DENSITY = "real_density";
	public static final String KEY_PREF_DENSITY = "density";
	public static final String KEY_PREF_DEVICE = "device";
	public static final String KEY_PREF_DEVICE_OS = "os";
	public static final String KEY_PREF_DEVICE_WIDTH = "device_width";
	public static final String KEY_PREF_DEVICE_HEIGHT = "device_height";
	public static final String KEY_PREF_SKIN_PACKAGE = "skin_package";
	public static final String KEY_PREF_IMAGE_QUALITY = "image_quality";
	public static final String KEY_PREF_SIGNIN_FB = "signfb_pref";// XXX
	public static final String KEY_PREF_SINGLE_SS = "single_ss";
	public static final String KEY_PREF_TEMPLATE_COUNT = "template_count";
	public static final String KEY_PREF_BLUR_BG = "blur_bg";
	public static final String KEY_PREF_BLUR_RADIUS = "blur_radius";
	public static final String KEY_PREF_TEMPLATE_WEB = "template_web";
	public static final String KEY_PREF_HIDE_WATTERMARK = "hide_wattermark";

	public static final String TEMPLATE_WEB = "http://goo.gl/309ctV";
	public static final String SETTING_TRIAL_COUNT = "hishoot_trial_count";

	public static final int SS1 = 0;
	public static final int SS2 = 1;
	public static final int WALL = 2;
	public static final int CROP = 3;

	@IntDef({ SS1, SS2, WALL, CROP })
	public @interface RESULT_CODE {
	}

	public static final int IQ_LOW = 0;
	public static final int IQ_MED = 1;
	public static final int IQ_HI = 2;

	@IntDef({ IQ_LOW, IQ_MED, IQ_HI })
	public @interface IMAGE_QUALITY {
	}

}
