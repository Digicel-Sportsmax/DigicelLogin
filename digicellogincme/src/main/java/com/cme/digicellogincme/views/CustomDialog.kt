package com.cme.digicellogincme.views

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.applicaster.util.StringUtil
import com.cme.digicellogincme.R
import kotlinx.android.synthetic.main.customdialogdigicel.*

/**
 * Created by Barak Halevi on 16/06/2020.
 */
class CustomDialog(private val context_: Activity, private val title_: String, private val subtitle_: String, private val redirect: Boolean) : Dialog(context_) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.customdialogdigicel)
        if (!StringUtil.isEmpty(title_)){
            titleTextView.text = title_
        }
        subtitleTextView.text = subtitle_
        closeButton.setOnClickListener{
            dismiss()
            if(redirect){
                context_.finish()
            }
        }
    }
}