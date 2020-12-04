package com.cme.digicellogin.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class AuthFlowResultReceiver(private val listener: AuthFlowResultListener?) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AUTH_FLOW_HANDLING_COMPLETED_BROADCAST -> {
                val isSucceed = intent.extras?.getBoolean(KEY_IS_SUCCESS) ?: false
                listener?.onAuthFlowHandlingFinished(isSucceed)
            }
        }
    }

    fun getFilter() = IntentFilter().apply { addAction(AUTH_FLOW_HANDLING_COMPLETED_BROADCAST) }

    interface AuthFlowResultListener {
        fun onAuthFlowHandlingFinished(isSucceed: Boolean)
    }

    companion object {
        const val AUTH_FLOW_HANDLING_COMPLETED_BROADCAST = "AUTH_FLOW_HANDLING_COMPLETED_BROADCAST"
        const val KEY_IS_SUCCESS = "KEY_IS_SUCCESS"

        fun createCompletionIntent(isSucceed: Boolean) = Intent().apply {
            action = AUTH_FLOW_HANDLING_COMPLETED_BROADCAST
            putExtra(KEY_IS_SUCCESS, isSucceed)
        }
    }
}