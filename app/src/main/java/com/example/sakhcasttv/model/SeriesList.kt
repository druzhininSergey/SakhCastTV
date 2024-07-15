package com.example.sakhcasttv.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SeriesList(
    @SerialName("amount")
    val amount: Int,
    @SerialName("items")
    val items: List<SeriesCard>
)

@Immutable
@Serializable
data class SeriesCard(
    @SerialName("available")
    val available: Boolean,
    @SerialName("cover")
    val cover: String,
    @SerialName("cover_alt")
    val coverAlt: String,
    @SerialName("cover_colors")
    val coverColors: CoverColors?,
    @SerialName("cover_h")
    val coverH: Int,
    @SerialName("cover_lq")
    val coverLq: String,
    @SerialName("cover_w")
    val coverW: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("imdb")
    val imdb: Boolean,
    @SerialName("imdb_rating")
    val imdbRating: Double?,
    @SerialName("kp")
    val kp: Boolean,
    @SerialName("kp_rating")
    val kpRating: Double?,
    @SerialName("link")
    val link: String,
    @SerialName("name")
    val name: String,
    @SerialName("new_episodes")
    val newEpisodes: Int,
    @SerialName("progress")
    val progress: Progress?,
    @SerialName("seasons")
    val seasons: String,
    @SerialName("tvshow")
    val tvshow: String,
    @SerialName("year")
    val year: Int
)

@Immutable
@Serializable
data class Progress(
    val viewed: Int,
    val amount: Int,
    val perc: Int,
    val variant: String,
)

@Immutable
@Serializable
data class CoverColors(
    @SerialName("background1")
    val background1: String,
    @SerialName("background2")
    val background2: String,
    @SerialName("lum")
    val lum: Double,
    @SerialName("text")
    val text: String,
    @SerialName("title")
    val title: String
)