package ru.russkikh.musicplayer

import ru.russkikh.musicplayer.utils.SongState


data class Song(
    val id: Long = 0,
    val title: String = "title$id",
    val artist: String = "artist$id",
    val duration: Int = 215_000,
    var index: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + duration
        result = 31 * result + index
        return result
    }
}

data class SongSelector(val song: Song, var isSelected: Boolean = false)


class Track(
    song: Song? = null,
    state: SongState = SongState.STOP,
    //var position: Int = 0,

) {
    private var onStateUpdated: () -> Unit = {}
    private var onSongUpdated: () -> Unit = {}
    var state: SongState = state
        private set(value) {
            field = value
            onStateUpdated()
        }
    var song: Song? = song
        set(value) {
            field = value
            onSongUpdated()
        }

    fun stop() {
        state = SongState.STOP
        onStateUpdated()
        println("Current track stopped")
    }

    fun pause() {
        state = SongState.PAUSE
        onStateUpdated()
        println("Current track paused")
    }

    fun play() {
        state = SongState.PLAY
        onStateUpdated()
        println("Current track played")
    }

    fun setOnStateUpdatedListener(listener: () -> Unit) {
        onStateUpdated = listener
    }

    fun setOnSongUpdatedListener(listener: () -> Unit) {
        onSongUpdated = listener
    }
}