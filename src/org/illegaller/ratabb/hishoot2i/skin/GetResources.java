package org.illegaller.ratabb.hishoot2i.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

public class GetResources {
	private Context mContext;
	private Bitmap mBitmap;
	private NinePatchDrawable mNinePatchDrawable;
	private PackageManager mPackageManager;

	public GetResources(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
	}

	public NinePatchDrawable getNinePatchDrawable(String pkgName, String namaPng) {
		String themePack = pkgName;
		Resources themeResources = null;
		try {
			themeResources = mPackageManager
					.getResourcesForApplication(themePack);
		} catch (PackageManager.NameNotFoundException e) {
		} catch (NullPointerException e) {
		}
		mNinePatchDrawable = loadNineDraw(themeResources, themePack, namaPng);

		return mNinePatchDrawable;
	}

	public Bitmap getImage(String pkgName, String namaPng) {
		String themePack = pkgName;
		Resources themeResources = null;
		try {
			themeResources = mPackageManager
					.getResourcesForApplication(themePack);
		} catch (PackageManager.NameNotFoundException e) {
		} catch (NullPointerException e) {
		}
		Drawable d = loadThemeResource(themeResources, themePack, namaPng);
		mBitmap = ((BitmapDrawable) d).getBitmap();

		return mBitmap;
	}

	@SuppressWarnings("deprecation")
	public static Drawable loadThemeResource(Resources res, String pkg,
			String item_name) {
		Drawable d = null;
		if (res != null) {
			int resource_id = res.getIdentifier(item_name, "drawable", pkg);
			if (resource_id != 0) {
				try {
					d = res.getDrawable(resource_id);
				} catch (Resources.NotFoundException e) {
				}
			}
		}
		return d;
	}

	@SuppressWarnings("deprecation")
	public static NinePatchDrawable loadNineDraw(Resources res, String pkg,
			String item_name) {
		NinePatchDrawable d = null;
		if (res != null) {
			int resource_id = res.getIdentifier(item_name, "drawable", pkg);
			if (resource_id != 0) {
				try {
					d = (NinePatchDrawable) res.getDrawable(resource_id);
				} catch (Resources.NotFoundException e) {
				}
			}
		}
		return d;
	}
}
