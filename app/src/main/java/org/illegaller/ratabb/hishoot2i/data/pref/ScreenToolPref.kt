package org.illegaller.ratabb.hishoot2i.data.pref

import io.reactivex.rxjava3.core.Flowable

interface ScreenToolPref {
    var doubleScreenEnable: Boolean
    val mainFlow: List<Flowable<*>>
}
