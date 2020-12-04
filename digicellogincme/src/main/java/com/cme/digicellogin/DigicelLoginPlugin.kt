package com.cme.digicellogin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.util.Log
import android.webkit.CookieManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.applicaster.app.CustomApplication
import com.applicaster.cleeng.CleengLoginPlugin
import com.applicaster.plugin_manager.PluginManager
import com.applicaster.plugin_manager.hook.HookListener
import com.applicaster.plugin_manager.login.AsyncLoginContract
import com.applicaster.plugin_manager.login.LoginContract
import com.applicaster.plugin_manager.login.LoginManager
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.util.StringUtil
import com.cme.digicellogin.helper.*
import com.cme.digicellogin.models.DigicelPlan
import com.cme.digicellogin.models.SubscriberType
import com.cme.digicellogin.models.UserType
import com.cme.digicellogin.remote.WebService
import com.cme.digicellogin.views.CustomDialog
import com.cme.digicellogin.views.LoginActivity
import com.cme.digicellogin.views.WelcomeActivity


class DigicelLoginPlugin : AsyncLoginContract() {
    private var cleengLogin: CleengLoginPlugin? = null



    override fun login(context: Context, additionalParams: Map<*, *>?, callback: LoginContract.Callback) {
        this.login(context, null, additionalParams as MutableMap<Any?, Any?>?, callback)
    }

    override fun login(context: Context, playable: Playable?, additionalParams: Map<*, *>?, callback: LoginContract.Callback) {
        val receiver = LoginManager.LoginContractBroadcasterReceiver(callback)
        LoginManager.registerToEvent(context, LoginManager.RequestType.LOGIN, receiver)

        this.login(context, playable, additionalParams as MutableMap<Any?, Any?>?)
    }


    override fun login(context: Context, additionalParams: MutableMap<Any?, Any?>?) {
        this.login(context, null, additionalParams)

    }

    override fun login(context: Context, playable: Playable?, additionalParams: MutableMap<Any?, Any?>?) {
    }


    override fun logout(context: Context, additionalParams: MutableMap<Any?, Any?>?) {
        logout(context, null)
    }

    override fun logout(context: Context?, additionalParams: MutableMap<Any?, Any?>?, callback: LoginContract.Callback?) {
        this.cleengLogin = createCleengLoginPlugin()
        DigicelCredentialsManager.saveDigicelUser(null)
        if (cleengLogin == null) {
            callback?.onResult(true)
        } else {
            val cleengLogoutParams = additionalParams ?: mutableMapOf()
            cleengLogoutParams[KEY_SKIP_LOGOUT_CONFIRMATION] = true.toString()
            cleengLogin?.logout(context, cleengLogoutParams) {
                callback?.onResult(true)
            }
        }
    }

    //this function called from image holder util and doesn't wait for a callback, in this case we can't know if a channel is
    //locked on not because channel that load inside a collection doesn't include the authorization_provider_is
    override fun isItemLocked(model: Any?): Boolean {
        return false
    }

    override fun isItemLocked(context: Context?, model: Any?, callback: LoginContract.Callback) {
        callback.onResult(false)
    }


    override fun isTokenValid(): Boolean {
        DigicelCredentialsManager.loadDigicelUser()?.let {
            return it.digicelToken?.access_token?.isNotEmpty() ?: false
        }
        return false
    }

    override fun getToken(): String {
        return ""
    }


    //according to the plugin configuration if the logged_in_free_access is on and the user logged in then return true;
//    private fun userFreePass(): Boolean {
//        return isTokenValid && DigicelManager.isFreeAccess()
//    }


    override fun executeOnApplicationReady(context: Context?, listener: HookListener) {
        listener.onHookFinished()
    }

    override fun executeOnStartup(context: Context, listener: HookListener) {
        listener.onHookFinished()
//        validateFreeAccessToken(context, "execute On Startup")
//        updateDigicelPlansIfNeeded(context,listener);
    }

    private fun continueExecuteOnStartup(context: Context, listener: HookListener) {
        val showOnAppLaunch = StringUtil.booleanValue(PluginConfigurationHelper.getConfigurationValue(START_ON_LAUNCH) as String)
        if (showOnAppLaunch) {
            //currently not supported
            listener.onHookFinished()
        } else {
            listener.onHookFinished()
        }
    }

