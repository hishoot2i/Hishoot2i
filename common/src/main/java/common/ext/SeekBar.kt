package common.ext

import android.widget.SeekBar

@JvmOverloads
inline fun onSeekBarChange(
    crossinline onProgress: (seekBar: SeekBar, progress: Int, fromUser: Boolean) -> Unit =
        { _: SeekBar, _: Int, _: Boolean -> },
    crossinline onStartTrack: (seekBar: SeekBar) -> Unit = {},
    crossinline onStopTrack: (seekBar: SeekBar) -> Unit = {}
): SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean): Unit =
        onProgress(seekBar, progress, fromUser)

    override fun onStartTrackingTouch(seekBar: SeekBar): Unit = onStartTrack(seekBar)
    override fun onStopTrackingTouch(seekBar: SeekBar): Unit = onStopTrack(seekBar)
}