package com.example.sakhcasttv.model

data class VideoQuality(
    val resolution: String,
    val bandwidth: Int,
    val uri: String
)

data class Subtitle(
    val id: String,
    val name: String,
    val language: String
)

data class AudioTrackReceived(
    val id: String,
    val name: String,
    val language: String
)