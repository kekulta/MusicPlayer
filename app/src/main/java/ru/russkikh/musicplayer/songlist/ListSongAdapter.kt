package ru.russkikh.musicplayer.songlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.russkikh.musicplayer.MainActivity
import ru.russkikh.musicplayer.R
import ru.russkikh.musicplayer.Song
import ru.russkikh.musicplayer.SongSelector
import ru.russkikh.musicplayer.utils.SongState
import ru.russkikh.musicplayer.utils.allSongs
import java.text.SimpleDateFormat
import java.util.*

class ListSongAdapter(var songs: List<Song>, val activity: MainActivity) :
    RecyclerView.Adapter<ListSongAdapter.SongViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_song, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        val currentTrack = activity.currentTrack

        holder.title.text = song.title
        holder.artist.text = song.artist
        holder.duration.text = SimpleDateFormat("mm:ss", Locale.FRANCE).format(song.duration)
        holder.button.setImageResource(
            if (song.id == currentTrack.song?.id && currentTrack.state == SongState.PLAY) {
                //currentTrack.position = holder.adapterPosition
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        )
        holder.itemView.setOnLongClickListener {
            activity.loadedPlaylist = allSongs
            activity.setAddState(listOf(SongSelector(song = song, isSelected = true)))
            true
        }
        holder.button.setOnClickListener {
            println("Image button ${holder.title.text} clicked.")
            if (song.id == currentTrack.song?.id) {
                when (currentTrack.state) {
                    SongState.STOP -> {
                        currentTrack.play()
                    }
                    SongState.PAUSE -> {
                        currentTrack.play()
                    }
                    SongState.PLAY -> {
                        currentTrack.pause()
                    }
                }
            } else {
                currentTrack.stop()
                currentTrack.song = song
                //currentTrack.position = holder.adapterPosition
                currentTrack.play()
            }
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.songItemTvTitle)
        val artist = view.findViewById<TextView>(R.id.songItemTvArtist)
        val duration = view.findViewById<TextView>(R.id.songItemTvDuration)
        val button = view.findViewById<ImageButton>(R.id.songItemImgBtnPlayPause)

    }
}