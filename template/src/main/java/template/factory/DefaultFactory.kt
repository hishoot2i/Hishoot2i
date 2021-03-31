package template.factory

import android.content.Context
import common.PathBuilder.stringDrawables
import common.ext.deviceSizes
import common.ext.dpSize
import entity.Sizes
import template.R
import template.Template.Default

class DefaultFactory(private val appContext: Context) : Factory<Default> {
    override fun newTemplate(): Default {
        val (topTop, topLeft) = appContext.run { dpSize(R.dimen.def_tt) to dpSize(R.dimen.def_tl) }
        val (bottomTop, bottomLeft) = appContext.run { dpSize(R.dimen.def_bt) to dpSize(R.dimen.def_bl) }
        val sizes = appContext.deviceSizes +
            Sizes(topLeft + bottomLeft, topTop + bottomTop)
        return Default(
            frame = stringDrawables(R.drawable.frame1), // [ignored] -> 9patch Drawable
            preview = stringDrawables(R.drawable.default_preview),
            sizes = sizes,
            coordinate = listOf(
                topLeft, topTop,
                sizes.x - bottomLeft, topTop,
                topLeft, sizes.y - bottomTop,
                sizes.x - bottomLeft, sizes.y - bottomTop
            ).map(Int::toFloat),
            installedDate = appContext.run {
                packageManager.getPackageInfo(packageName, 0).firstInstallTime
            }
        )
    }
}
