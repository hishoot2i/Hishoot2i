package rbb.hishoot2i.template

import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes
import rbb.hishoot2i.common.ext.exhaustive

sealed class Template(
    open val id: String,
    open val author: String,
    open val name: String,
    open val desc: String,
    open val frame: String,
    open val preview: String,
    open val sizes: Sizes,
    open val coordinate: List<Float>,
    open val installedDate: Long
) {
    val isDefault: Boolean
        get() = this is Default
    val isNotEmpty: Boolean
        get() = this != Empty

    fun containsNameOrAuthor(query: String): Boolean =
        name.contains(query, ignoreCase = true) ||
                author.contains(query, ignoreCase = true)

    internal val indexTypeSort: Byte
        get() = when (this) {
            is Default -> 0
            is Version1 -> 1
            is Version2 -> 2
            is Version3 -> 3
            is VersionHtz -> 4
            is Empty -> Byte.MAX_VALUE
        }.exhaustive

    /** Fallback */
    object Empty : Template(
        EMPTIES,
        EMPTIES,
        EMPTIES,
        EMPTIES,
        EMPTIES,
        EMPTIES,
        Sizes.ZERO,
        emptyList(),
        -1L
    )

    /** @since HiShoot */
    data class Default(
        override val frame: String,
        override val preview: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long
    ) : Template(
        TemplateConstants.DEFAULT_TEMPLATE_ID,
        DEFAULT_AUTHOR,
        DEFAULT_NAME,
        DEFAULT_DESC,
        frame,
        preview,
        sizes,
        coordinate,
        installedDate
    )

    /** @since HiShoot */
    data class Version1(
        override val id: String,
        override val author: String,
        override val name: String,
        override val desc: String,
        override val frame: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long
    ) : Template(id, author, name, desc, frame, frame, sizes, coordinate, installedDate)

    /** @since 1.0.0 (20151223) */
    data class VersionHtz(
        override val id: String,
        override val author: String,
        override val name: String,
        override val desc: String,
        override val frame: String,
        override val preview: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long,
        val glare: Glare?
    ) : Template(id, author, name, desc, frame, preview, sizes, coordinate, installedDate)

    /** @since 1.0.0 (20151223) */
    data class Version2(
        override val id: String,
        override val author: String,
        override val name: String,
        override val desc: String,
        override val frame: String,
        override val preview: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long,
        val shadow: String,
        val glare: Glare?
    ) : Template(id, author, name, desc, frame, preview, sizes, coordinate, installedDate)

    /** @since 1.2.0 (20180730) */
    data class Version3(
        override val id: String,
        override val author: String,
        override val name: String,
        override val desc: String,
        override val frame: String,
        override val preview: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long,
        val shadow: String?,
        val glares: List<Glare>?
    ) : Template(id, author, name, desc, frame, preview, sizes, coordinate, installedDate)

    companion object {
        private const val EMPTIES = ""
        private const val DEFAULT_AUTHOR = "DCSMS aka JMKL"
        private const val DEFAULT_NAME = "Default"
        private const val DEFAULT_DESC = "Template Default"
    }
}