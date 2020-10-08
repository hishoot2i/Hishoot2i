package org.illegaller.ratabb.hishoot2i.data.pref

import entity.BadgePosition
import io.reactivex.rxjava3.core.Flowable

interface BadgeToolPref {
    var badgePosition: BadgePosition
    var badgeColor: Int
    var badgeEnable: Boolean
    var badgeSize: Float
    var badgeText: String
    var badgeTypefacePath: String?
    val mainFlow: List<Flowable<*>>
}
