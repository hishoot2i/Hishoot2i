package org.illegaller.ratabb.hishoot2i.util;

import java.io.IOException;
import java.io.InputStream;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.skin.GetResources;
import org.illegaller.ratabb.hishoot2i.skin.SkinDescription;
import com.github.airk.tool.sobitmap.SoBitmap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import static org.illegaller.ratabb.hishoot2i.util.DrawView.recycleBitmap;

public class ImageTask extends AsyncTask<Void, Void, Bitmap> {
	private static final String TAG = "Hishoot:ImageTask";
	private OnImageTaskListener mListener;
	private SkinDescription skinDescription;
	private Point wh;
	private Context mContext;
	boolean mThrow = false, single = false, blur = false;

	public interface OnImageTaskListener {
		void onPostResult(Bitmap result);

		void onPre();

		void onThrow(boolean flag);

		String packageTemplate();

		String[] bitmapAll();

		Point WH();

		Bitmap[] wat();

		Boolean oneSS();

		Boolean onBlur();

	}

	public ImageTask(Context context, OnImageTaskListener listener)
			throws Throwable {
		mListener = listener;
		mContext = context;
	}

	private Bitmap loadBitmap(String data) {
		if (data != null) {
			try {
				return SoBitmap.getInstance(mContext).huntBlock(TAG,
						Uri.parse(data));
			} catch (Exception e) {
				Log.e(TAG, "loadBitmap: " + e.getMessage());
			}
		}
		return DrawView.imageDefault(mContext);
	}

	@Override
	protected void onPreExecute() {
		mListener.onPre();
		mThrow = false;
		wh = mListener.WH();
		single = mListener.oneSS();
		blur = mListener.onBlur();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		mListener.onPostResult(result);
		mListener.onThrow(mThrow);
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		//
		System.gc();
		long startMs = System.currentTimeMillis();
		int lebar = wh.x, tinggi = wh.y;
		String[] allBitmap = mListener.bitmapAll();

		Bitmap ss1 = scaledBitmap(loadBitmap(allBitmap[0]), lebar, tinggi);
		Bitmap ss2 = scaledBitmap(loadBitmap(allBitmap[1]), lebar, tinggi);
		Bitmap wall = loadBitmap(allBitmap[2]);

		int TL = getDimensionPixelSize(R.dimen.def_tl);
		int TT = getDimensionPixelSize(R.dimen.def_tt);
		int BL = getDimensionPixelSize(R.dimen.def_bl);
		int BT = getDimensionPixelSize(R.dimen.def_bt);

		int topx = (int) (TL);
		int topy = (int) (TT);
		int totx = (int) ((TL + BL));
		int toty = (int) ((TT + BT));

		Bitmap frame = DrawView.getNine(R.drawable.frame1, lebar + totx, tinggi
				+ toty, mContext);

		// XXX TEMPLATE
		String skinPkg = mListener.packageTemplate();
		if (skinPkg != null) {
			GetResources getResources = new GetResources(mContext);
			Bitmap framefrom = getResources.getImage(skinPkg, "skin");

			Context c;
			InputStream is = null;
			try {
				c = mContext.createPackageContext(skinPkg, 0);
				AssetManager am = c.getAssets();
				is = am.open("keterangan.xml");
				if (is != null) {
					skinDescription = new SkinDescription(is);
					is.close();
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			TL = notNol(skinDescription.getTx());
			TT = notNol(skinDescription.getTy());
			BL = notNol(skinDescription.getBx());
			BT = notNol(skinDescription.getBy());

			topx = (int) (TL);
			topy = (int) (TT);
			totx = (int) ((TL + BL));
			toty = (int) ((TT + BT));

			recycleBitmap(frame);
			frame = Bitmap.createScaledBitmap(framefrom, lebar + totx, tinggi
					+ toty, true);
			recycleBitmap(framefrom);

		}
		Bitmap xwall = null, mixthem = null, mix1 = null, mix2 = null;
		try {
			mix1 = DrawView.DrawMe(frame, 0, 0, ss1, topx, topy, mContext,
					lebar + totx, tinggi + toty);

			mix2 = (single) ? null : DrawView.DrawMe(frame, 0, 0, ss2, topx,
					topy, mContext, lebar + totx, tinggi + toty);

			recycleBitmap(frame);
			recycleBitmap(ss1);
			recycleBitmap(ss2);

			mixthem = Bitmap.createBitmap(
					(single) ? mix1.getWidth() : mix1.getWidth() * 2,
					mix1.getHeight(), Bitmap.Config.ARGB_8888);

			Point point = getPointMax(single,
					new Point(wall.getWidth(), wall.getHeight()), new Point(
							mixthem.getWidth(), mixthem.getHeight()));

			xwall = DrawView.resizeImage(wall, point.x, point.y);
		} catch (Throwable e) {
			// OOM ?
			Log.e(TAG, e.getMessage());
			mThrow = true;
			xwall = wall;
			mixthem = Bitmap.createBitmap(mix1.getWidth(), mix1.getHeight(),
					Bitmap.Config.ARGB_8888);
		}

		Bitmap wm = mListener.wat()[0];
		Bitmap wmi = mListener.wat()[1];
		Canvas canvas = new Canvas(mixthem);

		if (blur) {
			DrawView.drawBlurBitmap(canvas, xwall, mContext);
		} else {
			canvas.drawBitmap(xwall, 0, 0, null);
		}

		canvas.drawBitmap(mix1, 0, 0, null);// ss1
		if (!single) {
			if (mix2 != null) {
				canvas.drawBitmap(mix2, mix1.getWidth(), 0, null);// ss2
			}
		}
		canvas.drawBitmap(wm, (mixthem.getWidth() / 2) - (wm.getWidth() / 2),
				(mixthem.getHeight() - wm.getHeight()), null);
		canvas.drawBitmap(wmi, mixthem.getWidth() - wmi.getWidth(), 0f, null);

		recycleBitmap(mix1);
		recycleBitmap(mix2);
		recycleBitmap(wall);
		recycleBitmap(xwall);
		Log.d(TAG, "doInBackground: " + (System.currentTimeMillis() - startMs)
				+ "ms");
		return mixthem;
	}

	private int notNol(int i) {
		return (i > 0) ? i : 1;
	}

	private int getDimensionPixelSize(int dimenId) {
		return mContext.getResources().getDimensionPixelSize(dimenId);
	}

	private Bitmap scaledBitmap(Bitmap src, int dstWidth, int dstHeight) {
		return Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
	}

	private Point getPointMax(boolean flag, Point wall, Point mix) {
		int w = mix.x, h = mix.y;
		boolean wmin = wall.x < mix.x, hmin = wall.y < mix.y;

		if (flag) {
			if (wmin) {
				h = mix.y * mix.y;
				w = mix.x;
			}
			if (hmin) {
				h = mix.y;
				w = mix.x * mix.x;
			}
		} else {
			if (hmin) {
				h = mix.y;
				w = mix.x * mix.x;
			}
			if (wmin) {
				h = mix.y * mix.y;
				w = mix.x;
			}
		}

		return new Point(w, h);
	}

}
