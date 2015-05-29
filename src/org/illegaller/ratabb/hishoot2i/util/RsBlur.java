//package org.illegaller.ratabb.hishoot2i.util;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.support.v8.renderscript.Allocation;
//import android.support.v8.renderscript.Element;
//import android.support.v8.renderscript.RenderScript;
//import android.support.v8.renderscript.ScriptIntrinsicBlur;
//
////XXX unused
//public class RsBlur {
//	public static Bitmap doBlur(Bitmap source, int radius, Context context)
//			throws OutOfMemoryError {
//		Bitmap blurred = Bitmap.createBitmap(source.getWidth(),
//				source.getHeight(), Bitmap.Config.ARGB_8888);
//		RenderScript rs = RenderScript.create(context);
//
//		Allocation input = Allocation.createFromBitmap(rs, source,
//				Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
//		Allocation output = Allocation.createTyped(rs, input.getType());
//
//		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
//				Element.U8_4(rs));
//		blurScript.setRadius(radius);
//		blurScript.setInput(input);
//		blurScript.forEach(output);
//		output.copyTo(blurred);
//		rs.destroy();
//		input.destroy();
//		output.destroy();
//		return blurred;
//	}
//}
