package template

import template.Template.Default
import template.Template.Version1
import template.Template.Version2
import template.Template.Version3
import template.Template.VersionHtz
import java.text.Collator

private val Template.typeSort: Byte
    get() = when (this) {
        is Default -> 0x0
        is Version1 -> 0x1
        is Version2 -> 0x2
        is Version3 -> 0x3
        is VersionHtz -> 0x4
    }

enum class TemplateComparator : Comparator<Template> {
    NAME_ASC, NAME_DESC, TYPE_ASC, TYPE_DESC, DATE_ASC, DATE_DESC; //

    companion object {
        private val collator = Collator.getInstance()
    }

    override fun compare(lhs: Template, rhs: Template): Int =
        if (lhs is Default || rhs is Default) 1 // Template Default always on top
        else when (this) {
            NAME_ASC -> compareValuesBy(lhs, rhs, collator, Template::name)
                .takeIf { it != 0 } ?: compareValuesBy(lhs, rhs, collator, Template::author)
            NAME_DESC -> compareValuesBy(rhs, lhs, collator, Template::name)
                .takeIf { it != 0 } ?: compareValuesBy(lhs, rhs, collator, Template::author)
            TYPE_ASC -> compareValuesBy(lhs, rhs, Template::typeSort)
                .takeIf { it != 0 } ?: NAME_ASC.compare(lhs, rhs)
            TYPE_DESC -> compareValuesBy(rhs, lhs, Template::typeSort)
                .takeIf { it != 0 } ?: NAME_ASC.compare(lhs, rhs)
            DATE_ASC -> compareValuesBy(lhs, rhs, Template::installedDate)
                .takeIf { it != 0 } ?: NAME_ASC.compare(lhs, rhs)
            DATE_DESC -> compareValuesBy(rhs, lhs, Template::installedDate)
                .takeIf { it != 0 } ?: NAME_ASC.compare(lhs, rhs)
        }
}
