package org.illegaller.ratabb.hishoot2i.data.pref

import kotlinx.coroutines.flow.Flow

interface ScreenToolPref {
    var doubleScreenEnable: Boolean
    val mainFlow: List<Flow<*>>
}
