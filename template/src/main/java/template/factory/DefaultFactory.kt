package template.factory

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import common.PathBuilder.stringDrawables
import common.ext.deviceHeight
import common.ext.deviceWidth

class DefaultFactory(private val appContext: Context) : Factory<template.Template.Default> {
    @Throws(Exception::class) override fun newTemplate(): template.Template.Default {
        val res = appContext.resources
        val topTop = res.getDimensionPixelSize(template.R.dimen.def_tt)
        val topLeft = res.getDimensionPixelSize(template.R.dimen.def_tl)
        val bottomTop = res.getDimensionPixelSize(template.R.dimen.def_bt)
        val bottomLeft = res.getDimensionPixelSize(template.R.dimen.def_bl)
        val sizes = deviceSizes +
                entity.Sizes(topLeft + bottomLeft, topTop + bottomTop)
        val coordinate = listOf(
            topLeft, topTop,
            sizes.x - bottomLeft, topTop,
            topLeft, sizes.y - bottomTop,
            sizes.x - bottomLeft, sizes.y - bottomTop
        ).map { it.toFloat() }

        return template.Template.Default(
            stringDrawables(template.R.drawable.frame1), // [ignored]
            stringDrawables(template.R.drawable.default_preview),
            sizes,
            coordinate,
            firstInstallTime
        )
    }

    private val deviceSizes
        get() = with(appContext) {
            if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
                entity.Sizes(deviceHeight, deviceWidth)
            } else {
                entity.Sizes(deviceWidth, deviceHeight)
            }
        }
    private val firstInstallTime
        get() = with(appContext) {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        }
}
