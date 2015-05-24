package org.illegaller.ratabb.hishoot2i.util;

import static org.illegaller.ratabb.hishoot2i.Constants.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;

public class Save {
	/** <strong>Hint: </strong>{@link Bitmap#compress} */

	public File SaveBitmap(Bitmap bmp, @IMAGE_QUALITY int _imageQuality) {

		String extension = (_imageQuality == IQ_HI) ? ".png" : ".jpg";
		
		int quality = (_imageQuality == IQ_LOW) ? 60 : 80;
		Bitmap.CompressFormat format = (_imageQuality == IQ_HI) ? Bitmap.CompressFormat.PNG
				: Bitmap.CompressFormat.JPEG;

		String filename = String.format("HiShoot-%s%s",
				String.valueOf(System.currentTimeMillis()), extension);
		OutputStream outStream = null;
		File file = getHishootDirectory(filename);
		try {

			outStream = new FileOutputStream(file);
			bmp.compress(format, quality, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bmp.recycle();

		}

		return file;
	}

	private File getHishootDirectory(String namafile) {
		String sdcard = Environment.getExternalStorageDirectory() + "/HiShoot/";
		File f = new File(sdcard);
		if (!f.exists()) {
			f.mkdirs();
		}
		return new File(sdcard, namafile);
	}

	/**  */
	public void saveFile(Bitmap bitmap, String namafile) {
		byte[] data = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		data = baos.toByteArray();
		String a = Base64.encodeToString(data, Base64.DEFAULT);

		File file = new File(Environment.getExternalStorageDirectory(),
				namafile);
		OutputStream outStream = null;

		try {
			outStream = new FileOutputStream(file);
			PrintWriter print = new PrintWriter(outStream);
			print.append(a);
			print.flush();
			print.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
