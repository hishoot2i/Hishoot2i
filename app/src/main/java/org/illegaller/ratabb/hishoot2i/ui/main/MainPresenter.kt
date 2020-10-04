package org.illegaller.ratabb.hishoot2i.ui.main

import androidx.annotation.ColorInt
import org.illegaller.ratabb.hishoot2i.ui.common.Mvp.Presenter

interface MainPresenter : Presenter<MainView> {
    fun resume()
    fun render()
    fun save()
    fun backgroundColorPipette(@ColorInt color: Int)
    fun changeScreen1(path: String)
    fun changeScreen2(path: String)
    fun changeBackground(path: String)
    val sourcePath: entity.ImageSourcePath
}
