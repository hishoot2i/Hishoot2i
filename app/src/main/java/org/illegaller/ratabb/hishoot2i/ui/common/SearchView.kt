package org.illegaller.ratabb.hishoot2i.ui.common

import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun SearchView.queryTextChange(): Flow<String> = callbackFlow {
    val listener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean = false
        override fun onQueryTextChange(newText: String): Boolean {
            if (!isClosedForSend) {
                sendBlocking(newText)
                return true
            }
            return false
        }
    }
    setOnQueryTextListener(listener)
    awaitClose { setOnQueryTextListener(null) }
}
