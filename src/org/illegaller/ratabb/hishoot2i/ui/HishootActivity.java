package org.illegaller.ratabb.hishoot2i.ui;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.util.DeviceUtil;
import org.illegaller.ratabb.hishoot2i.util.DrawView;

//import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

@SuppressWarnings("rawtypes")
public class HishootActivity extends MaterialNavigationDrawer {
	private SharedPreferences mSharedPreferences;

	public SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}

	static {
		System.loadLibrary("photoprocessing");
	}

	public native String wat1();

	public native String wat2();

	public static enum watBitEnum {
		WAT1, WAT2
	};

	private Bitmap watBitmap, wat2Bitmap;

	public Bitmap getWatBitmap(watBitEnum wat) {
		if (wat.equals(watBitEnum.WAT1)) {
			return watBitmap;
		} else if (wat.equals(watBitEnum.WAT2)) {
			return wat2Bitmap;
		} else {
			return null;
		}
	}

	private void setWatBitmap(int density) {
		watBitmap = DrawView.getMark(wat1(), density);
		wat2Bitmap = DrawView.getMark(wat2(), density);
	}

	@SuppressWarnings("unchecked")
	public void selectItem(CharSequence menu, String object) {

		if (menu.equals(getString(R.string.menu_main))) {
			setFragment(MainFragment.newInstance(),
					getString(R.string.menu_main));
			setSection(sectionMain);
		} else if (menu.equals(getString(R.string.share))) {
			setFragment(ShareFragment.newInstance(object),
					getString(R.string.share));

		}
	}

	private MaterialSection<Fragment> sectionMain, sectionTemplate,
			sectionAbout, sectionSetting;

	@SuppressWarnings("unchecked")
	@Override
	public void init(Bundle savedInstanceState) {
		Resources res = getResources();
		setDrawerHeaderImage(R.drawable.banner);
		setUsername(getString(R.string.app_name));
		setUserEmail(String.format(getString(R.string.version),
				getString(R.string.app_ver)));

		setUsernameTextColor(res.getColor(android.R.color.black));
		setUserEmailTextColor(res.getColor(android.R.color.black));
		// section (menu)

		sectionMain = newSection(getString(R.string.menu_main),
				R.drawable.ic_section_main, MainFragment.newInstance());
		sectionTemplate = newSection(getString(R.string.menu_template),
				R.drawable.ic_section_template, ThemplateFragment.newInstance());
		sectionAbout = newSection(getString(R.string.menu_about),
				R.drawable.ic_section_about, AboutFragment.newInstance());

		sectionSetting = newSection(getString(R.string.menu_setting),
				R.drawable.ic_section_setting,
				new Intent(this, DeviceUtil.isICS() ?

				SettingActivity.class : OlderSettingActivity.class));

		this.addSection(sectionMain);
		this.addSection(sectionTemplate);
		this.addSection(sectionAbout);

		this.addBottomSection(sectionSetting);

		oldInit();

	}

	private void oldInit() {
		mSharedPreferences = getSharedPreferences(getPackageName()
				+ "_preferences", Context.MODE_PRIVATE);
		boolean firstrun = mSharedPreferences.getBoolean(
				Constants.KEY_FIRSTRUN, false);

		DeviceUtil.setColorStatusbar(this.getWindow(),
				getResources().getColor(R.color.latar_gelap));
		if (!firstrun) {
			DeviceUtil.setDeviceInfo(this.getWindowManager()
					.getDefaultDisplay(), mSharedPreferences);
		}

		int density = mSharedPreferences.getInt(
				Constants.KEY_PREF_REAL_DENSITY, DisplayMetrics.DENSITY_MEDIUM);
		setWatBitmap(density);
		setReceiver(this);

		int count = mSharedPreferences.getInt(
				Constants.KEY_PREF_TEMPLATE_COUNT, 0);
		setCountTemplate(count);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}

	private void setReceiver(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_UPDATE);
		filter.addAction(Constants.ACTION_TEMPLATE_COUNT);
		context.registerReceiver(receiver, filter);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.contains(Constants.ACTION_UPDATE)) {
				if (getCurrentSection() == sectionMain)
					selectItem(getString(R.string.menu_main), null);
			} else if (action.contains(Constants.ACTION_TEMPLATE_COUNT)) {
				int count = intent.getIntExtra(Constants.EXTRA_TEMPLATE_COUNT,
						0);
				setCountTemplate(count);
			}

		}
	};

	private void setCountTemplate(int count) {
		sectionTemplate.setNotifications(count);
		mSharedPreferences.edit()
				.putInt(Constants.KEY_PREF_TEMPLATE_COUNT, count).commit();
	}
}
