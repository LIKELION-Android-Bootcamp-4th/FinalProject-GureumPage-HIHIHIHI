package com.hihihihi.data.remote.datasourceimpl

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration

object FirestoreListenerManager {
    private val listeners = mutableListOf<ListenerRegistration>()

    fun add(listener: ListenerRegistration) {
        listeners.add(listener)
    }

    fun clearAll() {
        listeners.forEach {
            it.remove()
        }
        listeners.clear()
    }
}