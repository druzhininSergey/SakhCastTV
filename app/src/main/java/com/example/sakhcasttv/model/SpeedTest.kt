package com.example.sakhcasttv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeedTest(
    val info: TestInfo,
    val ip: String,
    val location: String,
    val url: String
)

@Serializable
data class TestInfo(
    val asn: Int,
    @SerialName("is_blacklisted") val isBlacklisted: Boolean,
    @SerialName("is_vpn") val isVpn: Boolean,
    val net: String,
    val org: String
)