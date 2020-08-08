package common

import android.net.Uri
import java.io.File

interface FileConstants {
    /**
     * File directory Hishoot at external Storage,<br></br>
     * create new file if we don't have it
     *
     * @return a [File] directory <storage>/HiShoot
     **/
    fun savedDir(): File

    /**
     * File directory Hishoot Htz
     *
     * @return a [File] directory `/data/data/<packageName>/files/htz`
     **/
    fun htzDir(): File

    /**
     * File temp background crop
     *
     * @return a [File] `/data/data/<packageName>/cache/.crop`
     **/
    fun bgCrop(): File

    /* */
    fun File.toUri(): Uri

    companion object {
        const val NO_MEDIA = ".nomedia"
        const val BG_CROP = ".crop"
    }
}
