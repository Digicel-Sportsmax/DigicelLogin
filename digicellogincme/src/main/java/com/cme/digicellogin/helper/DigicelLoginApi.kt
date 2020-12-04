package com.cme.digicellogin.helper

import android.content.Context
import android.util.Log
import com.applicaster.util.StringUtil
import com.cme.digicellogin.models.DigicelPlan
import com.cme.digicellogin.models.DigicelToken
import com.cme.digicellogin.models.DigicelUser
import com.cme.digicellogin.models.SubscriberType
import com.cme.digicellogin.remote.Params
import com.cme.digicellogin.remote.WebService
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.swrve.sdk.SwrveIdentityResponse
import com.swrve.sdk.SwrveSDK
import org.json.JSONArray

object DigicelLoginApi {
    const val redirectURL: String = "https://applicaster.sportsmax/auth/"
    val clientID = PluginConfigurationHelper.getConfigurationValue(DIGICEL_CLIENT_ID)
    private val clientSecretKey = PluginConfigurationHelper.getConfigurationValue(DIGICEL_SECRET)
    private val androidVersion = PluginConfigurationHelper.getConfigurationValue(DIGICEL_ANDROID_VER)
    private val webService = WebService()

    private const val authorizationCode = "authorization_code"
    private const val android = "Android"

    var currentDigicelUser: DigicelUser? = null

    fun handleOAuthFlow(authCode: String, context: Context, callback: (WebService.Status, DigicelUser?) -> Unit) {
        fun handleUserAccount(digicelUser: DigicelUser) {
            this.currentDigicelUser = digicelUser
            DigicelCredentialsManager.saveDigicelUser(digicelUser)

            SwrveSDK.identify(digicelUser.userGuid, object : SwrveIdentityResponse {
                override fun onSuccess(status: String, swrveId: String) {
                    Log.wtf("Swrve onSuccess ", swrveId)

                }

                override fun onError(responseCode: Int, errorMessage: String) {
                    Log.wtf("Swrve onError ", errorMessage)
                }
            })

            callback(WebService.Status.Success, digicelUser)
        }

        fun handleAccessToken(digicelToken: DigicelToken) {
            fetchUserAccount(context, digicelToken.access_token) { status: WebService.Status, digicelUser: DigicelUser? ->
                if (status != WebService.Status.Success || digicelUser == null) {
                    callback(status, null)
                    return@fetchUserAccount
                }

                digicelUser.digicelToken = digicelToken
                handleUserAccount(digicelUser)
            }
        }

        fetchAccessToken(authCode, context) { status: WebService.Status, digicelToken: DigicelToken? ->
            if (status != WebService.Status.Success || digicelToken == null) {
                callback(status, null)
                return@fetchAccessToken
            }

            handleAccessToken(digicelToken)
        }
    }

    fun fetchUserSubscriptions(context: Context, callback: ((WebService.Status, List<DigicelPlan>?) -> Unit)) {
        val digicelUser = this.currentDigicelUser

        if (digicelUser?.digicelToken == null || this.androidVersion == null) {
            callback(WebService.Status.InternalError, null)
            return
        }

        val accessToken = digicelUser.digicelToken!!.access_token

        val params = Params()
        params["status"] = "active"

        val headers = Params()
        headers["Source"] = SPORTSMAX
        headers["AndroidVer"] = this.androidVersion

        webService.performApiWithAccessRequest(WebService.ApiRequest.GetSubscriptions, params, headers, accessToken, context) { status: WebService.Status, response: String? ->
            if (status != WebService.Status.Success || StringUtil.isEmpty(response)) {
                callback(status, null)
                return@performApiWithAccessRequest
            }

            val digicelPlans = ArrayList<DigicelPlan>()

            val plans = JSONArray(response)
            (0 until plans.length()).forEach { i ->
                val plan = plans.getJSONObject(i)
                if (plan != null && plan.optInt("size") > 0 && plan.optString("groupName") == SPORTSMAX) {
                    val subscriptions = plan.getJSONArray(SUBSCRIPTIONS)
                    if (subscriptions != null) {
                        try {
                            val gson = Gson()
                            val digicelPlan = gson.fromJson(subscriptions.getJSONObject(0).toString(), DigicelPlan::class.java)

                            digicelPlans.add(digicelPlan)

                        } catch (e: JsonSyntaxException) {
                        }
                    }
                }
            }

            digicelUser.digicelActivePlans = digicelPlans
            DigicelCredentialsManager.saveDigicelUser(digicelUser)

            callback(status, digicelPlans)
        }

    }

    fun fetchSubscriberType(context: Context, callback: ((WebService.Status) -> Unit)) {
        val digicelUser = this.currentDigicelUser
        val accessToken = digicelUser?.digicelToken?.access_token

        if (digicelUser == null || accessToken == null) {
            callback(WebService.Status.InternalError)
            return
        }

        webService.performApiWithAccessRequest(WebService.ApiRequest.GetSubscriberType, null, null, accessToken, context) { status: WebService.Status, response: String? ->
            when (status) {
                WebService.Status.Success -> digicelUser.subscriberType = SubscriberType.InNetwork
                WebService.Status.NotFound -> digicelUser.subscriberType = SubscriberType.OffNetwork
                else -> {
                    callback(status)
                    return@performApiWithAccessRequest
                }

            }

            DigicelCredentialsManager.saveDigicelUser(digicelUser)
            callback(WebService.Status.Success)
        }
    }

    private fun fetchAccessToken(authCode: String, context: Context, callback: (WebService.Status, digicelToken: DigicelToken?) -> Unit) {
        if (clientID == null || clientSecretKey == null) {
            callback(WebService.Status.InternalError, null)
            return
        }

        val params = Params()
        params["code"] = authCode
        params["client_id"] = clientID
        params["redirect_uri"] = redirectURL
        params["client_secret"] = clientSecretKey
        params["grant_type"] = authorizationCode

        webService.performApiRequest(WebService.ApiRequest.GetAccess, params, context) { status: WebService.Status, response: String? ->
            if (status != WebService.Status.Success || response == null) {
                callback(status, null)
                return@performApiRequest
            }

            val digicelToken: DigicelToken
            try {
                val gson = Gson()
                digicelToken = gson.fromJson(response, DigicelToken::class.java)

            } catch (e: JsonSyntaxException) {
                callback(WebService.Status.InvalidParameters, null)
                return@performApiRequest
            }

            callback(WebService.Status.Success, digicelToken)
        }
    }

    private fun fetchUserAccount(context: Context, accessToken: String, callback: (WebService.Status, DigicelUser?) -> Unit) {
        if (clientID == null) {
            callback(WebService.Status.InternalError, null)
            return
        }

        val params = Params()
        params["client_id"] = clientID
        params["redirect_uri"] = redirectURL
        params["grant_type"] = authorizationCode

        webService.performApiWithAccessRequest(WebService.ApiRequest.Login, params, null, accessToken, context) { status: WebService.Status, response: String? ->
            if (status != WebService.Status.Success || response == null) {
                callback(status, null)
                return@performApiWithAccessRequest
            }

            val digicelUser: DigicelUser
            try {
                val gson = Gson()
                digicelUser = gson.fromJson(response, DigicelUser::class.java)

            } catch (e: JsonSyntaxException) {
                callback(WebService.Status.InvalidParameters, null)
                return@performApiWithAccessRequest
            }

            callback(status, digicelUser)

        }
    }
}
