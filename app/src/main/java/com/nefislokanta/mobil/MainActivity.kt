package com.nefislokanta.mobil

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import android.graphics.Color
import android.view.Gravity
import android.view.KeyEvent
import android.webkit.WebSettings
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private val panelUrl = "https://nefislokanta.com/mobil/"
    private val updateManifestUrl = "https://nefislokanta.com/wp-json/nefis-mobil/v1/app-update"
    private var updateDialogShown = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(21, 91, 59)
        window.navigationBarColor = Color.rgb(21, 91, 59)

        val frame = FrameLayout(this)
        webView = WebView(this)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        progress.max = 100
        progress.setBackgroundColor(Color.TRANSPARENT)
        progress.progressDrawable.setColorFilter(Color.rgb(32, 134, 83), android.graphics.PorterDuff.Mode.SRC_IN)
        val webParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        frame.addView(webView, webParams)
        val progressParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp(3), Gravity.TOP)
        frame.addView(progress, progressParams)
        setContentView(frame)

        val cookies = CookieManager.getInstance()
        cookies.setAcceptCookie(true)
        cookies.setAcceptThirdPartyCookies(webView, false)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.setSupportZoom(false)
        webView.isVerticalScrollBarEnabled = false
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                if (uri.scheme == "https" && uri.host == "nefislokanta.com") return false
                Toast.makeText(this@MainActivity, "Bu bağlantı uygulama dışında açılmalı.", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        if (savedInstanceState == null) webView.loadUrl(panelUrl) else webView.restoreState(savedInstanceState)

        // Version check runs in the background and never blocks the menu panel.
        checkForAppUpdate()
    }

    private fun checkForAppUpdate() {
        Thread {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(updateManifestUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 5000
                    readTimeout = 5000
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("Cache-Control", "no-cache")
                }
                if (connection.responseCode !in 200..299) return@Thread
                val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val json = JSONObject(body)
                val latestCode = json.optInt("versionCode", 0)
                val latestName = json.optString("versionName", "")
                val apkUrl = json.optString("apkUrl", "")
                val changelog = json.optString("changelog", "")
                val currentCode = if (Build.VERSION.SDK_INT >= 28) {
                    packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0).versionCode
                }
                if (latestCode > currentCode && apkUrl.startsWith("https://") && latestName.isNotBlank()) {
                    runOnUiThread { showUpdateDialog(latestName, apkUrl, changelog) }
                }
            } catch (_: Exception) {
                // Offline/server error: continue opening the app normally.
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    private fun showUpdateDialog(versionName: String, apkUrl: String, changelog: String) {
        if (isFinishing || updateDialogShown) return
        updateDialogShown = true
        val message = buildString {
            append("Yeni NefisMobil sürümü (")
            append(versionName)
            append(") hazır.")
            if (changelog.isNotBlank()) {
                append("\n\nYenilikler:\n")
                append(changelog)
            }
            append("\n\nGüncelle'ye dokunarak APK indirme sayfasını açın. Android kurulumu onaylamanızı isteyebilir.")
        }
        AlertDialog.Builder(this)
            .setTitle("Uygulama güncellemesi")
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("Güncelle") { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)))
                } catch (_: Exception) {
                    Toast.makeText(this, "APK bağlantısı açılamadı. Nefis Mobil Menü ayarlarını kontrol edin.", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Şimdi değil", null)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && ::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        if (::webView.isInitialized) webView.destroy()
        super.onDestroy()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
