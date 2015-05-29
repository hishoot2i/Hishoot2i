package org.illegaller.ratabb.hishoot2i.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Pair;

public class TemplateUtil {

	public static ArrayList<Pair<String, Drawable>> loadSkinPackage(
			Context context) {
		ArrayList<Pair<String, Drawable>> mPaket = new ArrayList<Pair<String, Drawable>>();

		PackageManager pm = context.getPackageManager();
		Intent i = new Intent(Intent.ACTION_MAIN, null);
		i.addCategory("dcsms.hishoot.SKINTEMPLATE");
		List<ResolveInfo> apps = pm.queryIntentActivities(i, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo skin : apps) {
			ActivityInfo ai = skin.activityInfo;
			// XXX:
			mPaket.add(Pair.create(ai.packageName, ai.loadIcon(pm)));
		}
		return mPaket;
	}
}
