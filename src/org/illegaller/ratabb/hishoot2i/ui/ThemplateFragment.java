package org.illegaller.ratabb.hishoot2i.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.illegaller.ratabb.hishoot2i.Constants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.skin.GetResources;
import org.illegaller.ratabb.hishoot2i.skin.SkinUtil;
import org.illegaller.ratabb.hishoot2i.util.DrawView;

import com.afollestad.materialdialogs.MaterialDialog;

import static org.illegaller.ratabb.hishoot2i.Constants.*;
//import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ThemplateFragment extends ListFragment {
	private Context mContext;
	private SharedPreferences mSharedPreferences;

	private SkinUtil mSkinUtil;
	private ArrayList<Pair<String, Drawable>> paket;
	private int density, width, height;

	public ThemplateFragment() {
	}

	public static ThemplateFragment newInstance() {
		return new ThemplateFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.template, container, false);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = getActivity();
		mSharedPreferences = ((HishootActivity) mContext)
				.getSharedPreferences();

		density = mSharedPreferences.getInt(KEY_PREF_DENSITY, 0);
		width = mSharedPreferences.getInt(KEY_PREF_DEVICE_WIDTH, 240);
		height = mSharedPreferences.getInt(KEY_PREF_DEVICE_HEIGHT, 320);

		mSkinUtil = new SkinUtil(mContext);

		List<SkinItem> items = new ArrayList<SkinItem>();

		// add default skin
		items.add(new SkinItem(mSkinUtil.DEFAULT, DEFAULT_TEMPLATE_DESC,
				mContext.getResources().getDrawable(R.drawable.ic_launcher)));

		paket = loadSkinPackage();
		if (paket != null) {
			for (int i = 0; i < paket.size(); i++) {
				Pair<String, Drawable> pair = paket.get(i);
				items.add(new SkinItem(pair.first, stringSkinInfo(pair.first),
						pair.second));
			}
		}

		setListAdapter(new SkinAdapter(mContext, items));

		// XXX
		Intent intent = new Intent();

		intent.setAction(Constants.ACTION_TEMPLATE_COUNT);
		intent.putExtra(Constants.EXTRA_TEMPLATE_COUNT, paket.size());
		mContext.sendBroadcast(intent);
	}

	private String skinDescription() {
		String formatString = "Device: %s  (%s)\nAuthor: %s";
		return String.format(formatString, mSkinUtil.getSkinName(),
				mSkinUtil.typeSkin(mSkinUtil.getType()),
				mSkinUtil.getAuthorName());
	}

	private String skinMessageNotCompatible() {
		String formatString = "Not compatible\nTemplate: %s\nYour device: %s";
		return String.format(formatString,
				mSkinUtil.typeSkin(mSkinUtil.getType()),
				mSkinUtil.typeSkin(density));
	}

	private String stringSkinInfo(String pkg) {
		mSkinUtil.getSkinInfo(pkg);
		return skinDescription();
	}

	private ArrayList<Pair<String, Drawable>> loadSkinPackage() {
		ArrayList<Pair<String, Drawable>> mPaket = new ArrayList<Pair<String, Drawable>>();

		PackageManager pm = mContext.getPackageManager();
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		SkinItem item = (SkinItem) l.getAdapter().getItem(position);
		String mSkinPackageName = item.pkgName, mSkinMessage = null;

		String description = DEFAULT_TEMPLATE_DESC;
		Bitmap bitmap = DrawView.getNine(0, 0, R.drawable.frame1, width,
				height, mContext);

		boolean isCompatible = true, isDefault = (mSkinPackageName
				.equalsIgnoreCase(mSkinUtil.DEFAULT));

		if (!isDefault) {
			mSkinUtil.getSkinInfo(mSkinPackageName);
			isCompatible = (mSkinUtil.getType() == density);
			mSkinMessage = (isCompatible) ? null : skinMessageNotCompatible();
			description = skinDescription();
			GetResources getResources = new GetResources(mContext);
			bitmap = getResources.getImage(mSkinPackageName, "skin");
		}

		SkinPreviewListener spListener = new SkinPreviewListener(
				mSkinPackageName, mSkinMessage, isCompatible, isDefault);

		showPreview(spListener, bitmap, description);
	}

	private void showPreview(final SkinPreviewListener listener, Bitmap bitmap,
			String desc) {
		MaterialDialog preview = new MaterialDialog.Builder(mContext)
				.customView(R.layout.preview, false).autoDismiss(true)
				.positiveText(R.string.btn_apply)
				.negativeText(R.string.btn_cancel)
				.callback(new MaterialDialog.ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						super.onPositive(dialog);
						listener.onApply();
					}
				})

				.build();

		((ImageView) preview.getCustomView().findViewById(R.id.preview_image))
				.setImageBitmap(bitmap);
		((TextView) preview.getCustomView().findViewById(R.id.description))
				.setText(desc);

		preview.show();
	}

	private class SkinPreviewListener implements IPreviewListener {
		String sPkgName, sMessage;
		boolean isCompatible, isDefault;

		public SkinPreviewListener(String pkgname, String message,
				boolean compatible, boolean def) {
			sPkgName = pkgname;
			sMessage = message;
			isCompatible = compatible;
			isDefault = def;
		}

		@Override
		public void onApply() {
			if (isCompatible) {
				setCurrentSkin(sPkgName, isDefault);
			} else {
				Toast.makeText(mContext, sMessage, Toast.LENGTH_SHORT).show();
			}
		}

	};

	public interface IPreviewListener {
		void onApply();
	}

	private void setCurrentSkin(String pkg, boolean isDefault) {
		Editor editor = mSharedPreferences.edit();

		if (isDefault) {
			editor.remove(KEY_PREF_SKIN_PACKAGE);
		} else {
			editor.putString(KEY_PREF_SKIN_PACKAGE, pkg);
		}
		editor.commit();

		// XXX
		((HishootActivity) getActivity()).selectItem(
				getString(R.string.menu_main), null);

	}

	private class SkinItem {
		String pkgName, des;
		Drawable icon;

		private SkinItem(String _pkg, String _des, Drawable _icon) {
			pkgName = _pkg;
			des = _des;
			icon = _icon;
		}
	}

	private class SkinAdapter extends ArrayAdapter<SkinItem> {

		public SkinAdapter(Context context, List<SkinItem> items) {
			super(context, R.layout.template_item, R.id.description, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			TextView tvDes = (TextView) view.findViewById(R.id.description);
			tvDes.setText(getItem(position).des);
			ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
			ivIcon.setImageDrawable(getItem(position).icon);
			return view;
		}
	}
}
