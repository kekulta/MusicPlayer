package ru.russkikh.musicplayer.songlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.russkikh.musicplayer.R

import ru.russkikh.musicplayer.SongSelector
import java.text.SimpleDateFormat
import java.util.*

class ListSongSelectorAdapter(var selectors: List<SongSelector>) : RecyclerView.Adapter<ListSongSelectorAdapter.SongSelectorViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSelectorViewHolder {
        return SongSelectorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_song_selector, parent, false))
    }


    override fun onBindViewHolder(holder: SongSelectorViewHolder, position: Int) {
//        println("____________onBindViewHolder_______________")
//        println("holder.adapterPosition: ${holder.adapterPosition}")
//        println("position: $position")
//        println("selectors[holder.adapterPosition]: ${selectors[holder.adapterPosition]}")
//        println("holder.checkBox.isChecked: ${holder.checkBox.isChecked}")
        val selector = selectors[holder.adapterPosition]
        holder.checkBox.isChecked = selector.isSelected
//        println("holder.checkBox.isChecked: ${holder.checkBox.isChecked}")
//        println("___________________________")
        holder.itemView.setBackgroundColor(if (selector.isSelected) Color.LTGRAY else Color.WHITE)
        holder.artist.text = selector.song.artist
        holder.title.text = selector.song.title
        holder.duration.text = SimpleDateFormat("mm:ss", Locale.FRANCE).format(selector.song.duration)
        holder.itemView.setOnClickListener {
//            println("___________listSongSelector clicked________________")
//            println(holder.title.text)
//            println("position: $position, holder.adapterPosition: ${holder.adapterPosition}, ")
//            println("selectors[holder.adapterPosition].isSelected: ${selectors[holder.adapterPosition].isSelected}")
            selectors[holder.adapterPosition].isSelected = !selectors[holder.adapterPosition].isSelected
//            println("selectors[holder.adapterPosition].isSelected: ${selectors[holder.adapterPosition].isSelected}")
//            println("holder.checkBox.isChecked: ${holder.checkBox.isChecked}")
//            println("NOTIFY RECYCLER")
            holder.checkBox.isChecked = selectors[holder.adapterPosition].isSelected
            holder.itemView.setBackgroundColor(if (selector.isSelected) Color.LTGRAY else Color.WHITE)
//            println("holder.checkBox.isChecked: ${holder.checkBox.isChecked}")
//            println("___________________________")
        }
    }

    override fun getItemCount(): Int {

        return selectors.size
    }
    class SongSelectorViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title: TextView = view.findViewById(R.id.songSelectorItemTvTitle)
        val artist: TextView = view.findViewById(R.id.songSelectorItemTvArtist)
        val duration: TextView = view.findViewById(R.id.songSelectorItemTvDuration)
        val checkBox: CheckBox = view.findViewById(R.id.songSelectorItemCheckBox)
    }
}