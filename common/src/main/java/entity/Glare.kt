package entity

import kotlinx.serialization.Serializable

@Serializable
data class Glare(
    val name: String,
    val size: Sizes,
    val position: SizesF
)
