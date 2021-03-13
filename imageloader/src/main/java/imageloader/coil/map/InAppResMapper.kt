package imageloader.coil.map

import androidx.annotation.DrawableRes
import coil.map.Mapper
import common.PathBuilder
import common.PathBuilder.DRAWABLES

/**
 *
 * @see [PathBuilder.stringDrawables]
 * @see [coil.map.ResourceIntMapper]
 **/
internal object InAppResMapper : Mapper<String, @DrawableRes Int> {
    override fun handles(data: String): Boolean = data.startsWith(DRAWABLES)

    override fun map(data: String): Int = data.removePrefix(DRAWABLES).toInt()
}
