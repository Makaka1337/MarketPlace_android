package com.example.marketplace

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.marketplace.LoginActivity
import com.example.marketplace.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var realNameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        emailEditText = findViewById(R.id.emailEditText)
        realNameEditText = findViewById(R.id.realNameEditText)
        addressEditText = findViewById(R.id.addressEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        backToLoginButton = findViewById(R.id.backToLoginButton)

        registerButton.setOnClickListener {
            registerUser()
        }
        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registerUser() {
        val phone = phoneNumberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val realName = realNameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (phone.isEmpty() || email.isEmpty() || realName.isEmpty() || address.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val json = JSONObject().apply {
            put("user_phoneNumber", phone)
            put("mail", email)
            put("user_real_name", realName)
            put("address", address)
            put("password", password)
        }

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url("http://10.0.2.2:56500/register/response") // Укажи свой URL сервера
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Ошибка подключения", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "{}")
                val message = jsonResponse.getJSONArray("response_server").getJSONObject(0).getString("message")

                runOnUiThread {

                    when (message) {
                        "error_user_phone_exist" -> Toast.makeText(this@RegisterActivity, "Номер уже зарегистрирован!", Toast.LENGTH_SHORT).show()
                        "error_user_mail_exists" -> Toast.makeText(this@RegisterActivity, "Почта уже зарегистрирована!", Toast.LENGTH_SHORT).show()
                        "good" -> {
                            Toast.makeText(this@RegisterActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                        else -> Toast.makeText(this@RegisterActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
