package com.cme.digicellogin.models

data class DigicelToken(val access_token: String,
                        val expires_in: String?,
                        val token_type: String?,
                        var refresh_token: String?)
