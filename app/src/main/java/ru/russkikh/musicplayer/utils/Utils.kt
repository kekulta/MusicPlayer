package ru.russkikh.musicplayer.utils


import android.os.Build
import android.widget.SeekBar
import ru.russkikh.musicplayer.MainActivity
import ru.russkikh.musicplayer.Song
import ru.russkikh.musicplayer.SongSelector
import java.text.SimpleDateFormat
import java.util.*


enum class State { PLAY_MUSIC, ADD_PLAYLIST }
enum class SongState { PLAY, PAUSE, STOP }


const val allSongs = "All Songs"
const val MEDIA_REQUEST_CODE = 1

fun playListToSelector(songs: List<Song>, prevSelector: List<SongSelector>): List<SongSelector> {
    val selectors = mutableListOf<SongSelector>()
    val selectedSongs = prevSelector.filter { it.isSelected }.map { it.song }.toList()
    for (song in songs) {
        selectors.add(SongSelector(song = song, isSelected = song in selectedSongs))
    }
    return selectors.toList()
}

fun millisToClock(millis: Int): String{
    return SimpleDateFormat("mm:ss", Locale.FRANCE).format(millis+500)
}

class SeekbarListener(val mainActivity: MainActivity): SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p0 == null) return
        mainActivity.mainPlayerControllerFragment.binding.controllerTvCurrentTime.text =
            SimpleDateFormat("mm:ss", Locale.FRANCE).format(p0.progress*1000)
        println("Seekbar progress changed. Current progress is ${p0.progress} out of ${p0.max}")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            println("Sync callbacks: ${mainActivity.handler.hasCallbacks(mainActivity.controllerSync)}")
        }

        mainActivity.handler.removeCallbacks(mainActivity.controllerSync)
        println("Seekbar touched, sync stopped")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            println("Sync callbacks: ${mainActivity.handler.hasCallbacks(mainActivity.controllerSync)}")
        }
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        if (p0 == null) return
        mainActivity.player?.seekTo(p0.progress*1000)
        if (mainActivity.currentTrack.state == SongState.PLAY) mainActivity.startSync()
        println("Seekbar released. Final progress is ${p0.progress} out of ${p0.max}")
    }

}