package org.illegaller.ratabb.hishoot2i.data.pref

import io.reactivex.Flowable

interface ScreenToolPref {
    var doubleScreenEnable: Boolean
    val mainFlow: List<Flowable<*>>
}
