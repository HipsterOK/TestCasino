package com.example.testcasino

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var sharPref: SharedPreferences
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val fileChooserRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        sharPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        webView = findViewById(R.id.webvv)

        val data = intent.getStringExtra("link")
        val savedUrl = sharPref.getString("wuehfwiuef", null)
        if (savedUrl != null) {
            openWWW(savedUrl)
            Log.d("ReceivedData", "Loaded saved URL: $savedUrl")
        } else {
            data?.let {
                openWWW(it)
                Log.d("ReceivedData", "Loaded intent URL: $data")
            }
        }
    }

    private fun openWWW(url: String) {
        Log.d("WebActivity", "Opening URL: $url")

        webView.settings.apply {
            javaScriptEnabled = true
            displayZoomControls = false
            builtInZoomControls = true
            setSupportZoom(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
            loadWithOverviewMode = true
            useWideViewPort = true
            domStorageEnabled = true
            allowContentAccess = true
            databaseEnabled = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            userAgentString = userAgentString.replace("; wv", "").replace(" Version/4.0", "")
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                saveLastUrl(url)
            }
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@WebActivity.filePathCallback?.onReceiveValue(null)
                this@WebActivity.filePathCallback = filePathCallback
                val intent = fileChooserParams?.createIntent()
                try {
                    if (intent != null) {
                        startActivityForResult(intent, fileChooserRequestCode)
                    }
                } catch (e: Exception) {
                    this@WebActivity.filePathCallback = null
                    return false
                }
                return true
            }
        }

        webView.loadUrl(url)
    }

    private fun saveLastUrl(url: String?) {
        val isFirstOpening = sharPref.getString("wuehfwiuef", null) == null
        if (url != null && isFirstOpening) {
            sharPref.edit().putString("wuehfwiuef", url).apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileChooserRequestCode) {
            filePathCallback?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode, data
                )
            )
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val data = intent.getStringExtra("link")
        data?.let {
            webView.loadUrl(it)
        }
    }
}