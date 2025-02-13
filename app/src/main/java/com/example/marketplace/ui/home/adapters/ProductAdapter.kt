package com.example.marketplace.ui.home.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketplace.databinding.ItemProductBinding
import com.example.marketplace.ui.home.models.Product

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name

            // Отображаем изображение
            Glide.with(binding.productImage.context)
                .load(product.image)
                .into(binding.productImage)

            // Проверяем скидку
            if (product.discount > 0) {
                val discountedPrice = product.price - product.discount
                binding.productDiscountedPrice.text = "$discountedPrice ₽"
                binding.productOriginalPrice.text = "${product.price} ₽"
                binding.productOriginalPrice.visibility = View.VISIBLE
            } else {
                binding.productDiscountedPrice.text = "${product.price} ₽"
                binding.productOriginalPrice.visibility = View.GONE
            }

            // Обработка нажатий на кнопку "В корзину"
            binding.addToCartButton.setOnClickListener {
                Log.d("ProductAdapter", "Товар добавлен в корзину: ${product.name}")
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }


    override fun getItemCount() = productList.size

    fun updateProducts(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
}