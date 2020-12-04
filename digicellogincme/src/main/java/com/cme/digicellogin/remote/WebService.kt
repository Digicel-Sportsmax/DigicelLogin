package com.cme.digicellogin.remote

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.applicaster.util.StringUtil
import com.cme.digicellogin.helper.DIGICEL_BASE_URL
import com.cme.digicellogin.helper.PluginConfigurationHelper
import java.util.*

typealias Params = HashMap<String, String>

class WebService {

    enum class ApiRequest(val endpoint: String) {
        GetAccess("oauth2/token"),
        Login("account"),
        GetSubscriptions("me/provisioning/subscriptions"),
        GetSubscriberType("me/profile/subscribertype"),

    }

    enum class Status {
        Unknown,
        Success,
        WrongCredentials,
        NotFound,
        InvalidParameters,
        InternalError
    }

    private fun getBaseUrl(): String {
        var baseUrl = "https://digicelid.digicelgroup.com/selfcarev2"

        if (StringUtil.isNotEmpty(PluginConfigurationHelper.getConfigurationValue(DIGICEL_BASE_URL))) {
            baseUrl = PluginConfigurationHelper.getConfigurationValue(DIGICEL_BASE_URL) as String
        }

        return baseUrl
    }

    fun performApiRequest(apiRequest: ApiRequest, params: Params?, context: Context, callback: (Status, String?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        val url = getBaseUrl() + "/${apiRequest.endpoint}"


        val sr = object : StringRequest(Method.POST, url, Response.Listener { response ->
            callback(getStatusFromCode(200), response)
        }, Response.ErrorListener { error ->
            callback(getStatusFromCode(error.networkResponse.statusCode), String(error.networkResponse.data))
        }) {
            override fun getParams(): Params {
                return params ?: Params()
            }

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }


        }
        queue.add(sr)
    }

    fun performApiWithAccessRequest(apiRequest: ApiRequest, params: Params?, extraHeaders: Params?, token: String, context: Context, callback: (Status, String?) -> Unit) {
        val queue = Volley.newRequestQueue(context)
        var url = getBaseUrl() + "/${apiRequest.endpoint}"

        if (params != null) {
            url += "?"
            for (key in params.keys) {
                url += key + "=" + params[key] + "&"
            }
        }

        val sr = object : StringRequest(Method.GET, url, Response.Listener { response ->
            callback(getStatusFromCode(200), response)
        }, Response.ErrorListener { error ->
            callback(getStatusFromCode(error.networkResponse.statusCode), String(error.networkResponse.data))
        }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val finalHeaders = extraHeaders ?: Params()
                finalHeaders["Cache-Control"] = "no-cache"
                finalHeaders["Host"] = "digicelid.digicelgroup.com"
                finalHeaders["Authorization"] = "Bearer $token"
                finalHeaders["Lang"] = Locale.getDefault().language
                return finalHeaders

            }
        }
        queue.add(sr)
    }

    fun getStatusFromCode(code: Int): Status {

        return when (code) {
            200 -> Status.Success
            400 -> Status.InvalidParameters
            401 -> Status.WrongCredentials
            404 -> Status.NotFound
            500 -> Status.InternalError
            else -> Status.Unknown
        }
    }
}