package com.cme.digicellogin.helper

import com.applicaster.analytics.AnalyticsAgentUtil
import com.applicaster.util.StringUtil


class AnalyticsHelper {

    companion object {

        // login events
        val START_LOGIN = "Start Login"
        val LOGIN_SUCCEEDS = "Login Succeeds"
        val LOGIN_DOES_NOT_SUCCEED = "Login Does Not Succeed"
        val START_REGISTRATION = "Start Registration"
        val REGISTRATION_SUCCEEDS = "Registration Succeeds"
        val REGISTRATION_DOES_NOT_SUCCEED = "Registration Does Not Succeed"
        val PW_RECOVERY_INITIALIZED = "PW Recovery Initialized"

        // properties
        val LOGIN_NAME = "Login Name"
        val REQUIRED_FIELDS = "Required Fields"
        val OPTIONAL_FIELDS = "Optional Fields"
        val TRIGGER = "Trigger"
        val REASON = "Reason"
        val ERROR_MESSAGE = "Error Message"


        // redeem code events
        val REDEEM_CODE_CLICKED = "Redeem Code Clicked"
        val REDEEM_SUCCESS = "Redeem success"
        val REDEEM_FAIL = "Redeem Fail"

        val OFFER_ID = "Offer ID"
        val AUTH_ID = "Auth Id"
        val EVENT_NAME = "event name"

        @JvmStatic
        fun sendLoginEvent(eventName: String, extraParams: Map<String, String>?, trigger: String) {
            val analyticsParams = HashMap<String, String>()
            analyticsParams.put(AnalyticsHelper.REQUIRED_FIELDS, "User Name | Password")
            analyticsParams.put(AnalyticsHelper.OPTIONAL_FIELDS, "FacebookId")
            analyticsParams.put(AnalyticsHelper.TRIGGER, trigger)

            if(extraParams != null && extraParams.isNotEmpty())
                analyticsParams.putAll(extraParams)
            AnalyticsAgentUtil.logEvent(eventName, analyticsParams)
        }

        @JvmStatic
        fun sendErrorEvent(eventName: String, trigger: String, reason: String, errorMsg: String? = "") {
            val extraParams = HashMap<String, String>()
            extraParams[AnalyticsHelper.REASON] = reason
            if (StringUtil.isNotEmpty(errorMsg)) extraParams[AnalyticsHelper.ERROR_MESSAGE] = errorMsg!!
            AnalyticsHelper.sendLoginEvent(eventName, extraParams, trigger)
        }


        @JvmStatic
        fun sendRedeemCodeEvent(eventName: String, offerId: String, authId : String) {
            val analyticsParams = HashMap<String, String>()
            analyticsParams[AnalyticsHelper.OFFER_ID] = offerId
            analyticsParams[AnalyticsHelper.AUTH_ID] = authId
            AnalyticsAgentUtil.logEvent(eventName, analyticsParams)
        }

    }
}
