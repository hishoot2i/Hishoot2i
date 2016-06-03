/**
 * StackBlur v1.0 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * Author of the original algorithm: Mario Klingemann <mario.quasimondo.com>
 *
 * This is a compromise between Gaussian Blur and Box blur
 * It creates much better looking blurs than Box Blur, but is
 * 7x faster than my Gaussian Blur implementation.
 *
 * I called it Stack Blur because this describes best how this
 * filter works internally: it creates a kind of moving stack
 * of colors whilst scanning through the image. Thereby it
 * just has to add one new block of color to the right side
 * of the stack and remove the leftmost color. The remaining
 * colors on the topmost layer of the stack are either added on
 * or reduced by one, depending on if they are on the right or
 * on the left side of the stack.
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */
package com.enrique.stackblur;

import android.graphics.Bitmap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackBlurManager {
  static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
  static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

  /**
   * Original image
   */
  private final Bitmap mImage;
  /**
   * Method of blurring
   */
  private final BlurProcess mBlurProcess;
  /**
   * Most recent result of blurring
   */
  private Bitmap mResult;

  /**
   * Constructor method (basic initialization and construction of the pixel array)
   *
   * @param image The image that will be analyed
   */
  public StackBlurManager(Bitmap image) {
    mImage = image;
    mBlurProcess = new JavaBlurProcess();
  }

  /**
   * Process the image on the given radius. Radius must be at least 1
   */
  public Bitmap process(int radius) {
    mResult = mBlurProcess.blur(mImage, radius);
    return mResult;
  }

  /**
   * Returns the blurred image as a bitmap
   *
   * @return blurred image
   */
  public Bitmap returnBlurredImage() {
    return mResult;
  }
}
