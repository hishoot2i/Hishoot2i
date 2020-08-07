package entity

sealed class BackgroundMode(val id: Int) {
    object Color : BackgroundMode(0)
    object Image : BackgroundMode(1)
    object Transparent : BackgroundMode(2)

    val isImage get() = this is Image
    val isColor get() = this is Color
    val isTransparent get() = this is Transparent

    companion object {
        @JvmStatic
        fun fromId(id: Int): BackgroundMode = when (id) {
            0 -> Color
            1 -> Image
            2 -> Transparent
            else -> Color // fallback
        }
    }
}
