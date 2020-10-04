@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.app.PendingIntent
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.BitmapFactory
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.annotation.Px
import entity.Sizes
import java.io.IOException
import java.io.InputStream

const val POINT_OF_FIVE = .5F
inline val Context.displayMetrics: DisplayMetrics
    get() = if (SDK_INT > JELLY_BEAN) {
        val ret = DisplayMetrics()
        (getSystemService(WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.getRealMetrics(ret)
        ret
    } else resources.displayMetrics

inline val Context.deviceWidth
    get() = displayMetrics.widthPixels

inline val Context.deviceHeight
    get() = displayMetrics.heightPixels

inline val Context.deviceSizes
    get() = if (isLandScape) Sizes(deviceHeight, deviceWidth)
    else Sizes(deviceWidth, deviceHeight)

inline val Context.isLandScape
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

inline fun Context.dp2px(dp: Float): Float =
    TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, displayMetrics)

@Px
inline fun Context.dpSize(@DimenRes id: Int): Int = resources.getDimensionPixelSize(id)

inline fun Context.textSize(sp: Float): Float =
    TypedValue.applyDimension(COMPLEX_UNIT_SP, sp, displayMetrics)

@Throws(NameNotFoundException::class)
inline fun Context.resourcesFrom(packageName: String): Resources =
    createPackageContext(packageName, 0).resources

@Throws(NameNotFoundException::class, IOException::class)
inline fun Context.openAssetsFrom(packageName: String, assetsName: String): InputStream =
    resourcesFrom(packageName).assets.open(assetsName)

inline fun Context.drawableSizes(packageName: String, drawableName: String): Sizes? {
    val res = resourcesFrom(packageName)
    val id = res.getIdentifier(drawableName, "drawable", packageName)
    if (id > 0) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, id, options)
        if (options.outWidth > 0 && options.outHeight > 0) {
            return Sizes(options.outWidth, options.outHeight)
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

@Throws(NotFoundException::class)
inline fun Resources.openRawResource(name: String, type: String, pkg: String): InputStream? =
    getIdentifier(name, type, pkg).takeIf { it != 0 }?.run { openRawResource(this) }
