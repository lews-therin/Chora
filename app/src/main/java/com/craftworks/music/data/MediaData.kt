package com.craftworks.music.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class MediaData {
    @Serializable
    data class Song(
        @SerialName("id")
        val navidromeID: String,
        val parent: String,
        val isDir: Boolean? = false,
        val title: String,
        val album: String,
        val artist: String,
        val track: Int? = 0,
        val year: Int? = 0,
        val genre: String? = "",
        @SerialName("coverArt")
        var imageUrl: String,
        val size: Int? = 0,
        val contentType: String? = "music",
        @SerialName("suffix")
        val format: String,
        val duration: Int = 0,
        val bitrate: Int? = 0,
        val path: String,
        @SerialName("playCount")
        var timesPlayed: Int? = 0,
        val discNumber: Int? = 0,
        @SerialName("created")
        val dateAdded: String,
        val albumId: String,
        val artistId: String? = "",
        val type: String? = "music",
        val isVideo: Boolean? = false,
        @SerialName("played")
        val lastPlayed: String? = "",
        val bpm: Int,
        val comment: String? = "",
        val sortName: String? = "",
        val mediaType: String? = "song",
        val musicBrainzId: String? = "",
        val genres: List<Genre>? = listOf(),
        val replayGain: ReplayGain? = null,
        val channelCount: Int? = 2,
        val samplingRate: Int? = 0,

        val isRadio: Boolean? = false,
        var media: String? = null,
        val trackIndex: Int? = 0
    ) : MediaData()

    @Serializable
    data class Album(
        @SerialName("id")
        val navidromeID : String,
        val parent : String? = "",

        val album : String? = "",
        val title : String? = "",
        val name : String? = "",

        val isDir : Boolean? = false,
        var coverArt : String?,
        val songCount : Int,

        val played : String? = "",
        val created : String? = "",
        val duration : Int,
        val playCount : Int? = 0,

        val artistId : String?,
        val artist : String,
        val year : Int? = 0,
        val genre : String? = "",
        val genres : List<Genre>? = listOf(),

        @SerialName("song")
        var songs: List<Song>? = listOf()
    ) : MediaData()
}