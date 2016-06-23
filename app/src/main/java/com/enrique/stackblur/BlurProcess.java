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

public interface BlurProcess {
  /**
   * Process the given image, blurring by the supplied radius.
   * If radius is 0, this will return original
   *
   * @param original the bitmap to be blurred
   * @param radius the radius in pixels to blur the image
   * @return the blurred version of the image.
   */
  Bitmap blur(Bitmap original, float radius);
}
