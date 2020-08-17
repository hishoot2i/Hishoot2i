package common.ext

import androidx.recyclerview.widget.DiffUtil

/*
fun <T> RecyclerView.Adapter<*>.asyncListDiffer(
    diffCallback: DiffUtil.ItemCallback<T>
): AsyncListDiffer<T> = AsyncListDiffer<T>(this, diffCallback)
*/
@JvmOverloads
inline fun <T> itemDiffCallback(
    crossinline payload: (T, T) -> Any? = { _, _ -> null },
    crossinline itemSame: (T, T) -> Boolean,
    crossinline contentSame: (T, T) -> Boolean
): Lazy<DiffUtil.ItemCallback<T>> = lazy {
    object : DiffUtil.ItemCallback<T>() {
        override fun getChangePayload(oldItem: T, newItem: T): Any? =
            payload(oldItem, newItem)

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            itemSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            contentSame(oldItem, newItem)
    }
}

