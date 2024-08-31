package com.example.cookingassistance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {
    var search = MutableLiveData<String>()

    init {
        search.value = ""
    }

    fun setSearch(input: String) {
        search.value = input
    }
}