@file:Suppress("SpellCheckingInspection")

package template

import entity.Sizes
import template.TemplateConstants.DEFAULT_TEMPLATE_ID

sealed class Template {
    abstract val id: String
    abstract val author: String
    abstract val name: String
    abstract val desc: String
    abstract val frame: String
    abstract val preview: String
    abstract val sizes: Sizes
    abstract val coordinate: List<Float>
    abstract val installedDate: Long

    /** @since HiShoot */
    data class Default(
        override val frame: String,
        override val preview: String,
        override val sizes: Sizes,
        override val coordinate: List<Float>,
        override val installedDate: Long
    ) : Template() {
        override val author: String = "DCSMS aka JMKL"
        override val desc: String = "Template Default"
        override val id: String = DEFAULT_TEMPLATE_ID
        override val name: String = "Default"
    }

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
    ) : Template() {
        override val preview: String = frame
    }

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
        val glare: entity.Glare?
    ) : Template()

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
        val glare: entity.Glare?
    ) : Template()

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
        val glares: List<entity.Glare>?
    ) : Template()
}
