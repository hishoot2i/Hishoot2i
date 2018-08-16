@file:Suppress("NOTHING_TO_INLINE")

package common.ext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

@JvmOverloads
@RequiresApi(Build.VERSION_CODES.O)
inline fun Context.prepareNotificationChannel(
    id: String,
    name: String,
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT
) {
    getSystemService(NotificationManager::class.java)?.also {
        if (null == it.getNotificationChannel(id)) {
            it.createNotificationChannel(NotificationChannel(id, name, importance))
        }
    }
}