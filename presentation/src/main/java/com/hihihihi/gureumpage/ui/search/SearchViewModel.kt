package com.hihihihi.gureumpage.ui.search

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SearchViewModel @Inject constructor(
): ViewModel() {

    fun search(query: String) {
        println(query+"@@@")
    }
}
