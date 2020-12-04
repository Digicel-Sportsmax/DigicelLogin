package com.cme.digicellogincme.views
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.applicaster.activities.base.APBaseActivity
import com.applicaster.plugin_manager.login.LoginManager
import com.applicaster.plugin_manager.playersmanager.Playable
import com.applicaster.util.OSUtil
import com.cme.digicellogincme.R
import com.cme.digicellogincme.helper.*
import com.cme.digicellogincme.remote.WebService

import org.json.JSONObject
import java.util.*

abstract class DigicelBaseActivity : APBaseActivity() {

    var playable: Playable? = null
    var trigger: String? = null
    private var progressBar: View? = null

    abstract fun getContentViewResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if (OSUtil.isTablet()) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        playable = intent.getSerializableExtra(PLAYABLE) as Playable?
        trigger = intent.getSerializableExtra(TRIGGER) as String?
        progressBar = findViewById(R.id.progress_bar)

        this.customize()
    }


    open fun customize() {
//        CustomizationHelper.updateBgResource(this, R.id.background_image, "cleeng_login_background_image")
        this.customizeNavigationBar()
    }

    open fun customizeNavigationBar() {
        val navigationBarContainer = this.findViewById(R.id.toolbar) as View?
                ?: return

        val backButton = navigationBarContainer.findViewById<ImageView>(R.id.toolbar_back_button)
        backButton.setOnClickListener {
            backButtonPressed();

        }
    }

    open fun backButtonPressed() {
        LoginManager.notifyEvent(this, LoginManager.RequestType.CLOSED, false)
        this.finish()
    }

    fun dismissLoading() {
        progressBar?.visibility = View.GONE
    }

    fun showLoading() {
        progressBar?.visibility = View.VISIBLE
    }

    fun showError(status: WebService.Status, response: String?, callback: (() -> Unit)? = null) {
        val localizationKeys =  getLocaliztionText(status, response);
        showAlertDialog(this, localizationKeys[0], localizationKeys[1], callback)
    }

    fun showError(status: WebService.Status, response: String?) {
        showError(status, response, null)
    }

    companion object {
        fun showAlertDialog(activity:Activity, title: String, subtitle: String, callback: (() -> Unit)? = null) {

        }

        fun getLocaliztionText(status: WebService.Status, response: String?): MutableList<String> {
            val internalResponse = JSONObject(response)
            val errorCode = if (internalResponse.has("code")) internalResponse.get("code") else -1

            val localizationKeys = when (status) {
                WebService.Status.Unknown -> Arrays.asList(LOGIN_INTERNAL_ERROR_TITLE, LOGIN_INTERNAL_ERROR_MESSAGE)
                WebService.Status.WrongCredentials -> Arrays.asList(LOGIN_CREDENTAIL_ERROR_TITLE, LOGIN_CREDENTAIL_ERROR_MESSAGE)
                WebService.Status.InternalError -> when (errorCode) {
                    3 -> Arrays.asList(LOGIN_CREDENTAIL_ERROR_TITLE, "Invalid publisher token")
                    5 -> Arrays.asList(LOGIN_CREDENTAIL_ERROR_TITLE, "Access denied")
                    10 -> Arrays.asList("cleeng_login_error_invalid_email_credentials_title", "cleeng_login_error_invalid_email_credentials_message")
                    11 -> Arrays.asList(LOGIN_CREDENTAIL_ERROR_TITLE, "cleeng_login_error_credentials_message")
                    12 -> Arrays.asList("cleeng_login_error_expired_title", "cleeng_login_error_expired_message")
                    13 -> Arrays.asList("cleeng_login_error_existing_user_credentials_title", "cleeng_login_error_existing_user_credentials_message")
                    15 -> Arrays.asList("cleeng_login_error_invalid_credentials_title", "cleeng_login_error_invalid_credentials_message")
                    17 -> Arrays.asList("cleeng_login_error_coupon_title", "cleeng_login_error_coupon_message")
                    else -> Arrays.asList("cleeng_login_error_internal_title", "cleeng_login_error_internal_message")
                }
                else ->  Arrays.asList(LOGIN_INTERNAL_ERROR_TITLE, LOGIN_INTERNAL_ERROR_MESSAGE)
            }
            return localizationKeys;
        }
    }


}