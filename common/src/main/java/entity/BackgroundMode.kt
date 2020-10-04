package entity

enum class BackgroundMode {
    COLOR, IMAGE, TRANSPARENT;

    val isColor: Boolean get() = this == COLOR
    val isImage: Boolean get() = this == IMAGE
    val isTransparent: Boolean get() = this == TRANSPARENT
}
