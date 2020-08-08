package common.ext

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView

@JvmOverloads
inline fun <T : Adapter> AdapterView<T>.setOnItemSelected(
    crossinline nothingSelected: (parent: AdapterView<*>?) -> Unit = { _ -> },
    crossinline itemSelected: (
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) -> Unit = { _: AdapterView<*>?, _: View?, _: Int, _: Long -> }
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?): Unit = nothingSelected(parent)
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ): Unit = itemSelected(parent, view, position, id)
    }
}
