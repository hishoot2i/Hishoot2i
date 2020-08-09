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

import java.util.concurrent.Callable

internal class BlurTask(
    private val src: IntArray,
    private val width: Int,
    private val hight: Int,
    private val radius: Int,
    private val totalCores: Int,
    private val coreIndex: Int,
    private val step: Int
) : Callable<Unit> {
    @Throws(Exception::class)
    override fun call() {
        var x: Int
        var y: Int
        var xp: Int
        var yp: Int
        var i: Int
        var sp: Int
        var stackStart: Int
        var stackI: Int
        var srcI: Int
        var dstI: Int
        var sumR: Long
        var sumG: Long
        var sumB: Long
        var sumInR: Long
        var sumInG: Long
        var sumInB: Long
        var sumOutR: Long
        var sumOutG: Long
        var sumOutB: Long
        val wm = width - 1
        val hm = hight - 1
        val div = radius * 2 + 1
        val mulSum = STACK_BLUR_MUL[radius].toInt()
        val shrSum = STACK_BLUR_SHR[radius]
        val stack = IntArray(div)
        if (step == 1) {
            val minY = coreIndex * hight / totalCores
            val maxY = (coreIndex + 1) * hight / totalCores
            y = minY
            while (y < maxY) {
                sumOutB = 0
                sumOutG = sumOutB
                sumOutR = sumOutG
                sumInB = sumOutR
                sumInG = sumInB
                sumInR = sumInG
                sumB = sumInR
                sumG = sumB
                sumR = sumG
                srcI = width * y // start of line (0,y)
                i = 0
                while (i <= radius) {
                    stackI = i
                    stack[stackI] = src[srcI]
                    sumR += (src[srcI] ushr 16 and 0xff) * (i + 1).toLong()
                    sumG += (src[srcI] ushr 8 and 0xff) * (i + 1).toLong()
                    sumB += (src[srcI] and 0xff) * (i + 1).toLong()
                    sumOutR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumOutG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumOutB += (src[srcI] and 0xff).toLong()
                    i++
                }
                i = 1
                while (i <= radius) {
                    if (i <= wm) srcI += 1
                    stackI = i + radius
                    stack[stackI] = src[srcI]
                    sumR += (src[srcI] ushr 16 and 0xff) * (radius + 1 - i).toLong()
                    sumG += (src[srcI] ushr 8 and 0xff) * (radius + 1 - i).toLong()
                    sumB += (src[srcI] and 0xff) * (radius + 1 - i).toLong()
                    sumInR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumInG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumInB += (src[srcI] and 0xff).toLong()
                    i++
                }
                sp = radius
                xp = radius
                if (xp > wm) xp = wm
                srcI = xp + y * width //   img.pix_ptr(xp, y);
                dstI = y * width // img.pix_ptr(0, y);
                x = 0
                while (x < width) {
                    src[dstI] = (src[dstI] and -0x1000000 or
                            (sumR * mulSum ushr shrSum.toInt() and 0xff shl 16).toInt() or
                            (sumG * mulSum ushr shrSum.toInt() and 0xff shl 8).toInt() or
                            (sumB * mulSum ushr shrSum.toInt() and 0xff).toInt())
                    dstI += 1
                    sumR -= sumOutR
                    sumG -= sumOutG
                    sumB -= sumOutB
                    stackStart = sp + div - radius
                    if (stackStart >= div) stackStart -= div
                    stackI = stackStart
                    sumOutR -= (stack[stackI] ushr 16 and 0xff).toLong()
                    sumOutG -= (stack[stackI] ushr 8 and 0xff).toLong()
                    sumOutB -= (stack[stackI] and 0xff).toLong()
                    if (xp < wm) {
                        srcI += 1
                        ++xp
                    }
                    stack[stackI] = src[srcI]
                    sumInR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumInG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumInB += (src[srcI] and 0xff).toLong()
                    sumR += sumInR
                    sumG += sumInG
                    sumB += sumInB
                    ++sp
                    if (sp >= div) sp = 0
                    stackI = sp
                    sumOutR += (stack[stackI] ushr 16 and 0xff).toLong()
                    sumOutG += (stack[stackI] ushr 8 and 0xff).toLong()
                    sumOutB += (stack[stackI] and 0xff).toLong()
                    sumInR -= (stack[stackI] ushr 16 and 0xff).toLong()
                    sumInG -= (stack[stackI] ushr 8 and 0xff).toLong()
                    sumInB -= (stack[stackI] and 0xff).toLong()
                    x++
                }
                y++
            }
        } else if (step == 2) { // step 2
            val minX = coreIndex * width / totalCores
            val maxX = (coreIndex + 1) * width / totalCores
            x = minX
            while (x < maxX) {
                sumOutB = 0
                sumOutG = sumOutB
                sumOutR = sumOutG
                sumInB = sumOutR
                sumInG = sumInB
                sumInR = sumInG
                sumB = sumInR
                sumG = sumB
                sumR = sumG
                srcI = x // x,0
                i = 0
                while (i <= radius) {
                    stackI = i
                    stack[stackI] = src[srcI]
                    sumR += (src[srcI] ushr 16 and 0xff) * (i + 1).toLong()
                    sumG += (src[srcI] ushr 8 and 0xff) * (i + 1).toLong()
                    sumB += (src[srcI] and 0xff) * (i + 1).toLong()
                    sumOutR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumOutG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumOutB += (src[srcI] and 0xff).toLong()
                    i++
                }
                i = 1
                while (i <= radius) {
                    if (i <= hm) srcI += width // +stride
                    stackI = i + radius
                    stack[stackI] = src[srcI]
                    sumR += (src[srcI] ushr 16 and 0xff) * (radius + 1 - i).toLong()
                    sumG += (src[srcI] ushr 8 and 0xff) * (radius + 1 - i).toLong()
                    sumB += (src[srcI] and 0xff) * (radius + 1 - i).toLong()
                    sumInR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumInG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumInB += (src[srcI] and 0xff).toLong()
                    i++
                }
                sp = radius
                yp = radius
                if (yp > hm) yp = hm
                srcI = x + yp * width // img.pix_ptr(x, yp);
                dstI = x // img.pix_ptr(x, 0);
                y = 0
                while (y < hight) {
                    src[dstI] = (src[dstI] and -0x1000000 or
                            (sumR * mulSum ushr shrSum.toInt() and 0xff shl 16).toInt() or
                            (sumG * mulSum ushr shrSum.toInt() and 0xff shl 8).toInt() or
                            (sumB * mulSum ushr shrSum.toInt() and 0xff).toInt())
                    dstI += width
                    sumR -= sumOutR
                    sumG -= sumOutG
                    sumB -= sumOutB
                    stackStart = sp + div - radius
                    if (stackStart >= div) stackStart -= div
                    stackI = stackStart
                    sumOutR -= (stack[stackI] ushr 16 and 0xff).toLong()
                    sumOutG -= (stack[stackI] ushr 8 and 0xff).toLong()
                    sumOutB -= (stack[stackI] and 0xff).toLong()
                    if (yp < hm) {
                        srcI += width // stride
                        ++yp
                    }
                    stack[stackI] = src[srcI]
                    sumInR += (src[srcI] ushr 16 and 0xff).toLong()
                    sumInG += (src[srcI] ushr 8 and 0xff).toLong()
                    sumInB += (src[srcI] and 0xff).toLong()
                    sumR += sumInR
                    sumG += sumInG
                    sumB += sumInB
                    ++sp
                    if (sp >= div) sp = 0
                    stackI = sp
                    sumOutR += (stack[stackI] ushr 16 and 0xff).toLong()
                    sumOutG += (stack[stackI] ushr 8 and 0xff).toLong()
                    sumOutB += (stack[stackI] and 0xff).toLong()
                    sumInR -= (stack[stackI] ushr 16 and 0xff).toLong()
                    sumInG -= (stack[stackI] ushr 8 and 0xff).toLong()
                    sumInB -= (stack[stackI] and 0xff).toLong()
                    y++
                }
                x++
            }
        }
    }

    companion object {
        private val STACK_BLUR_MUL = shortArrayOf(
            512, 512, 456, 512, 328, 456, 335, 512, 405, 328,
            271, 456, 388, 335, 292, 512, 454, 405, 364, 328, 298, 271, 496, 456, 420, 388, 360,
            335, 312, 292, 273, 512, 482, 454, 428, 405, 383, 364, 345, 328, 312, 298, 284, 271,
            259, 496, 475, 456, 437, 420, 404, 388, 374, 360, 347, 335, 323, 312, 302, 292, 282,
            273, 265, 512, 497, 482, 468, 454, 441, 428, 417, 405, 394, 383, 373, 364, 354, 345,
            337, 328, 320, 312, 305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475, 465,
            456, 446, 437, 428, 420, 412, 404, 396, 388, 381, 374, 367, 360, 354, 347, 341, 335,
            329, 323, 318, 312, 307, 302, 297, 292, 287, 282, 278, 273, 269, 265, 261, 512, 505,
            497, 489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417, 411, 405, 399, 394,
            389, 383, 378, 373, 368, 364, 359, 354, 350, 345, 341, 337, 332, 328, 324, 320, 316,
            312, 309, 305, 301, 298, 294, 291, 287, 284, 281, 278, 274, 271, 268, 265, 262, 259,
            257, 507, 501, 496, 491, 485, 480, 475, 470, 465, 460, 456, 451, 446, 442, 437, 433,
            428, 424, 420, 416, 412, 408, 404, 400, 396, 392, 388, 385, 381, 377, 374, 370, 367,
            363, 360, 357, 354, 350, 347, 344, 341, 338, 335, 332, 329, 326, 323, 320, 318, 315,
            312, 310, 307, 304, 302, 299, 297, 294, 292, 289, 287, 285, 282, 280, 278, 275, 273,
            271, 269, 267, 265, 263, 261, 259
        )
        private val STACK_BLUR_SHR = byteArrayOf(
            9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16,
            16, 16, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19,
            19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
            20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
            21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22,
            22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
            22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
            24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
        )
    }
}
