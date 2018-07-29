package rbb.hishoot2i.common.entity

import rbb.hishoot2i.common.ext.exhaustive

sealed class BackgroundMode {
    object Color : BackgroundMode()
    object Image : BackgroundMode()
    object Transparent : BackgroundMode()

    val id: Int
        get() = when (this) {
            Color -> ID_COLOR
            Image -> ID_IMAGE
            Transparent -> ID_TRANSPARENT
        }.exhaustive

    companion object {
        const val ID_COLOR: Int = 0
        const val ID_IMAGE: Int = 1
        const val ID_TRANSPARENT: Int = 2
        @JvmStatic
        fun fromId(id: Int): BackgroundMode = when (id) {
            ID_COLOR -> Color
            ID_IMAGE -> Image
            ID_TRANSPARENT -> Transparent
            else -> Color // fallback
        }
    }
}