package common.ext

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

fun <T> RecyclerView.Adapter<*>.asyncListDiffer(
    diffCallback: DiffUtil.ItemCallback<T>
): AsyncListDiffer<T> = AsyncListDiffer<T>(this, diffCallback)
