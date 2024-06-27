package com.example.testcasino

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.testcasino.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Настройка WebViewClient для обработки переходов внутри WebView
        webView.webViewClient = WebViewClient()

        // Настройка WebChromeClient для поддержки полноэкранного режима
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                // Убедитесь, что ваша реализация здесь соответствует вашим требованиям
                // Это вызывается, когда WebView запрашивает переход в полноэкранный режим
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                // Здесь можно выполнить необходимые действия при выходе из полноэкранного режима
            }
        }

        // Загрузка URL игры
        webView.loadUrl("http://akademija-mediciny.ru/htmlgames/6151794/")
    }

    // Опционально: добавьте методы жизненного цикла, если необходимо
}
