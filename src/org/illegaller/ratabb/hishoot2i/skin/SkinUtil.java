package org.illegaller.ratabb.hishoot2i.skin;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

public class SkinUtil {
	public final String DEFAULT = "default";
	private Context mContext;
	private String mSkinName, mAuthorName;
	private int mType;

	public SkinUtil(Context context) {
		mContext = context;
	}

	public String getSkinName() {
		return mSkinName;
	}

	public String getAuthorName() {
		return mAuthorName;
	}

	public int getType() {
		return mType;
	}

	public void getSkinInfo(String pkg) {
		if (pkg.equalsIgnoreCase(DEFAULT))
			return;

		try {
			Context c = mContext.createPackageContext(pkg, 0);
			AssetManager am = c.getAssets();
			InputStream is = am.open("keterangan.xml");

			SkinDescription k = new SkinDescription(is);
			this.mSkinName = k.getDevice();
			this.mAuthorName = k.getAuthor();
			this.mType = k.getDensType();

			is.close();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String typeSkin(int d) {
		String type = "";
		switch (d) {
		default:
			type = "unknown";
			break;
		case 0:
			type = "LDPI";
			break;
		case 1:
			type = "MDPI";
			break;
		case 2:
			type = "HDPI";
			break;
		case 3:
			type = "XHDPI";
			break;
		case 4:
			type = "XXHDPI";
			break;
		case 5:
			type = "XXXHDPI";
			break;
		}
		return type;
	}
}
