package rbb.hishoot2i.template

import java.text.Collator
import kotlin.LazyThreadSafetyMode.NONE

sealed class TemplateComparator(val id: Int) : Comparator<Template> {
    private fun defaultAlwaysOnTop(lhs: Template, rhs: Template): Int =
        if (lhs.isDefault || rhs.isDefault) 1 else actualCompare(lhs, rhs)

    internal abstract fun actualCompare(lhs: Template, rhs: Template): Int
    override fun compare(lhs: Template, rhs: Template): Int = defaultAlwaysOnTop(lhs, rhs)

    object NameAsc : TemplateComparator(NAME_ASC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int =
            collator.compare(lhs.name, rhs.name)
    }

    object NameDesc : TemplateComparator(NAME_DESC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int =
            collator.compare(rhs.name, lhs.name)
    }

    object TypeAsc : TemplateComparator(TYPE_ASC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int {
            val ret = lhs.indexTypeSort.compareTo(rhs.indexTypeSort)
            return when (ret) {
                0 -> NameAsc.actualCompare(lhs, rhs)
                else -> ret
            }
        }
    }

    object TypeDesc : TemplateComparator(TYPE_DESC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int {
            val ret = rhs.indexTypeSort.compareTo(lhs.indexTypeSort)
            return when (ret) {
                0 -> NameAsc.actualCompare(lhs, rhs) //
                else -> ret
            }
        }
    }

    object DateAsc : TemplateComparator(DATE_ASC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int {
            val ret = lhs.installedDate.compareTo(rhs.installedDate)
            return when (ret) {
                0 -> NameAsc.actualCompare(lhs, rhs)
                else -> ret
            }
        }
    }

    object DateDesc : TemplateComparator(DATE_DESC_ID) {
        override fun actualCompare(lhs: Template, rhs: Template): Int {
            val ret = rhs.installedDate.compareTo(lhs.installedDate)
            return when (ret) {
                0 -> NameAsc.actualCompare(lhs, rhs) //
                else -> ret
            }
        }
    }

    protected val collator: Collator by lazy(NONE) { Collator.getInstance() }

    companion object {
        const val NAME_ASC_ID = 0
        const val NAME_DESC_ID = 1
        const val TYPE_ASC_ID = 2
        const val TYPE_DESC_ID = 3
        const val DATE_ASC_ID = 4
        const val DATE_DESC_ID = 5
        @JvmStatic
        fun fromId(id: Int): TemplateComparator = when (id) {
            NAME_ASC_ID -> NameAsc
            NAME_DESC_ID -> NameDesc
            TYPE_ASC_ID -> TypeAsc
            TYPE_DESC_ID -> TypeDesc
            DATE_ASC_ID -> DateAsc
            DATE_DESC_ID -> DateDesc
            else -> NameAsc // fallback
        }
    }
}
