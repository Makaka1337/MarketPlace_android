package com.example.marketplace

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "UserPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_PHONE = "user_phone"
        const val KEY_USER_NAME = "user_name"
    }

    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginEditText = findViewById(R.id.loginTextPrinter)
        passwordEditText = findViewById(R.id.paswrdTextPrinter)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        
        
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Проверяем, входил ли пользователь раньше
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            openMainScreen()
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }


        loginButton.setOnClickListener {
            val login = loginEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                runOnUiThread {
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content), // Корневой View
                        "Введите логин и пароль!", // Текст сообщения
                        Snackbar.LENGTH_SHORT // Длительность показа
                    )

                    // Устанавливаем положение сверху
                    val view = snackbar.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params

                    // Опционально: меняем цвет фона и текста
                    view.setBackgroundColor(Color.RED) // Красный фон
                    snackbar.setTextColor(Color.WHITE) // Белый текст

                    snackbar.show()
                }
            } else {
                sendLoginRequest(login, password)
            }
        }
    }

    private fun sendLoginRequest(login: String, password: String) {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:56500/login/response"

        val json = JSONObject()
        json.put("login", login)
        json.put("password", password)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    runOnUiThread {
                        val snackbar = Snackbar.make(
                            findViewById(android.R.id.content), // Корневой View
                            "Ошибка соединения", // Текст сообщения
                            Snackbar.LENGTH_SHORT // Длительность показа
                        )

                        // Устанавливаем положение сверху
                        val view = snackbar.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params

                        // Опционально: меняем цвет фона и текста
                        view.setBackgroundColor(Color.RED) // Красный фон
                        snackbar.setTextColor(Color.WHITE) // Белый текст

                        snackbar.show()
                    }
                    Log.e("LoginActivity", "Error:${e}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.e("LoginActivity", "Body: $responseBody") // Логируем весь ответ для отладки

                if (response.isSuccessful && responseBody != null) {
                    try {
                        val jsonResponse = JSONObject(responseBody)

                        // Проверяем наличие ключа "response_server"
                        if (jsonResponse.has("response_server")) {
                            val responseServerArray = jsonResponse.getJSONArray("response_server")

                            // Проверяем, что массив не пустой
                            if (responseServerArray.length() > 0) {
                                val userObject = responseServerArray.getJSONObject(0)

                                // Проверяем наличие данных пользователя
                                if (userObject.has("mail") && userObject.has("user_phoneNumber") && userObject.has("user_real_name")) {
                                    // Получаем данные пользователя
                                    val userEmail = userObject.getString("mail")
                                    val userPhoneNumber = userObject.getString("user_phoneNumber")
                                    val userRealName = userObject.getString("user_real_name")

                                    // Сохраняем данные в SharedPreferences
                                    sharedPreferences.edit().apply {
                                        putBoolean(KEY_IS_LOGGED_IN, true)
                                        putString(KEY_USER_EMAIL, userEmail)
                                        putString(KEY_USER_PHONE, userPhoneNumber)
                                        putString(KEY_USER_NAME, userRealName)
                                        apply()
                                    }

                                    // Успешный логин
                                    runOnUiThread {
                                        openMainScreen()
                                    }
                                } else {
                                    // Данные пользователя отсутствуют
                                    showError("Ошибка: некорректные данные пользователя")
                                }
                            } else {
                                // Массив "response_server" пустой
                                showError("Ошибка: пустой ответ сервера")
                            }
                        } else {
                            // Ключ "response_server" отсутствует
                            showError("Ошибка: некорректный ответ сервера")
                        }
                    } catch (e: JSONException) {
                        // Обработка ошибок парсинга JSON
                        Log.e("LoginActivity", "JSON parsing error: ${e.message}")
                        showError("Ошибка: некорректный ответ сервера")
                    }
                } else if (response.code == 400) {
                    // Обработка неуспешного HTTP-запроса
                    showError("НЕПРАВИЛЬНЫЕ ДАННЫЕ ДЛЯ ВХОДА!")
                } else {
                    showError("Ошибка: некорректный ответ сервера")
                }
            }
        })
    }

    private fun openMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showError(message: String) {
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
            view.setBackgroundColor(Color.RED) // Красный фон
            snackbar.setTextColor(Color.WHITE) // Белый текст
            snackbar.show()
        }
    }
}
