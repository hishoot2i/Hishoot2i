package rbb.hishoot2i.template.factory

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import rbb.hishoot2i.common.PathBuilder.stringDrawables
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.deviceHeight
import rbb.hishoot2i.common.ext.deviceWidth
import rbb.hishoot2i.template.R
import rbb.hishoot2i.template.Template

class DefaultFactory(private val appContext: Context) : Factory<Template.Default> {
    @Throws(Exception::class)
    override fun newTemplate(): Template.Default {
        val res = appContext.resources
        val topTop = res.getDimensionPixelSize(R.dimen.def_tt)
        val topLeft = res.getDimensionPixelSize(R.dimen.def_tl)
        val bottomTop = res.getDimensionPixelSize(R.dimen.def_bt)
        val bottomLeft = res.getDimensionPixelSize(R.dimen.def_bl)
        val sizes = getDeviceSize(appContext) +
                Sizes(topLeft + bottomLeft, topTop + bottomTop)
        val coordinate = listOf(
            topLeft, topTop,
            sizes.x - bottomLeft, topTop,
            topLeft, sizes.y - bottomTop,
            sizes.x - bottomLeft, sizes.y - bottomTop
        ).map { it.toFloat() }
        val firstInstallTime = appContext.packageManager.getPackageInfo(
            appContext.packageName, 0
        ).firstInstallTime
        return Template.Default(
            stringDrawables(R.drawable.frame1), // [ignored]
            stringDrawables(R.drawable.default_preview),
            sizes,
            coordinate,
            firstInstallTime
        )
    }

    // ?
    private fun getDeviceSize(context: Context): Sizes = with(context) {
        if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
            Sizes(deviceHeight, deviceWidth)
        } else {
            Sizes(deviceWidth, deviceHeight)
        }
    }
}