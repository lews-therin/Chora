package com.craftworks.music.providers

import android.content.Context
import android.net.Uri
import androidx.compose.ui.util.fastFilter
import com.craftworks.music.data.MediaData
import com.craftworks.music.data.albumList
import com.craftworks.music.data.playlistList
import com.craftworks.music.data.radioList
import com.craftworks.music.data.selectedArtist
import com.craftworks.music.managers.NavidromeManager
import com.craftworks.music.managers.SettingsManager
import com.craftworks.music.providers.local.localPlaylistImageGenerator
import com.craftworks.music.providers.navidrome.sendNavidromeGETRequest
import com.craftworks.music.ui.elements.dialogs.playlistToDelete
import com.craftworks.music.ui.elements.dialogs.songToAddToPlaylist
import com.craftworks.music.ui.screens.selectedAlbum
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// region Albums
suspend fun getAlbums(
    sort: String? = "alphabeticalByName", size: Int? = 100, offset: Int? = 0
): List<MediaData.Album> = coroutineScope {
    val deferredAlbums = mutableListOf<Deferred<List<MediaData.Album>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredAlbums.add(async {
            sendNavidromeGETRequest("getAlbumList.view?type=$sort&size=$size&offset=$offset&f=json").filterIsInstance<MediaData.Album>()
        })
    }

    deferredAlbums.awaitAll().flatten()
}

//suspend fun getAlbumSongs(albumId: String): List<MediaData.Song> = coroutineScope {
//    val deferredAlbumSongs = mutableListOf<Deferred<List<MediaData.Song>>>()
//
//    if (NavidromeManager.checkActiveServers()) {
//        deferredAlbumSongs.add(async {
//            sendNavidromeGETRequest("getAlbum.view?id=$albumId&f=json").filterIsInstance<MediaData.Song>()
//        })
//    }
//
//    deferredAlbumSongs.awaitAll().flatten()
//}

suspend fun getAlbum(albumId: String): MediaData.Album? = coroutineScope {
    if (NavidromeManager.checkActiveServers()) {
        val deferredAlbum = async {
            sendNavidromeGETRequest("getAlbum.view?id=$albumId&f=json")
                .filterIsInstance<MediaData.Album>()
                .firstOrNull() ?: throw IllegalStateException("Cannot get album data")
        }
        deferredAlbum.await()
    } else {
        selectedAlbum
    }
}

suspend fun searchAlbum(
    query: String? = ""
) : List<MediaData.Album> = coroutineScope {
    val deferredAlbums = mutableListOf<Deferred<List<MediaData.Album>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredAlbums.add(async {
            sendNavidromeGETRequest("search3.view?query=$query&songCount=0&songOffset=0&artistCount=0&albumCount=100&f=json").filterIsInstance<MediaData.Album>()
        })
    }

    deferredAlbums.awaitAll().flatten()
}
//endregion

//region Songs
suspend fun getSongs(
    query: String? = "",
    songCount: Int? = 100,
    songOffset: Int? = 0
) : List<MediaData.Song> = coroutineScope {
    val deferredSongs = mutableListOf<Deferred<List<MediaData.Song>>>()

    println("Getting deferred album songs")

    if (NavidromeManager.checkActiveServers()) {
        deferredSongs.add(async {
            sendNavidromeGETRequest("search3.view?query=$query&songCount=$songCount&songOffset=$songOffset&artistCount=0&albumCount=0&f=json").filterIsInstance<MediaData.Song>()
        })
    }

    deferredSongs.awaitAll().flatten()
}
//endregion

//region Artists
suspend fun getArtists(): List<MediaData.Artist> = coroutineScope {
    val deferredArtists = mutableListOf<Deferred<List<MediaData.Artist>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredArtists.add(async {
            sendNavidromeGETRequest("getArtists.view?size=100&f=json").filterIsInstance<MediaData.Artist>()
        })
    }

    deferredArtists.awaitAll().flatten()
}

suspend fun getArtistDetails(
    id: String
): MediaData.Artist = coroutineScope {
    if (NavidromeManager.checkActiveServers()) {
        val deferredArtist = async {
            val details = sendNavidromeGETRequest("getArtist.view?id=$id&f=json")
                .filterIsInstance<MediaData.Artist>()
                .firstOrNull() ?: throw IllegalStateException("No artist details returned")
            val biography = sendNavidromeGETRequest("getArtistInfo.view?id=$id&f=json")
                .filterIsInstance<MediaData.Artist>()
                .firstOrNull() ?: throw IllegalStateException("No artist biography returned")

            details.copy(
                description = biography.description,
                similarArtist = biography.similarArtist,
                musicBrainzId = biography.musicBrainzId
            )
        }
        deferredArtist.await()
    } else {
        selectedArtist.copy(
            album = albumList.fastFilter { it.artist == selectedArtist.name }
        )
    }
}

