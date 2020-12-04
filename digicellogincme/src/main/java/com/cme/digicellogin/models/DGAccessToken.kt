package com.cme.digicellogin.models

data class DGAccessToken(val expires_in: String,
                         val token_type: String,
                         val access_token: String,
                         var refresh_token: String)
