package com.cme.digicellogin.models

data class DigicelPlan(val name: String,
                       val dateEnd: Long,
                       val dateStart: Long,
                       var description: String,
                       var planId: Int,
                       val subscriptionId: Long)