    //    private fun updateDigicelPlansIfNeeded(context: Context, listener: HookListener) {
//        if(isTokenValid ){
//            LoginManager.registerToEvent(context, LoginManager.RequestType.LOGIN, LoginManager.LoginContractBroadcasterReceiver { isSuccess ->
//                listener.onHookFinished()
//            })
//            LoginManager.registerToEvent(context, LoginManager.RequestType.CLOSED, LoginManager.LoginContractBroadcasterReceiver { isSuccess ->
//                listener.onHookFinished()
//            })
//
//           if(DigicelManager.isDigicelUserWithNoStorePlan(PluginConfigurationHelper.getConfigurationValue(SPORTMAX_OFFER_ID))){
//               val user = Utils.getUser()
//               user?.userType = UserType.Basic
//               Utils.setUser(user)
//               if( CleengUtil.getUser() != null) {
//                   // store user
//                   DigicelManager.user = user;
//                   DigicelManager.cleengUser = CleengUtil.getUser()!!;
//                   //need to load Digicel plans
//                   DigicelManager.getDigicelSubscriptions(context)
//               }else{
//                   continueExecuteOnStartup(context, listener)
//               }
//           }else{
//               //continue with the flow
//               continueExecuteOnStartup(context, listener)
//           }
//        }else{
//            continueExecuteOnStartup(context, listener);
//        }
//    }
//
//    private fun validateFreeAccessToken(context: Context, trigger: String) {
//        val freeAccessAuthId = PluginConfigurationHelper.getConfigurationValue(DIGICEL_FREE_ACCESS_AUTH_ID)
//        if (isTokenValid && DigicelManager.isFreeAccess() && StringUtil.isNotEmpty(freeAccessAuthId)) {
//            if (StringUtil.isEmpty(AuthenticationProviderUtil.getToken(freeAccessAuthId)))
//                CleengManager.getUser()?.let {
//                    DigicelManager.cleengUser = CleengManager.getUser()!!;
//                    DigicelManager.user = Utils.getUser();
//                    DigicelManager.fetchFreeAccessToken(freeAccessAuthId!!, context, trigger, true)
//                }
//        }
//
//    }
//
//    override fun setPluginConfigurationParams(params: MutableMap<Any?, Any?>?) {
//        if (params != null) {
//            val cleengPlugin = CleengLoginPlugin()
//            cleengPlugin.setPluginConfigurationParams(params)
//            PluginConfigurationHelper.setConfigurationMap(params as Map<String, String>)
//        }
//    }
    override fun setPluginConfigurationParams(params: MutableMap<Any?, Any?>?) {
        @Suppress("UNCHECKED_CAST")
        (params as? MutableMap<String, String>)?.let { PluginConfigurationHelper.setConfigurationMap(it) }
    }

    override fun setToken(token: String?) {
        TODO("not implemented")
    }

    override fun handlePluginScheme(context: Context?, data: MutableMap<String, String>?): Boolean {
        var action = data?.get(SCHEME_ACTION_KEY)

        action?.let {
            when (it) {
                SCHEME_ACTION_LOGIN -> {
                    if (isTokenValid) {
                        userAlreadyLoginLogoutMessage(context as? Activity, true)
                    } else {
                        if (context is Activity) {
                            this.registerLoginActivityBroadcastReceiver(context)
                            WelcomeActivity.launchWelcomeActivity(context)
                        }
                    }
                    return true
                }
                SCHEME_ACTION_LOGOUT -> {
                    if (isTokenValid) {
                        removeCookies()
                        context?.let {
                            this.logout(context, null) { result ->
                                if (result && context is Activity) {
                                    CustomDialog(context,PluginConfigurationHelper.getConfigurationValue(""),
                                            PluginConfigurationHelper.getConfigurationValue(LOGOUT_SUCCESSFULLY_MSG),true).show()
                                }
                            }
                        }
                    } else {
                        userAlreadyLoginLogoutMessage(context as? Activity, false)
                    }
                }
                else -> return false
            }
        }
        return false;
    }

