package com.example.marketplace.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marketplace.ui.home.models.Product

class HomeViewModel : ViewModel() {

    // LiveData для хранения списка товаров
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    // Функция для обновления списка товаров
    fun updateProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }
}