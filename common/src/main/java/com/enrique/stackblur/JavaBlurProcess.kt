@file:Suppress("SpellCheckingInspection")
/*
 * StackBlur v1.0 for Android
 *
 * @Author: Enrique LÃ³pez MaÃ±as <eenriquelopez@gmail.com>
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
 * @copyright: Enrique LÃ³pez MaÃ±as
 * @license: Apache License 2.0
 */
package com.enrique.stackblur

import android.graphics.Bitmap
import java.util.concurrent.Executors

class JavaBlurProcess : BlurProcess {
    override fun blur(original: Bitmap, radius: Int): Bitmap? {
        val w = original.width
        val h = original.height
        val currentPixels = IntArray(w * h)
        original.getPixels(currentPixels, 0, w, 0, 0, w, h)
        val cores = EXECUTOR_THREADS
        val horizontal = ArrayList<BlurTask>(cores)
        val vertical = ArrayList<BlurTask>(cores)
        for (i in 0 until cores) {
            horizontal.add(BlurTask(currentPixels, w, h, radius, cores, i, 1))
            vertical.add(BlurTask(currentPixels, w, h, radius, cores, i, 2))
        }
        try {
            EXECUTOR.invokeAll(horizontal)
            EXECUTOR.invokeAll(vertical)
        } catch (e: InterruptedException) {
            return null
        }
        return Bitmap.createBitmap(currentPixels, w, h, original.config)
    }

    companion object {
        private val EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors()
        private val EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS)
    }
}
