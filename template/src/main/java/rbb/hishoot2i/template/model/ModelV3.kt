package rbb.hishoot2i.template.model

import rbb.hishoot2i.common.entity.Glare
import rbb.hishoot2i.common.entity.Sizes

/*
// loc: assets/template.cfg
   {
     "author"       : "template-author-name",
     "name"         : "template-name",
     "desc"         : "template-description",

     "coordinate"   : [
       9999, 9999,
       9999, 9999,
       9999, 9999,
       9999, 9999
     ],

     "template_size": {
       "width"      : 9999,
       "height"     : 9999
     },

     "preview"      : "preview-filename-without-extension",

     "frame"        : "frame-filename-without-extension",

     "shadow"       : "shadow-filename-without-extension",

     "glares"       : [
       {
         "name"     : "glare-1-filename-without-extension",
         "size"     : {
           "width"  : 9999,
           "height" : 9999
         },
         "position" : {
           "x"      : 9999,
           "y"      : 9999
         }
       },
       {
         "name"     : "glare-2-filename-without-extension",
         "size"     : {
           "width"  : 9999,
           "height" : 9999
         },
         "position" : {
           "x"      : 9999,
           "y"      : 9999
         }
       },
       {
         "name"     : "glare-N-filename-without-extension",
         "size"     : {
           "width"  : 9999,
           "height" : 9999
         },
         "position" : {
           "x"      : 9999,
           "y"      : 9999
         }
       }
     ]

   }
//
*/
data class ModelV3 @JvmOverloads constructor(
    var name: String = "",
    var author: String = "",
    var desc: String? = null,
    var frame: String = "",
    var preview: String? = null,
    var shadow: String? = null,
    var coordinate: List<Float> = emptyList(),
    var size: Sizes = Sizes.ZERO,
    var glares: List<Glare>? = null
) {
    fun isNotValid(): Boolean = when {
        name == "" -> true
        author == "" -> true
        frame == "" -> true
        coordinate.isEmpty() -> true
        size == Sizes.ZERO -> true
        else -> false
    }
}