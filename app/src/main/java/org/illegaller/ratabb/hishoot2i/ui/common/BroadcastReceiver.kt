package org.illegaller.ratabb.hishoot2i.ui.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

inline fun broadcastReceiver(
    crossinline receive: (Context?, Intent?) -> Unit
): Lazy<BroadcastReceiver> = lazy {
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receive(context, intent)
        }
    }
}
