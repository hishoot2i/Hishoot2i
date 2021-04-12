package common.content

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

inline fun lazyBroadcastReceiver(
    crossinline receive: (Context?, Intent?) -> Unit
): Lazy<BroadcastReceiver> = lazy {
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            receive(context, intent)
        }
    }
}
