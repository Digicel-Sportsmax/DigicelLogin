package com.cme.digicellogincme.helper

import com.applicaster.util.PreferenceUtil
import com.applicaster.util.StringUtil
import com.applicaster.util.serialization.SerializationUtils
import com.cme.digicellogincme.models.DigicelUser
import com.google.gson.Gson


class DigicelCredentialsManager {
    companion object {
        private const val DIGICEL_USER = "DigicelCredentialsManager.DigicelUser"

        @JvmStatic
        fun loadDigicelUser(): DigicelUser? {
            val rawValue = PreferenceUtil.getInstance().getStringPref(DIGICEL_USER, "")
            if (StringUtil.isEmpty(rawValue)) {
                return null
            }

            return Gson().fromJson(rawValue, DigicelUser::class.java);
        }

        @JvmStatic
        fun saveDigicelUser(user: DigicelUser?) {
            val rawValue = SerializationUtils.toJson(user, DigicelUser::class.java)
            PreferenceUtil.getInstance().setStringPref(DIGICEL_USER, rawValue)
        }
    }
}