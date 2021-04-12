package common.widget

import android.view.View
import android.widget.AdapterView

inline fun AdapterView<*>.setOnItemSelected(
    crossinline itemSelected: (AdapterView<*>, View, Int, Long) -> Unit
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>): Unit = Unit
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            itemSelected(parent, view, position, id)
        }
    }
}
