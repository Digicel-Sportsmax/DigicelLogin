package com.cme.digicellogincme.models

enum class WebAuth {
    CREATE_ACCOUNT, LOGIN
}

data class DigicelWebResponse(val code: String?,
                              val type: WebAuth = WebAuth.LOGIN)

