package common.egl

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1

object MaxTextureCompat : MaxTexture {
    private val IMPL: MaxTexture by lazy(this) {
        if (SDK_INT >= JELLY_BEAN_MR1) Egl14Impl() else Egl10Impl()
    }

    override fun get(): Int? = try {
        IMPL.get()
    } catch (ignore: Exception) {
        null
    }
}