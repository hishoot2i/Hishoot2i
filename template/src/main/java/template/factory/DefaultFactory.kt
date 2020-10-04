package template.factory

import android.content.Context
import common.PathBuilder.stringDrawables
import common.ext.deviceSizes
import common.ext.dpSize
import entity.Sizes
import template.R
import template.Template.Default

class DefaultFactory(private val appContext: Context) : Factory<Default> {
    @Throws(Exception::class)
    override fun newTemplate(): Default {
        val topTop = appContext.dpSize(template.R.dimen.def_tt)
        val topLeft = appContext.dpSize(template.R.dimen.def_tl)
        val bottomTop = appContext.dpSize(template.R.dimen.def_bt)
        val bottomLeft = appContext.dpSize(template.R.dimen.def_bl)
        val sizes = appContext.deviceSizes +
                Sizes(topLeft + bottomLeft, topTop + bottomTop)
        val coordinate = listOf(
            topLeft, topTop,
            sizes.x - bottomLeft, topTop,
            topLeft, sizes.y - bottomTop,
            sizes.x - bottomLeft, sizes.y - bottomTop
        ).map { it.toFloat() }

        return Default(
            stringDrawables(R.drawable.frame1), // [ignored]
            stringDrawables(R.drawable.default_preview),
            sizes,
            coordinate,
            firstInstallTime
        )
    }

    private val firstInstallTime
        get() = appContext.run {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        }
}
