package ru.russkikh.musicplayer

import ru.russkikh.musicplayer.utils.MusicStore
import ru.russkikh.musicplayer.utils.SongSource
import ru.russkikh.musicplayer.utils.allSongs


class PlaylistsStorage(private val storage: MusicStore) {
    private val _playlists: MutableMap<String, List<Song>?> = HashMap()

    init {
        storage.getPlaylists().forEach {
            if (it.isNotBlank()) _playlists[it] = null
        }
    }

    val keys: List<String>
        get() = _playlists.keys.toList().filter { it.isNotBlank() }


    operator fun get(playlistName: String): List<Song>? {
        if (_playlists[allSongs] == null) {
            searchForSongs()
        }



        if (_playlists[playlistName] == null) {
            val playlist = storage.getPlaylist(playlistName)
            _playlists[playlistName] = _playlists[allSongs]?.filter { song -> song.id in playlist }
        }



        return _playlists[playlistName]
    }

    operator fun set(playlistName: String, songs: List<Song>) {
        if (playlistName == allSongs) {
            _playlists[allSongs] = songs
            return
        }
        if(_playlists[playlistName] != null) {
            storage.deletePlaylist(playlistName)
        }
        storage.addPlaylist(playlistName, songs)

        _playlists[playlistName] = songs
    }

    fun remove(playlistName: String) {
        storage.deletePlaylist(playlistName)
        _playlists.remove(playlistName)
    }

    private fun searchForSongs() {
        _playlists[allSongs] = SongSource.findSongs()
    }

}