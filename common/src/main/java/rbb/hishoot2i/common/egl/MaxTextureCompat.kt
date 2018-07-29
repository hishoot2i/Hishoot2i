package rbb.hishoot2i.common.egl

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1

object MaxTextureCompat {
    @JvmStatic
    fun get(): Int? = try {
        when {
            SDK_INT >= JELLY_BEAN_MR1 -> Egl14Impl()
            else -> Egl10Impl()
        }.get()
    } catch (ignore: Exception) {
        null //
    }
}