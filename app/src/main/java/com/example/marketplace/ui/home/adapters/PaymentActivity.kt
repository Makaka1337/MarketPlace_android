package com.example.marketplace.ui.home.adapters

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.marketplace.MainActivity
import com.example.marketplace.R
import com.google.android.material.snackbar.Snackbar

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Получаем переданные данные
        val productName = intent.getStringExtra("productName") ?: "Товар"
        val productPrice = intent.getStringExtra("productPrice") ?: "0"
        val productImage = intent.getStringExtra("productImage")

        // Находим элементы
        val productImageView = findViewById<ImageView>(R.id.paymentProductImage)
        val productNameView = findViewById<TextView>(R.id.paymentProductName)
        val productPriceView = findViewById<TextView>(R.id.paymentProductPrice)
        val cardNumberField = findViewById<EditText>(R.id.cardNumber)
        val cardExpiryField = findViewById<EditText>(R.id.cardExpiry)
        val cardCVVField = findViewById<EditText>(R.id.cardCVV)
        val payButton = findViewById<Button>(R.id.payButton)
        val backButton = findViewById<Button>(R.id.backToMainButton)

        // Устанавливаем данные
        productNameView.text = productName
        productPriceView.text = "$productPrice ₽"
        productImage?.let {
            Glide.with(this).load(it).into(productImageView)
        }

        // Обработчик нажатия кнопки "Оплатить"
        payButton.setOnClickListener {
            val cardNumber = cardNumberField.text.toString()
            val cardExpiry = cardExpiryField.text.toString()
            val cardCVV = cardCVVField.text.toString()

            if (cardNumber.length == 16 && cardCVV.length == 3) {
                Handler(Looper.getMainLooper()).postDelayed({
                    showInfo("Оплата прошла успешно!", true)
                }, 1000)

                //Toast.makeText(this, "Оплата прошла успешно!", Toast.LENGTH_SHORT).show()

                // Задержка перед переходом
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }, 3000) // 4000 миллисекунд = 4 секунды
            } else {
                //Toast.makeText(this, "Ошибка ввода данных!", Toast.LENGTH_SHORT).show()
                showInfo("Ошибка ввода данных!", false)
            }
        }
        // Кнопка "Назад" — возвращаемся в MainActivity
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Закрывает текущие активити, если MainActivity уже запущен
            startActivity(intent)
            finish()
        }
    }
    private fun showInfo(message: String, status: Boolean) {
        runOnUiThread {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
            )
            val view = snackbar.view
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            if (!status){
                view.setBackgroundColor(Color.RED) // Красный фон
            } else {
                view.setBackgroundColor(Color.GREEN) // ЗЕЛЁНЫЙ фон
            }
            snackbar.setTextColor(Color.WHITE) // Белый текст
            snackbar.show()
        }
    }
}
