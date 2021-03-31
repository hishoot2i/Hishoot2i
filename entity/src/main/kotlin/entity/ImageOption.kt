package entity

enum class ImageOption {
    SCALE_FILL, CENTER_CROP, MANUAL_CROP;

    val isManualCrop get() = this == MANUAL_CROP

    companion object
}
