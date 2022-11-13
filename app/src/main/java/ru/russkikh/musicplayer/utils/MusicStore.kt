package ru.russkikh.musicplayer.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.russkikh.musicplayer.Song
import java.io.Closeable

class MusicStore(private val db: SQLiteDatabase) : Closeable {

    private val insert = db.compileStatement(
        "INSERT INTO playlist (playlistName, songId) VALUES (?, ?)"
    )

    private val delete = db.compileStatement(
        "DELETE FROM playlist WHERE playlistName = ?"
    )

    fun getPlaylists(): List<String> {
        val playlists = mutableListOf<String>()

        db.query(true,
            "playlist",
            arrayOf("playlistName"),
            null,
            null,
            null,
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow("playlistName")
                do {
                    playlists.add(cursor.getString(index))
                } while (cursor.moveToNext())
            }
        }

        return playlists.toList()
    }


    fun deletePlaylist(playlistName: String): Boolean {
        delete.bindString(1, playlistName)
        return delete.executeUpdateDelete() > 0
    }

    fun getPlaylist(playlistName: String): List<Long> {
        val ids = mutableListOf<Long>()

        db.query(
            "playlist",
            arrayOf("songId"),
            "playlistName = ?",
            arrayOf(playlistName),
            null,
            null,
            null
        ).use { cursor ->


            if(cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndexOrThrow("songId")
                do {
                    ids.add(cursor.getLong(idIndex))
                }while (cursor.moveToNext())
            }
        }

        return ids.toList()


    }
    fun addPlaylist(playlist: String, songs: List<Song>) {
        songs.forEach {
            insert(playlist, it.id)
        }
    }


    private fun insert(playlist: String, songId: Long): Long {
        insert.bindString(1, playlist)
        insert.bindLong(2, songId)
        println("Inserted: playlist $playlist - song id: $songId")
        return insert.executeInsert()
    }


    override fun close() {
        insert.close()
        delete.close()

    }
}


class DbHelper(
    context: Context,
) : SQLiteOpenHelper(context, "musicPlayerDatabase.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = 1")
        db.execSQL("PRAGMA trusted_schema = 0")
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE playlist(playlistName TEXT NOT NULL, songId INTEGER NOT NULL, PRIMARY KEY (playlistName, songId));""")
    }

    override fun onUpgrade(
        db: SQLiteDatabase, oldVersion: Int, newVersion: Int
    ) {
        throw UnsupportedOperationException()
    }

}