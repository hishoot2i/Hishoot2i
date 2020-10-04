package org.illegaller.ratabb.hishoot2i.ui.common

import com.google.android.material.slider.Slider

inline fun Slider.doOnStopTouch(
    crossinline stopTouch: (Slider) -> Unit
) {
    addOnSliderTouchListeners(onStopTouch = stopTouch)
}

@JvmOverloads
inline fun Slider.addOnSliderTouchListeners(
    crossinline onStarTouch: (Slider) -> Unit = { _ -> },
    crossinline onStopTouch: (Slider) -> Unit = { _ -> }
) {
    addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
        override fun onStartTrackingTouch(slider: Slider): Unit = onStarTouch(slider)
        override fun onStopTrackingTouch(slider: Slider): Unit = onStopTouch(slider)
    })
}
