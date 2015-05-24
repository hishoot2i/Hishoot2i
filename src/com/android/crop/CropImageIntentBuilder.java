/* Copyright 2009 The Android Open Source Project, Inc.
   Copyright 2014 Lorenzo Villani <lorenzo@villani.me>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.android.crop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class CropImageIntentBuilder {
	private static final String EXTRA_ASPECT_X = "aspectX";
	private static final String EXTRA_ASPECT_Y = "aspectY";
	private static final String EXTRA_OUTPUT_X = "outputX";
	private static final String EXTRA_OUTPUT_Y = "outputY";
	private static final String EXTRA_BITMAP_DATA = "data";
	private static final String EXTRA_SCALE = "scale";
	private static final String EXTRA_SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
	private static final String EXTRA_NO_FACE_DETECTION = "noFaceDetection";
	private static final String EXTRA_CIRCLE_CROP = "circleCrop";
	private static final String EXTRA_OUTPUT_FORMAT = "outputFormat";

	private static final int DEFAULT_SCALE = 1;

	private boolean scale = true;
	private boolean scaleUpIfNeeded = true;
	private boolean doFaceDetection = true;
	private boolean circleCrop = false;
	private String outputFormat = null;
	private Uri sourceImage;
	private Bitmap bitmap;

	private final int aspectX;
	private final int aspectY;
	private final int outputX;
	private final int outputY;
	private final Uri saveUri;

	/**
	 * Constructor.
	 *
	 * @param outputX
	 *            Output vertical size in pixels.
	 * @param outputY
	 *            Output horizontal size in pixels.
	 * @param saveUri
	 *            Output file URI.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder(final int outputX, final int outputY,
			final Uri saveUri) {
		this(DEFAULT_SCALE, DEFAULT_SCALE, outputX, outputY, saveUri);
	}

	/**
	 * Constructor.
	 *
	 * @param aspectX
	 *            Horizontal aspect ratio.
	 * @param aspectY
	 *            Vertical aspect ratio.
	 * @param outputX
	 *            Output vertical size in pixels.
	 * @param outputY
	 *            Output horizontal size in pixels.
	 * @param saveUri
	 *            Output file URI.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder(final int aspectX, final int aspectY,
			final int outputX, final int outputY, final Uri saveUri) {
		this.aspectX = aspectX;
		this.aspectY = aspectY;
		this.outputX = outputX;
		this.outputY = outputY;
		this.saveUri = saveUri;
	}

	/**
	 * Builds the Intent.
	 *
	 * @param context
	 *            The application context.
	 * @return The newly created intent.
	 * @since 1.0.1
	 */
	public Intent getIntent(final Context context) {
		final Intent intent = new Intent(context, CropImage.class);

		//
		// Required Intent extras.
		//

		intent.putExtra(EXTRA_ASPECT_X, this.aspectX);
		intent.putExtra(EXTRA_ASPECT_Y, this.aspectY);
		intent.putExtra(EXTRA_OUTPUT_X, this.outputX);
		intent.putExtra(EXTRA_OUTPUT_Y, this.outputY);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.saveUri);

		//
		// Optional Intent Extras
		//

		intent.putExtra(EXTRA_SCALE, this.scale);
		intent.putExtra(EXTRA_SCALE_UP_IF_NEEDED, this.scaleUpIfNeeded);
		intent.putExtra(EXTRA_NO_FACE_DETECTION, !this.doFaceDetection);
		intent.putExtra(EXTRA_CIRCLE_CROP, this.circleCrop);
		intent.putExtra(EXTRA_OUTPUT_FORMAT, this.outputFormat);

		if (this.bitmap != null) {
			intent.putExtra(EXTRA_BITMAP_DATA, this.bitmap);
		}

		if (this.sourceImage != null) {
			intent.setData(this.sourceImage);
		}

		return intent;
	}

	/**
	 * Scales down the picture.
	 *
	 * @param scale
	 *            Whether to scale down the image.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setScale(final boolean scale) {
		this.scale = scale;

		return this;
	}

	/**
	 * Whether to scale up the image if the cropped region is smaller than the
	 * output size.
	 *
	 * @param scaleUpIfNeeded
	 *            Whether to scale up the image.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setScaleUpIfNeeded(
			final boolean scaleUpIfNeeded) {
		this.scaleUpIfNeeded = scaleUpIfNeeded;

		return this;
	}

	/**
	 * Performs face detection before allowing users to crop the image.
	 *
	 * @param doFaceDetection
	 *            Whether to perform face detection.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setDoFaceDetection(
			final boolean doFaceDetection) {
		this.doFaceDetection = doFaceDetection;

		return this;
	}

	/**
	 * Sets bitmap data to crop. Please note that this method overrides any
	 * source image set by {@link #setSourceImage(Uri)}.
	 *
	 * @param bitmap
	 *            The {@link Bitmap} to crop.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setBitmap(final Bitmap bitmap) {
		this.bitmap = bitmap;

		return this;
	}

	/**
	 * Sets the Uri of the image to crop. It must be accessible to the calling
	 * application/activity.
	 *
	 * @param sourceImage
	 *            Uri of the image to crop.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setSourceImage(final Uri sourceImage) {
		this.sourceImage = sourceImage;

		return this;
	}

	/**
	 * Whether to crop the image as circle
	 *
	 * @param circleCrop
	 *            Whether to crop the image as circle.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setCircleCrop(final boolean circleCrop) {
		this.circleCrop = circleCrop;

		return this;
	}

	/**
	 * Set output format of the image to crop. If not set, JPEG will be used.
	 *
	 * @param outputFormat
	 *            Output format of the image to crop, such as JPEG, PNG or WEBP.
	 * @return This Builder object to allow for chaining of calls to set
	 *         methods.
	 * @since 1.0.1
	 */
	public CropImageIntentBuilder setOutputFormat(final String outputFormat) {
		this.outputFormat = outputFormat;

		return this;
	}
}
