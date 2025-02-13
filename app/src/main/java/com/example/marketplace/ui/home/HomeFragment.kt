package com.example.marketplace.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketplace.databinding.FragmentHomeBinding
import com.example.marketplace.ui.home.adapters.ProductAdapter
import com.example.marketplace.ui.home.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter
    private val client = OkHttpClient() // Инициализируем OkHttpClient

    // Используем ViewModel
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Инициализация адаптера
        productAdapter = ProductAdapter(emptyList())
        binding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // Наблюдаем за изменениями в списке товаров
        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
        }

        // Загружаем товары
        loadProducts()

        return root
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            val products = fetchProductsFromApi()
            homeViewModel.updateProducts(products) // Обновляем данные в ViewModel
        }
    }

    private suspend fun fetchProductsFromApi(): List<Product> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://10.0.2.2:56500/get_all_product/response")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        // Парсим JSON
                        val jsonObject = JSONObject(responseBody)
                        val productsArray = jsonObject.getJSONArray("response_server")

                        val productList = mutableListOf<Product>()
                        for (i in 0 until productsArray.length()) {
                            val item = productsArray.getJSONArray(i)
                            val product = Product(
                                id = item.getInt(0), // Индекс 0 — это id
                                code = item.getString(1), // Индекс 1 — это code
                                name = item.getString(2), // Индекс 2 — это name
                                image = item.getString(3), // Индекс 3 — это image
                                price = item.getInt(4), // Индекс 4 — это price
                                discount = item.getInt(5) // Индекс 5 — это discount
                            )
                            productList.add(product)
                        }

                        productList // Возвращаем список продуктов
                    } else {
                        emptyList() // Если ответ пустой
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.code}")
                    emptyList() // Если запрос неуспешный
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching products: ${e.message}")
                emptyList() // В случае ошибки
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}