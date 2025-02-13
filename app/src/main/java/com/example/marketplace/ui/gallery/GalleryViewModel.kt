package com.example.marketplace.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "пусто:(\nпотом будет:)"
    }
    val text: LiveData<String> = _text
}