suspend fun searchArtist(
    query: String? = ""
) : List<MediaData.Artist> = coroutineScope {
    val deferredArtists = mutableListOf<Deferred<List<MediaData.Artist>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredArtists.add(async {
            sendNavidromeGETRequest("search3.view?query=$query&songCount=0&songOffset=0&artistCount=100&albumCount=0&f=json").filterIsInstance<MediaData.Artist>()
        })
    }

    deferredArtists.awaitAll().flatten()
}
//endregion

//region Radios
suspend fun getRadios(): List<MediaData.Radio> = coroutineScope {
    val deferredRadios = mutableListOf<Deferred<List<MediaData.Radio>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredRadios.add(async {
            sendNavidromeGETRequest("getInternetRadioStations.view?f=json").filterIsInstance<MediaData.Radio>()
        })
    }

    deferredRadios.awaitAll().flatten()
}

suspend fun createRadio(name:String, url:String, homePage:String, context: Context) {
    if (NavidromeManager.checkActiveServers())
        sendNavidromeGETRequest("createInternetRadioStation.view?name=$name&streamUrl=$url&homepageUrl=$homePage")
    else {
        radioList.add(
            MediaData.Radio(
                navidromeID = "Local",
                name = name,
                media = url,
                homePageUrl = homePage,
            )
        )
        SettingsManager(context).saveLocalRadios()
    }
}
suspend fun modifyRadio(radio: MediaData.Radio) {
    if (NavidromeManager.checkActiveServers())
        sendNavidromeGETRequest("updateInternetRadioStation.view?name=${radio.name}&streamUrl=${radio.media}&homepageUrl=${radio.homePageUrl}&id=${radio.navidromeID}")
    else {
        radioList.remove(radio)
        radioList.add(
            MediaData.Radio(
                navidromeID = "Local",
                name = radio.name,
                media = radio.media
            )
        )
    }
}
suspend fun deleteRadio(radio: MediaData.Radio, context: Context){
    if (NavidromeManager.checkActiveServers())
        sendNavidromeGETRequest("deleteInternetRadioStation.view?id=${radio.navidromeID}")
    else{
        radioList.remove(radio)
        SettingsManager(context).saveLocalRadios()
    }
}
//endregion

//region Playlists
suspend fun getPlaylists(): List<MediaData.Playlist> = coroutineScope {
    val deferredPlaylists = mutableListOf<Deferred<List<MediaData.Playlist>>>()

    if (NavidromeManager.checkActiveServers()) {
        deferredPlaylists.add(async {
            sendNavidromeGETRequest("getPlaylists.view?f=json").filterIsInstance<MediaData.Playlist>()
        })
    }

    deferredPlaylists.awaitAll().flatten()
}

suspend fun getPlaylistDetails(
    id: String
): MediaData.Playlist? = coroutineScope {
    val deferredPlaylist: Deferred<MediaData.Playlist>

    if (NavidromeManager.checkActiveServers()) {
        deferredPlaylist = async {
            sendNavidromeGETRequest("getPlaylist.view?id=$id&f=json").filterIsInstance<MediaData.Playlist>()
                .firstOrNull() ?: throw IllegalStateException("No artist details returned")
        }
        deferredPlaylist.await()
    } else {
        null
    }
}

suspend fun createPlaylist(playlistName: String, context: Context) {
    if (NavidromeManager.checkActiveServers()) sendNavidromeGETRequest("createPlaylist.view?name=$playlistName")
    else {
        val playlistImage: Uri = localPlaylistImageGenerator(
            listOf(songToAddToPlaylist.value), context
        ) ?: Uri.EMPTY
        playlistList.add(
            MediaData.Playlist(
                "Local",
                playlistName,
                playlistImage.toString(),
                changed = "",
                created = "",
                duration = 0,
                songCount = 0,
                songs = listOf(songToAddToPlaylist.value)
            )
        )
        SettingsManager(context).saveLocalPlaylists()
    }
}
suspend fun deletePlaylist(playlistID: String, context: Context){
    if (NavidromeManager.checkActiveServers())
        sendNavidromeGETRequest("deletePlaylist.view?id=$playlistID")
    else{
        playlistList.remove(playlistToDelete.value)
        SettingsManager(context).saveLocalPlaylists()
    }
}
suspend fun addSongToPlaylist(playlist: MediaData.Playlist, songID: String, context: Context){
    if (NavidromeManager.checkActiveServers())
        sendNavidromeGETRequest("updatePlaylist.view?playlistId=${playlist.navidromeID}&songIdToAdd=$songID")
    else {
        playlist.songs = playlist.songs?.plus(songToAddToPlaylist.value)
        SettingsManager(context).saveLocalPlaylists()
    }
}
//endregion