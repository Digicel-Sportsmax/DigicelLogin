package com.cme.digicellogincme.views

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.applicaster.analytics.AnalyticsUtils
import com.applicaster.util.OSUtil
import com.applicaster.util.StringUtil
import com.applicaster.util.ui.APWebView
import com.cme.digicellogincme.helper.AUTH_TOKEN_KEY
import com.cme.digicellogincme.models.DigicelWebResponse
import com.cme.digicellogincme.models.WebAuth
import rx.Observable
import rx.Observer

class DigicelWebView : APWebView {

    var schemeCallback: String? = null;
    lateinit var observer: Observer<DigicelWebResponse>

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun loadUrl(url: String) {
        webViewClient = DigicelWebClient()
        super.loadUrl(url)
    }

    override fun loadUrl(context: Context, url: String, enableZoom: Boolean, exposureTime: Int, callBackScheme: String) {
        schemeCallback = callBackScheme;
        super.loadUrl(context, url, enableZoom, exposureTime, callBackScheme)
    }

    private inner class DigicelWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            var result = false
            if (isCallBackScheme(url)) {
                extractToken(url)
                result = true
            } else if (isKnownScheme(url)) {
                handleSpecialUrl(view.context, url)
                result = true
            }
            return result
        }

        private fun isCallBackScheme(url: String): Boolean {
            var result = false
            if (!StringUtil.isEmpty(url) && !StringUtil.isEmpty(schemeCallback)) {
                if (url.startsWith(schemeCallback!!)) {
                    result = true
                }
            }
            return result
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Log.e(TAG, "$failingUrl $errorCode")
            if (context != null && (context as Activity).findViewById<View>(OSUtil.getResourceId("progress_bar")) != null) {
                (context as Activity).findViewById<View>(OSUtil.getResourceId("progress_bar")).visibility = View.GONE
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            Log.d(TAG, "onPageFinished $url")
            if (context != null && (context as Activity).findViewById<View>(OSUtil.getResourceId("progress_bar")) != null) {
                (context as Activity).findViewById<View>(OSUtil.getResourceId("progress_bar")).visibility = View.GONE
            }
            AnalyticsUtils.sendOpenWebViewAnalytics(url)
            setTimerIfNeeded()
        }
    }

    private fun extractToken(url: String) {
        val uri = Uri.parse(url)
        val token =  uri.getQueryParameter(AUTH_TOKEN_KEY)
        //Currently i assume that if i have no token then i redirected from the mail confirmation phase.
        val digicelWebResponse = if (StringUtil.isEmpty(token)) {
            DigicelWebResponse(token, WebAuth.CREATE_ACCOUNT)
        } else {
            DigicelWebResponse(token, WebAuth.LOGIN)

        }
        Observable.just(digicelWebResponse).subscribe(observer)
    }
}
