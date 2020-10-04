package imageloader.uil

import android.content.Context
import com.nostra13.universalimageloader.core.download.BaseImageDownloader
import common.PathBuilder.SEPARATOR
import common.PathBuilder.TEMPLATE_APP
import common.ext.openRawResource
import common.ext.resourcesFrom
import java.io.InputStream

class TemplateImageDownloader(context: Context) : BaseImageDownloader(context) {
    override fun getStreamFromOtherSource(imageUri: String, extra: Any?): InputStream = when {
        imageUri.startsWith(TEMPLATE_APP) -> {
            try {
                val (pkg, id) = imageUri.removePrefix(TEMPLATE_APP).split(SEPARATOR, limit = 2)
                context.resourcesFrom(pkg).openRawResource(id, "drawable", pkg)
                    ?: throw UnsupportedOperationException(imageUri)
            } catch (e: Exception) {
                throw UnsupportedOperationException(imageUri, e)
            }
        }
        else -> throw UnsupportedOperationException(imageUri)
    }
}
