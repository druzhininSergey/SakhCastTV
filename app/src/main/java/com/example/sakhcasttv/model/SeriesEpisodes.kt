package com.example.sakhcasttv.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SeriesEpisode(
    val date: String,
    val id: Int,
    val index: String,
    val isViewed: Int,
    val medias: List<Media>,
    val name: String,
    val preview: String,
    @SerialName("preview_alt")
    val previewAlt: String
)

@Immutable
@Serializable
data class PlaylistVariants(
    val type: String,
    val url: String
)

@Immutable
@Serializable
data class Media(
    val id: Int,
    val name: String,
    @SerialName("playlist_url")
    val playlistUrl: String,
    @SerialName("playlist_variants")
    val playlistVariants: List<PlaylistVariants>,
    @SerialName("runame")
    val ruName: String
)