    private fun removeCookies() {
        val cookieManager = CookieManager.getInstance()

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cookieManager.removeSessionCookies {
            }
            cookieManager.removeAllCookies { aBoolean ->
                Log.d("DigicelLoginPlugin", "Cookie removed: " + aBoolean!!)
            }
        } else
            cookieManager.removeAllCookie()
    }

    private fun userAlreadyLoginLogoutMessage(activity: Activity?, isLogin: Boolean) {
        val title = if (isLogin) ALREADY_LOGIN_TITLE else ALREADY_LOGOUT_TITLE
        val subtitle = if (isLogin) ALREADY_LOGIN_MESSAGE else ALREADY_LOGOUT_MESSAGE
        activity?.let {
            CustomDialog(activity,PluginConfigurationHelper.getConfigurationValue(title),
                    PluginConfigurationHelper.getConfigurationValue(subtitle),false).show()


        }

    }

    private fun registerLoginActivityBroadcastReceiver(context: Context) {
        val receiver = object : BroadcastReceiver() {

            private fun getRedirectCode(intent: Intent): String? {
                val params = intent.getSerializableExtra(LoginActivity.PARAMS_EXTRA) as? Map<*, *>
                return params?.get("code") as? String
            }

            private fun handleFailure() {
                loginCallback(WebService.Status.InvalidParameters)
            }

            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    LoginActivity.OAUTH_FINISHED_BROADCAST -> {
                        val code = getRedirectCode(intent)
                        if (code.isNullOrEmpty()) handleFailure() else handleOAuthFlow(code, context)
                    }

                    LoginActivity.OAUTH_FAILED_BROADCAST -> handleFailure()
                    LoginActivity.CANCELED_BROADCAST -> handleFailure()

                    else -> handleFailure()

                }
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
            }
        }

        val filter = IntentFilter()
        filter.addAction(LoginActivity.OAUTH_FINISHED_BROADCAST)
        filter.addAction(LoginActivity.OAUTH_FAILED_BROADCAST)
        filter.addAction(LoginActivity.CANCELED_BROADCAST)

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter)
    }

    private fun loginCallback(status: WebService.Status) {
        if (status != WebService.Status.Success) {
            Log.d("barak","not Success")
        }else{
            Log.d("barak","Success")
            handleLoginCompletion()
        }
    }

    private fun handleOAuthFlow(redirectCode: String, context: Context) {
        DigicelLoginApi.handleOAuthFlow(authCode = redirectCode, context = context) { status, digicelUser ->
            this.cleengLogin = createCleengLoginPlugin()
            if (status != WebService.Status.Success || digicelUser == null) {
                loginCallback(status)
                return@handleOAuthFlow
            }

            val email = digicelUser.email
            if (!email.isNullOrEmpty()) {
                this.syncWithCleeng(context) { syncWithCleengStatus ->
                    loginCallback(status)
                }
            }
        }

    }

    private fun syncWithCleeng(context: Context, callback: (WebService.Status) -> Unit) {
        val cleengPlugin = this.cleengLogin
        val digicelUser = DigicelLoginApi.currentDigicelUser

        val email = digicelUser?.email
        if (cleengPlugin == null || digicelUser == null || email == null) {
            callback(WebService.Status.InvalidParameters)
            return
        }

        fun handleUserSubscriptions(status: WebService.Status, plans: List<DigicelPlan>?) {
            if (status != WebService.Status.Success) {
                callback(status)
                return
            }

            if (plans.isNullOrEmpty()) {
                callback(WebService.Status.Success)
                return
            }

            digicelUser.userType = UserType.Premium

            DigicelCredentialsManager.saveDigicelUser(digicelUser)
            callback(WebService.Status.Success)
        }

        fun handleDigicelSubscriptionType(status: WebService.Status) {
            if (status != WebService.Status.Success) {
                callback(status)
                return
            }

            when (digicelUser.subscriberType) {
                SubscriberType.InNetwork -> DigicelLoginApi.fetchUserSubscriptions(context, callback = ::handleUserSubscriptions)
                SubscriberType.OffNetwork -> callback(WebService.Status.Success)
                else -> callback(WebService.Status.InvalidParameters)
            }
        }

        val handleCleengRegistration = object : LoginContract.Callback {
            override fun onResult(result: Boolean) {
                if (!result) {
                    callback(WebService.Status.InvalidParameters)
                    return
                }

                digicelUser.userType = UserType.Basic
                DigicelCredentialsManager.saveDigicelUser(digicelUser)
                DigicelLoginApi.fetchSubscriberType(context, callback = ::handleDigicelSubscriptionType)
            }
        }

        val authData = hashMapOf("email" to email)

        cleengPlugin.signUp(authData = authData, callback = handleCleengRegistration)
    }

    private fun createCleengLoginPlugin(): CleengLoginPlugin? {
        return PluginManager.getInstance().getInitiatedPlugin("CleengDigicel")?.instance as? CleengLoginPlugin
    }

    private fun handleLoginCompletion() {
        notifyAuthFlowCompletion()

        val digicelUser = DigicelLoginApi.currentDigicelUser
        if (digicelUser != null && digicelUser.userType == UserType.Premium) {
            return
        }
    }

    private fun notifyAuthFlowCompletion() {
        val completionIntent = AuthFlowResultReceiver.createCompletionIntent(true)
        LocalBroadcastManager.getInstance(CustomApplication.getAppContext()).sendBroadcast(completionIntent)
    }

    companion object {
        const val KEY_SKIP_LOGOUT_CONFIRMATION = "skipConfirmationDialog"
    }
}
