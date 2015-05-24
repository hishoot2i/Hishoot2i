package org.illegaller.ratabb.hishoot2i.util;

import java.io.File;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class SaveTask extends AsyncTask<Bitmap, Void, File> {

	private OnSaveTaskListener mListener;

	public interface OnSaveTaskListener {
		void onPostResult(File result);

		void onPreSave();

		int quality();
	}

	public SaveTask(OnSaveTaskListener listener) {
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		mListener.onPreSave();
	}

	@Override
	protected void onPostExecute(File result) {
		mListener.onPostResult(result);
	}

	@Override
	protected File doInBackground(Bitmap... params) {
		return new Save().SaveBitmap(params[0], mListener.quality());
	}
}
