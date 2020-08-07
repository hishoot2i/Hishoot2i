package template

import java.text.Collator

sealed class TemplateComparator(
    val id: Int,
    protected val actualCompare: (Template, Template) -> Int
) : Comparator<Template> {
    override fun compare(lhs: Template, rhs: Template): Int =
        if (lhs.isDefault || rhs.isDefault) 1 else actualCompare(lhs, rhs)

    object NameAsc : TemplateComparator(
        NAME_ASC_ID,
        { lhs: Template, rhs: Template ->
            collator.compare(lhs.name, rhs.name)
        }
    )

    object NameDesc : TemplateComparator(
        NAME_DESC_ID,
        { lhs: Template, rhs: Template ->
            collator.compare(rhs.name, lhs.name)
        }
    )

    object TypeAsc : TemplateComparator(
        TYPE_ASC_ID,
        { lhs: Template, rhs: Template ->
            lhs.indexTypeSort.compareTo(rhs.indexTypeSort).let {
                if (it == 0) NameAsc.actualCompare(lhs, rhs) else it
            }
        }
    )

    object TypeDesc : TemplateComparator(
        TYPE_DESC_ID,
        { lhs: Template, rhs: Template ->
            rhs.indexTypeSort.compareTo(lhs.indexTypeSort).let {
                if (it == 0) NameAsc.actualCompare(lhs, rhs) else it
            }
        }
    )

    object DateAsc : TemplateComparator(
        DATE_ASC_ID,
        { lhs: Template, rhs: Template ->
            lhs.installedDate.compareTo(rhs.installedDate).let {
                if (it == 0) NameAsc.actualCompare(lhs, rhs) else it
            }
        }
    )

    object DateDesc : TemplateComparator(
        DATE_DESC_ID,
        { lhs: Template, rhs: Template ->
            rhs.installedDate.compareTo(lhs.installedDate).let {
                if (it == 0) NameAsc.actualCompare(lhs, rhs) else it
            }
        }
    )

    companion object {
        @JvmStatic
        private val collator: Collator = Collator.getInstance()
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
