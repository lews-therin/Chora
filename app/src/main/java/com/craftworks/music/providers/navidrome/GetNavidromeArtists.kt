package com.craftworks.music.providers.navidrome

import android.util.Log
import com.craftworks.music.data.MediaData
import com.craftworks.music.data.artistList
import com.craftworks.music.data.selectedArtist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
@SerialName("artists")
data class Artists(val index: List<index>)

@Serializable
data class index(val artist: List<MediaData.Artist>? = listOf())

fun parseNavidromeArtistsJSON(
    response: String
) : List<MediaData.Artist> {
    val jsonParser = Json { ignoreUnknownKeys = true }
    val subsonicResponse = jsonParser.decodeFromJsonElement<SubsonicResponse>(
        jsonParser.parseToJsonElement(response).jsonObject["subsonic-response"]!!
    )

    val mediaDataArtists = mutableListOf<MediaData.Artist>()

    subsonicResponse.artists?.index?.forEach { index ->
        index.artist?.filterNot { newArtist ->
            //println(newArtist)
            artistList.any { existingArtist ->
                existingArtist.navidromeID == newArtist.navidromeID
            }
        }?.let { filteredArtists ->
            mediaDataArtists.addAll(filteredArtists) // Assuming mediaDataArtists is a mutable list
        }
    }

    Log.d("NAVIDROME", "Added artists. Total: ${mediaDataArtists.size}")

    return mediaDataArtists
}

fun parseNavidromeArtistAlbumsJSON(
    response: String,
    navidromeUrl: String,
    navidromeUsername: String,
    navidromePassword: String
) : MediaData.Artist {
    val jsonParser = Json { ignoreUnknownKeys = true }
    val subsonicResponse = jsonParser.decodeFromJsonElement<SubsonicResponse>(
        jsonParser.parseToJsonElement(response).jsonObject["subsonic-response"]!!
    )

    val mediaDataArtist = selectedArtist

    // Generate password salt and hash for album coverArt
    val passwordSaltArt = generateSalt(8)
    val passwordHashArt = md5Hash(navidromePassword + passwordSaltArt)

    subsonicResponse.artist?.album?.map {
        it.coverArt = "$navidromeUrl/rest/getCoverArt.view?&id=${it.navidromeID}&u=$navidromeUsername&t=$passwordHashArt&s=$passwordSaltArt&v=1.16.1&c=Chora"
    }

    mediaDataArtist.starred = subsonicResponse.artist?.starred
    mediaDataArtist.album = subsonicResponse.artist?.album

    Log.d("NAVIDROME", "Added Metadata to ${mediaDataArtist.name}")

    return mediaDataArtist
}

fun parseNavidromeArtistBiographyJSON(
    response: String
) : MediaData.Artist {
    val jsonParser = Json { ignoreUnknownKeys = true }
    val subsonicResponse = jsonParser.decodeFromJsonElement<SubsonicResponse>(
        jsonParser.parseToJsonElement(response).jsonObject["subsonic-response"]!!
    )

    val mediaDataArtist = selectedArtist

    mediaDataArtist.description = subsonicResponse.artistInfo?.biography.toString().replace(Regex("<a[^>]*>.*?</a>"), "")
    mediaDataArtist.musicBrainzId = subsonicResponse.artistInfo?.musicBrainzId
    mediaDataArtist.similarArtist = subsonicResponse.artistInfo?.similarArtist

    Log.d("NAVIDROME", "Added Metadata to ${mediaDataArtist.name}")

    return mediaDataArtist
}