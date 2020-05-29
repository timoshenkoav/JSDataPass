package com.scopic.jsdatapass

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        setContentView(R.layout.activity_main)
        val url = "https://fitnice-5c550.web.app"
        open.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        webview.settings.apply {
            javaScriptEnabled = true
        }

        webview.clearCache(true)
        webview.addJavascriptInterface(JSBridge(this), "Android")
        webview.loadUrl(url)

        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            intent.extras?.let { data ->

                AlertDialog.Builder(this)
                    .setTitle("Branch")
                    .setMessage(data.keySet().joinToString("\n") { "$it - ${data.get(it).toString()}" })
                    .show()
            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {

                    AlertDialog.Builder(this)
                        .setTitle("Firebase")
                        .setMessage(pendingDynamicLinkData.link?.toString() ?: "")
                        .show()
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e ->

            }
    }

    class JSBridge(val ctx: Context) {
        @JavascriptInterface
        fun sendData(data: String) {
            AlertDialog.Builder(ctx)
                .setTitle("JS")
                .setMessage(data)
                .show()
        }
    }
}
