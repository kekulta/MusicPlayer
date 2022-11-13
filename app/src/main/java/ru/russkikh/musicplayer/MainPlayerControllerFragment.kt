package ru.russkikh.musicplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.russkikh.musicplayer.databinding.FragmentMainPlayerControllerBinding
import ru.russkikh.musicplayer.songlist.ListSongAdapter
import ru.russkikh.musicplayer.utils.SeekbarListener
import ru.russkikh.musicplayer.utils.SongState
import ru.russkikh.musicplayer.utils.millisToClock

class MainPlayerControllerFragment : Fragment() {

    lateinit var binding: FragmentMainPlayerControllerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainPlayerControllerBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = (activity as MainActivity)
        val currentTrack = mainActivity.currentTrack

        binding.controllerSeekBar.setOnSeekBarChangeListener(SeekbarListener(mainActivity))

        println("ControllerFragment view created")
        println("Current track duration: ${currentTrack.song?.duration}")
        binding.controllerTvTotalTime.text = millisToClock(currentTrack.song?.duration ?: 0)
        binding.controllerSeekBar.max = (currentTrack.song?.duration ?: (1000 * 100)) / 1000

        binding.controllerBtnPlayPause.setOnClickListener {
            val main = activity as MainActivity



            println("____________controllerPlayPauseBtn clicked_________________")
            println("currentTrack: $currentTrack")
            println()
            for (i in 0..(main.playlists["All Songs"]?.size ?: 0)) {
                println(
                    "View holder for adapter position $i:${
                        if (main.binding.mainSongList.findViewHolderForAdapterPosition(
                                i
                            ) != null
                        ) (main.binding.mainSongList.findViewHolderForAdapterPosition(
                            i
                        ) as ListSongAdapter.SongViewHolder).title.text else null
                    }"
                )
            }
            if (currentTrack.song != null) {


                println("________________________________")

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
            }
        }
        binding.controllerBtnStop.setOnClickListener {
            if (currentTrack.song != null) {
                val main = activity as MainActivity
                currentTrack.stop()
                for (i in 0..(main.playlists["All Songs"]?.size ?: 0)) {
                    println(
                        "View holder for adapter position $i:${
                            if (main.binding.mainSongList.findViewHolderForAdapterPosition(
                                    i
                                ) != null
                            ) (main.binding.mainSongList.findViewHolderForAdapterPosition(
                                i
                            ) as ListSongAdapter.SongViewHolder).title.text else null
                        }"
                    )
                }
            }
        }
    }
}