package ru.russkikh.musicplayer.utils

import android.content.ContentResolver
import android.provider.MediaStore
import ru.russkikh.musicplayer.Song


object SongSource {
    private lateinit var contentResolver: ContentResolver

    fun init(contentResolver: ContentResolver) {
        SongSource.contentResolver = contentResolver
    }

    fun findSongs(): List<Song> {
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projections = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
        )

        val songs = mutableListOf<Song>()
        contentResolver.query(
            contentUri,
            projections,
            null,
            null,
            "${MediaStore.Audio.Media._ID} ASC"
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val durationIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                var index = 0
                do {
                    val song = Song(
                        id = cursor.getLong(idIndex),
                        artist = cursor.getString(artistIndex),
                        title = cursor.getString(titleIndex),
                        duration = cursor.getInt(durationIndex),
                        index = index
                    )
                    println("Song added: $song")
                    songs.add(song)
                    index++
                } while (cursor.moveToNext())
            }
        }
        return songs.toList()
    }
}