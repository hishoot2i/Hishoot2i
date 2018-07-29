package rbb.hishoot2i.common.imageloader.uil

import android.content.Context
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import rbb.hishoot2i.common.PathBuilder
import rbb.hishoot2i.common.ext.resourcesFrom
import java.io.InputStream

class TemplateImageDownloader(context: Context) : BaseImageDownloader(context) {
    override fun getStreamFromOtherSource(imageUri: String, extra: Any?): InputStream {
        if (imageUri.startsWith(PathBuilder.TEMPLATE_APP)) {
            try {
                val loc = imageUri.replace(PathBuilder.TEMPLATE_APP, "")
                    .split(PathBuilder.SEPARATOR)
                val resources = context.resourcesFrom(loc[0])
                val drawableRes = resources.getIdentifier(loc[1], "drawable", loc[0])
                if (drawableRes > 0) {
                    return resources.openRawResource(drawableRes)
                } else throw UnsupportedOperationException(imageUri)
            } catch (e: Exception) {
                throw UnsupportedOperationException(imageUri, e)
            }
        } else throw UnsupportedOperationException(imageUri)
    }
}