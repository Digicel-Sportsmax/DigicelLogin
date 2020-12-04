package com.cme.digicellogin.models

enum class UserType {
    Free,
    Basic,
    Premium
}

enum class SubscriberType {
    InNetwork, OffNetwork
}

data class DigicelUser(
        var email: String? = null,
        var password: String? = null,

        var msisdn: String? = null,
        var userGuid: String? = null,
        var userId: String? = null,

        var firstName: String? = null,
        var lastName: String? = null,

        var countryCode: String? = null,
        var international: String? = null,

        var digicelToken: DigicelToken? = null,
        var digicelActivePlans: List<DigicelPlan>? = null,
        var subscriberType: SubscriberType? = null,

        var enabled: String? = null,
        var userType: UserType = UserType.Free
)



