@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.IOException
import java.io.InputStream

const val POINT_OF_FIVE = .5F
inline val Context.displayMetrics: DisplayMetrics
    get() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
        val ret = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getRealMetrics(ret)
        ret
    } else resources.displayMetrics
inline val Context.scaledDensity
    get() = displayMetrics.scaledDensity
inline val Context.density
    get() = displayMetrics.density
inline val Context.deviceWidth
    get() = displayMetrics.widthPixels
inline val Context.deviceHeight
    get() = displayMetrics.heightPixels

inline fun Context.dp2px(dp: Int): Float = dp * density + POINT_OF_FIVE
inline fun Context.textSize(size: Int): Float = size * scaledDensity + POINT_OF_FIVE
@Throws(PackageManager.NameNotFoundException::class)
inline fun Context.resourcesFrom(packageName: String): Resources =
    createPackageContext(packageName, 0).resources

@Throws(PackageManager.NameNotFoundException::class, IOException::class)
inline fun Context.openAssetsFrom(packageName: String, assetsName: String): InputStream =
    resourcesFrom(packageName).assets.open(assetsName)

inline fun Context.drawableSizes(packageName: String, drawableName: String): entity.Sizes? {
    val res = resourcesFrom(packageName)
    val id = res.getIdentifier(drawableName, "drawable", packageName)
    if (id > 0) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, id, options)
        if (options.outWidth > 0 && options.outHeight > 0) {
            return entity.Sizes(options.outWidth, options.outHeight)
        }
    }
    return null
}

val DEF_FLAG_PENDING_INTENT
    get() = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT

@JvmOverloads
inline fun Context.activityPendingIntent(
    flag: Int = DEF_FLAG_PENDING_INTENT,
    crossinline intent: () -> Intent
): PendingIntent = PendingIntent.getActivity(this, 0, intent(), flag)
