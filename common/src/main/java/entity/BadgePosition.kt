package entity

import entity.Sizes.Companion.ZERO

enum class BadgePosition {
    LEFT_TOP {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { _, _, padding -> Sizes(padding) }
    },
    CENTER_TOP {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) / 2).copy(y = padding)
        }
    },
    RIGHT_TOP {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) - padding).copy(y = padding)
        }
    },

    LEFT_MIDDLE {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) / 2).copy(x = padding)
        }
    },
    CENTER_MIDDLE {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, _ ->
            (total - source) / 2
        }
    },
    RIGHT_MIDDLE {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) / 2).copy(x = ((total - source) - padding).x)
        }
    },

    LEFT_BOTTOM {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) - padding).copy(x = padding)
        }
    },
    CENTER_BOTTOM {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            ((total - source) / 2).copy(y = ((total - source) - padding).y)
        }
    },
    RIGHT_BOTTOM {
        override val impl: (Sizes, Sizes, Int) -> Sizes = { total, source, padding ->
            (total - source) - padding
        }
    };

    protected abstract val impl: (Sizes, Sizes, Int) -> Sizes

    fun getValue(total: Sizes, source: Sizes, padding: Int): SizesF {
        require(total > ZERO && source > ZERO && padding > 0) {
            "padding, total and source must be positive," +
                "\npadding:$padding total:$total source:$source"
        }
        return impl(total, source, padding).toSizeF()
    }
}
