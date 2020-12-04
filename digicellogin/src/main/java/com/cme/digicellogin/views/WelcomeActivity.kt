package com.cme.digicellogin.views

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.applicaster.plugin_manager.login.LoginManager
import com.applicaster.util.OSUtil
import com.cme.digicellogin.R
import com.cme.digicellogin.helper.*
import kotlinx.android.synthetic.main.welcome_activity.*


class WelcomeActivity : DigicelBaseActivity() {
    override fun getContentViewResId(): Int {
        return R.layout.welcome_activity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        requestedOrientation = if (OSUtil.isTablet()) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        CustomizationHelper.updateImageView(toolbar_back_button, "cleeng_login_back_button")
        toolbar_back_button.setOnClickListener {
            LoginManager.notifyEvent(this, LoginManager.RequestType.LOGIN, false)
            finish()
        }
        initView()
    }

    private fun initView() {
        initStyles()
        setListeners()
    }

    private fun setListeners() {
        findViewById<View?>(R.id.create_account_button)?.setOnClickListener { v: View -> launchLoginActivity(true) }
        findViewById<View?>(R.id.login_button)?.setOnClickListener { v: View -> launchLoginActivity(false) }
    }

    private fun initStyles() {
        val title = findViewById<TextView?>(R.id.first_title_text_view)
        val secondTitle = findViewById<TextView?>(R.id.second_title_text_view)

        val subtitle = findViewById<TextView?>(R.id.subtitle_text_view)
        val login_text_view = findViewById<TextView?>(R.id.login_text_view)
        val create_account_text_view = findViewById<TextView?>(R.id.create_account_text_view)

        UiHelper.updateTextViewText(title, DIGICEL_WELCOME_SCREEN_TITLE)
        UiHelper.updateTextViewText(secondTitle, DIGICEL_WELCOME_SCREEN_SECOND_TITLE)
        UiHelper.updateTextViewText(subtitle, DIGICEL_WELCOME_SCREEN_DESCRIPTION)
        UiHelper.updateTextViewText(login_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT)
        UiHelper.updateTextViewText(create_account_text_view, DIGICEL_WELCOME_SCREEN_CREATE_ACCOUNT_TEXT)

        UiHelper.updateTextViewColor(title, DIGICEL_WELCOME_SCREEN_TITLE_COLOR)
        UiHelper.updateTextViewColor(secondTitle, DIGICEL_WELCOME_SCREEN_SECOND_TITLE_COLOR)
        UiHelper.updateTextViewColor(subtitle, DIGICEL_WELCOME_SCREEN_DESCRIPTION_COLOR)
        UiHelper.updateTextViewColor(login_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_COLOR)
        UiHelper.updateTextViewColor(create_account_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_COLOR)

        UiHelper.updateTextViewSize(title, DIGICEL_WELCOME_SCREEN_TITLE_SIZE)
        UiHelper.updateTextViewSize(secondTitle, DIGICEL_WELCOME_SCREEN_TITLE_SIZE)
        UiHelper.updateTextViewSize(subtitle, DIGICEL_WELCOME_SCREEN_DESCRIPTION_SIZE)
        UiHelper.updateTextViewSize(login_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_SIZE)
        UiHelper.updateTextViewSize(create_account_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_SIZE)

        UiHelper.updateTextViewFont(title, DIGICEL_WELCOME_SCREEN_TITLE_FONT)
        UiHelper.updateTextViewFont(secondTitle, DIGICEL_WELCOME_SCREEN_TITLE_FONT)
        UiHelper.updateTextViewFont(subtitle, DIGICEL_WELCOME_SCREEN_DESCRIPTION_FONT)
        UiHelper.updateTextViewFont(login_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_FONT)
        UiHelper.updateTextViewFont(create_account_text_view, DIGICEL_WELCOME_SCREEN_LOGIN_BUTTON_TEXT_FONT)
    }

    private fun launchLoginActivity(createAccount: Boolean) {
        LoginActivity.launchLogin(this, createAccount, trigger)
    }

    companion object {

        fun launchWelcomeActivity(context: Activity) {
            val intent = Intent(context, WelcomeActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            context.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }


}