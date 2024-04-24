package com.idsoft.textreader

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.teipreader.Main.Config_dirs
import java.util.Objects

class main_web_view : ComponentActivity(){
    lateinit var wv : WebView;
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_web_view)
        wv = findViewById(R.id.main_web)
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                // 对于HTTP、HTTPS、FILE等协议，直接在WebView中加载
                return !(request?.url?.toString()?.startsWith("http") == true ||
                        request?.url.toString().startsWith("file"))

                // 对于其他协议，可以选择使用Intent在外部浏览器中打开
                // 但这里我们返回true以阻止打开外部浏览器
            }

            // 兼容旧版本的shouldOverrideUrlLoading方法
            @Deprecated("Deprecated in Java",
                ReplaceWith("!(url?.startsWith(\"http\") == true || url?.startsWith(\"file\") == true)")
            )
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 对于HTTP、HTTPS、FILE等协议，直接在WebView中加载
                return !(url?.startsWith("http") == true || url?.startsWith("file") == true)

                // 对于其他协议，可以选择使用Intent在外部浏览器中打开
                // 但这里我们返回true以阻止打开外部浏览器
            }
        }
        wv.settings.javaScriptEnabled = true
        wv.loadUrl("http://localhost:${Config_dirs.NormPort}/app")
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.getAction() === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (wv.canGoBack()) {
                        val currentUrl: String? = wv.url
                        if (currentUrl == "http://localhost:${Config_dirs.NormPort}/app") {
                            val rootView = findViewById<View>(android.R.id.content)
                            Snackbar.make(rootView, "已退出 WebView", Snackbar.LENGTH_SHORT).setAction("") {

                            }.show()
                            finish() // 退出Activity
                        }else{
                            wv.goBack()
                        }

                    } else {
                        finish() // 退出Activity
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}