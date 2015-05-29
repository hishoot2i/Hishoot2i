package org.illegaller.ratabb.hishoot2i.fragment;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import com.android.crop.CropImageIntentBuilder;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.ui.HishootActivity;
import org.illegaller.ratabb.hishoot2i.ui.HishootActivity.watBitEnum;
import org.illegaller.ratabb.hishoot2i.util.ImageTask;
import org.illegaller.ratabb.hishoot2i.util.Pref;
import org.illegaller.ratabb.hishoot2i.util.SaveTask;
import org.illegaller.ratabb.hishoot2i.util.SystemProp;

import static org.illegaller.ratabb.hishoot2i.Constants.*;

public class MainFragment extends Fragment implements View.OnClickListener,
		ImageTask.OnImageTaskListener, SaveTask.OnSaveTaskListener {

	public static final String TAG = "Hishoot2i:MainFragment";
	private Pref mPref;
	private Context mContext;

	private FloatingActionsMenu fabMenu;
	private FloatingActionButton fabss1, fabss2, fabwalp, fabsave;

	private ImageView ivPreview;

	private MaterialDialog progDialog;
	private Uri mUriCrop;

	private int wall_lebar, wall_tinggi, tinggi, lebar, imageQuality;

	private String IMG_SS1, IMG_SS2, IMG_WALL;
	private Bitmap mBitmapSave;
	private ImageTask mImageTask;

	private boolean single;

	public MainFragment() {
	}

	public static MainFragment newInstance() {
		return new MainFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main, container, false);
		mContext = getActivity();
		mPref = new Pref(mContext);

		tinggi = mPref.getSPref().getInt(KEY_PREF_DEVICE_HEIGHT, 320);
		lebar = mPref.getSPref().getInt(KEY_PREF_DEVICE_WIDTH, 240);
		mUriCrop = Uri.fromFile(new File(mContext.getExternalCacheDir(),
				CACHE_IMAGE_CROP));
		imageQuality = getIntImageQuality();

		single = mPref.getSPref().getBoolean(KEY_PREF_SINGLE_SS, false);

		ivPreview = (ImageView) view.findViewById(R.id.iv_preview);

		fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
		fabss1 = (FloatingActionButton) view.findViewById(R.id.fab_ss1);
		fabss2 = (FloatingActionButton) view.findViewById(R.id.fab_ss2);
		fabwalp = (FloatingActionButton) view.findViewById(R.id.fab_wallp);
		fabsave = (FloatingActionButton) view.findViewById(R.id.fab_save);

		fabss1.setOnClickListener(this);
		fabss2.setOnClickListener(this);
		fabwalp.setOnClickListener(this);
		fabsave.setOnClickListener(this);

		runningTask(false);
		return view;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.fab_ss1:
			getImageChooser(
					(String.format("%s 1", getString(R.string.screenshoot))),
					SS1);
			break;
		case R.id.fab_ss2:
			getImageChooser(
					(String.format("%s 2", getString(R.string.screenshoot))),
					SS2);
			break;
		case R.id.fab_wallp:
			getImageChooser(getString(R.string.wallpaper), WALL);
			break;
		case R.id.fab_save:
			runningTask(true);
			break;
		default:
			break;
		}
		fabMenu.collapse();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((resultCode != Activity.RESULT_OK) || (data == null))
			return;

		String sdata = data.getDataString();
		switch (requestCode) {
		case SS1:
			IMG_SS1 = sdata;
			break;
		case SS2:
			IMG_SS2 = sdata;
			break;
		case WALL:
			CropImageIntentBuilder cropImage = new CropImageIntentBuilder(
					wall_lebar, wall_tinggi, wall_lebar, wall_tinggi, mUriCrop)
					.setScale(true).setScaleUpIfNeeded(true)
					.setOutputFormat("PNG").setDoFaceDetection(false)
					.setSourceImage(data.getData());

			startActivityForResult(cropImage.getIntent(mContext), CROP);
			break;
		case CROP:
			IMG_WALL = mUriCrop.toString();
			break;
		default:
			break;
		}

		runningTask(false);
	}

	@Override
	public void onPause() {
		dismisDialog();
		System.gc();
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onLowMemory() {
		dismisDialog();
		super.onLowMemory();

	}

	@Override
	public void onDestroy() {
		dismisDialog();
		System.gc();
		super.onDestroy();
	}

	private void dismisDialog() {
		if (progDialog != null && progDialog.isShowing())
			progDialog.dismiss();
	}

	private void runningTask(boolean save) {
		System.gc();
		String ss = getString(R.string.screenshoot);
		if (single) {
			fabss1.setTitle(ss);
			fabss2.setVisibility(View.GONE);
		} else {
			fabss1.setTitle(String.format("%s 1", ss));
			fabss2.setTitle(String.format("%s 2", ss));
		}

		if (save) {
			new SaveTask(this).execute(mBitmapSave);
		} else {
			try {
				mImageTask = new ImageTask(mContext, this);
				mImageTask.execute();
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage());
				mImageTask.cancel(false);
			}

		}
	}

	private MaterialDialog getProgressDialog(int titleId, int msgId) {
		return new MaterialDialog.Builder(mContext).cancelable(false)
				.progress(true, 0).title(titleId).content(msgId).show();
	}

	private void onErrImageTask() {
		MaterialDialog.Builder builder =

		new MaterialDialog.Builder(mContext)

		.title(R.string.failed)

		.content("Soory, ....")

		.autoDismiss(true)

		.positiveText(android.R.string.yes)

		.callback(new MaterialDialog.ButtonCallback() {
			@Override
			public void onPositive(MaterialDialog dialog) {
				super.onPositive(dialog);
				mPref.putAndApply(KEY_PREF_SINGLE_SS, true);
				mPref.putAndApply(KEY_FIRSTRUN, false);
				runningTask(false);
			}

		});
		progDialog = builder.show();

	}

	private void getImageChooser(String string, @RESULT_CODE int id) {
		String chooser = getString(R.string.chooser);
		String title = String.format(chooser, string);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, title), id);
	}

	private int getIntImageQuality() {
		// low=0, medium=1, high=2
		String[] values = getResources().getStringArray(
				R.array.imagequality_values);
		String ret = mPref.getSPref().getString(KEY_PREF_IMAGE_QUALITY,
				values[1]);
		int result = IQ_MED;

		if (ret.equals(values[0])) {
			result = IQ_LOW;
		} else if (ret.equals(values[1])) {
			result = IQ_MED;
		} else if (ret.equals(values[2])) {
			result = IQ_HI;
		}
		return result;
	}

	/* ImageTask.IImageTaskListener */
	@Override
	public void onPostResult(Bitmap result) {
		wall_lebar = result.getWidth();
		wall_tinggi = result.getHeight();
		ivPreview.setImageBitmap(result);
		mBitmapSave = result;
		progDialog.dismiss();

	}

	@Override
	public void onPre() {
		progDialog = getProgressDialog(R.string.processing,
				R.string.please_wait);

	}

	@Override
	public void onThrow(boolean flag) {
		if (flag)
			onErrImageTask();
	}

	@Override
	public String packageTemplate() {
		if (mPref.getSPref().contains(KEY_PREF_SKIN_PACKAGE)) {
			return mPref.getSPref().getString(KEY_PREF_SKIN_PACKAGE, "");
		} else {
			return null;
		}
	}

	@Override
	public String[] bitmapAll() {
		return new String[] { IMG_SS1, IMG_SS2, IMG_WALL };
	}

	@Override
	public Point WH() {
		return new Point(lebar, tinggi);
	}

	@Override
	public Bitmap[] wat() {
		Bitmap wm = ((HishootActivity) getActivity())
				.getWatBitmap(watBitEnum.WAT1);
		Bitmap wmi = ((HishootActivity) getActivity())
				.getWatBitmap(watBitEnum.WAT2);
		return new Bitmap[] { wm, wmi };
	}

	@Override
	public Boolean onBlur() {
		return mPref.getSPref().getBoolean(KEY_PREF_BLUR_BG, false);
	}

	@Override
	public Boolean onHideWm() {
		boolean ret = mPref.getSPref().getBoolean(KEY_PREF_HIDE_WATTERMARK,
				false);
		int t = SystemProp.getTrial(mContext);
		return ret && t >= 1;
	}

	@Override
	public Boolean oneSS() {
		return mPref.getSPref().getBoolean(KEY_PREF_SINGLE_SS, false);
	}/* ImageTask.IImageTaskListener END */

	/* SaveTask.ISaveTaskListener */
	@Override
	public void onPostResult(File result) {
		if (result.exists()) {
			if (mPref.getSPref().getBoolean(KEY_PREF_HIDE_WATTERMARK, false)) {
				SystemProp.getOrPutTrial(mContext);
			}

			mBitmapSave.recycle();
			progDialog.setContent(getString(R.string.success));
			progDialog.dismiss();
			((HishootActivity) mContext).selectItem(getString(R.string.share),
					result.getAbsolutePath());

		} else {
			progDialog.setContent(getString(R.string.failed));
			progDialog.dismiss();
		}
	}

	@Override
	public int quality() {
		return imageQuality;
	}

	@Override
	public void onPreSave() {
		progDialog = getProgressDialog(R.string.save, R.string.please_wait);

	}/* SaveTask.ISaveTaskListener END */

}
