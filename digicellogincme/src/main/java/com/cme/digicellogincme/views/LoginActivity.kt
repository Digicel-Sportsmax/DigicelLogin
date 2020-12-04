package com.cme.digicellogincme.views


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.applicaster.plugin_manager.login.LoginManager
import com.applicaster.util.OSUtil
import com.cme.digicellogincme.R
import com.cme.digicellogincme.helper.*
import com.cme.digicellogincme.models.DigicelWebResponse
import com.cme.digicellogincme.models.WebAuth
import kotlinx.android.synthetic.main.login_active.*
import kotlinx.android.synthetic.main.login_active.toolbar_back_button
import rx.Observer
import java.util.*


class LoginActivity : DigicelBaseActivity(), AuthFlowResultReceiver.AuthFlowResultListener {

    private var code: String? = null
    private var createAccount = false
    private var authFlowResultReceiver: AuthFlowResultReceiver? = null

    override fun getContentViewResId(): Int {
        return R.layout.login_active
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_active)
        CustomizationHelper.updateBgResource(this, R.id.background_image, "cleeng_login_back_button")
        createAccount = intent.getBooleanExtra(CREATE_ACCOUNT, false)
        CustomizationHelper.updateImageView(toolbar_back_button, "cleeng_login_back_button")
        toolbar_back_button.setOnClickListener {
            LoginManager.notifyEvent(this, LoginManager.RequestType.LOGIN, false)
            finish()
        }
        openDigicelLink()
    }

    override fun backButtonPressed() {
        sendBroadcast(CANCELED_BROADCAST)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        web_view.observer = getObserver()
        registerFlowCompletionReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterFlowCompletionReceiver()
    }

    private fun registerFlowCompletionReceiver() {
        authFlowResultReceiver = AuthFlowResultReceiver(this)
        authFlowResultReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, it.getFilter())
        }
    }

    private fun unregisterFlowCompletionReceiver() {
        authFlowResultReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        authFlowResultReceiver = null
    }

    override fun onAuthFlowHandlingFinished(isSucceed: Boolean) {
        if (isSucceed) {
            finishWithResult()
        }
    }

    override fun onBackPressed() {
        finishWithResult()
    }

    private fun finishWithResult() {
        var res = Activity.RESULT_CANCELED
        code?.let {
            res = Activity.RESULT_OK
        }
        val returnIntent = Intent()
        returnIntent.putExtra("result", res)
        setResult(res, returnIntent)
        finish()
    }

    private fun getObserver(): Observer<DigicelWebResponse> {
        return object : Observer<DigicelWebResponse> {
            override fun onCompleted() {
            }

            override fun onNext(response: DigicelWebResponse) {
                when (response.type) {
                    WebAuth.LOGIN -> {
                        code = response.code
                        response.code?.let {
                            sendBroadcast(OAUTH_FINISHED_BROADCAST, params = hashMapOf("code" to code))
                        }
                    }
                    WebAuth.CREATE_ACCOUNT -> {
                        createAccount = false
                        openDigicelLink()
                    }
                }
            }

            override fun onError(e: Throwable) {
                sendBroadcast(OAUTH_FAILED_BROADCAST)
            }
        }
    }

    private fun sendBroadcast(name: String, params: HashMap<*, *>? = null) {
        val intent = Intent(name)

        if (params != null) {
            intent.putExtra(PARAMS_EXTRA, params)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun openDigicelLink() {
        AnalyticsHelper.sendLoginEvent(AnalyticsHelper.START_LOGIN, null, trigger ?: "")

        when (createAccount) {
            true -> {
                var baseAccountUrl = PluginConfigurationHelper.getConfigurationValue(DIGICEL_CREATE_ACCOUNT_URL)?:""
                web_view.loadUrl(this, baseAccountUrl, false, -1, getString(OSUtil.getStringResourceIdentifier("scheme_url_prefix")))
            }
            false -> {
                val loginUrl = PluginConfigurationHelper.getConfigurationValue(DIGICEL_LOGIN_URL)?:""
                web_view.loadUrl(this, loginUrl, false, -1, DigicelLoginApi.redirectURL)
            }
        }
    }

    companion object {
        const val OAUTH_FINISHED_BROADCAST = "OAUTH_FINISHED_BROADCAST"
        const val OAUTH_FAILED_BROADCAST = "OAUTH_FAILED_BROADCAST"
        const val CANCELED_BROADCAST = "CANCELED_BROADCAST"
        const val PARAMS_EXTRA = "PARAMS_EXTRA"

        const val CREATE_ACCOUNT = "CREATE_ACCOUNT"

        fun launchLogin(context: Activity, createAccount: Boolean, trigger: String?) {
            val intent = Intent(context, LoginActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(CREATE_ACCOUNT, createAccount)

            if (trigger != null) intent.putExtra(TRIGGER, trigger)
            context.startActivityForResult(intent,123)
        }
    }

}