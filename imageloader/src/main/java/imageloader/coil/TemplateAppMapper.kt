package imageloader.coil

import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import coil.map.Mapper
import common.PathBuilder
import common.PathBuilder.SEPARATOR
import common.PathBuilder.TEMPLATE_APP

/**
 * based on [coil.map.ResourceUriMapper]
 *
 * @see [PathBuilder.stringTemplateApp]
 **/
internal class TemplateAppMapper(
    private val context: Context
) : Mapper<String, Uri> {

    override fun handles(data: String): Boolean = data.startsWith(TEMPLATE_APP)

    override fun map(data: String): Uri {
        val (templateId, drawableName) = data.removePrefix(TEMPLATE_APP)
            .split(SEPARATOR, limit = 2)
        val id = context.packageManager.getResourcesForApplication(templateId)
            .getIdentifier(drawableName, "drawable", templateId)
        check(id != 0) { "Invalid $TEMPLATE_APP: $data" }
        return "$SCHEME_ANDROID_RESOURCE://$templateId/$id".toUri()
    }
}
