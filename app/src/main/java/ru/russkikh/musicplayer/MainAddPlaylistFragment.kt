package ru.russkikh.musicplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ru.russkikh.musicplayer.songlist.ListSongSelectorAdapter
import ru.russkikh.musicplayer.utils.allSongs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainAddPlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainAddPlaylistFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_add_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        childFragmentManager.setFragmentResultListener("key", this) { key, bundle ->
            val result = bundle.get("bundleKey")
        }




        super.onViewCreated(view, savedInstanceState)
        val cancelBtn = view.findViewById<Button>(R.id.addPlaylistBtnCancel)
        val okBtn = view.findViewById<Button>(R.id.addPlaylistBtnOk)
        val editText = view.findViewById<EditText>(R.id.addPlaylistEtPlaylistName)
        val mainActivity = (activity as MainActivity)

        cancelBtn.setOnClickListener {
            (activity as MainActivity).setMusicState()
        }
        okBtn.setOnClickListener {
            val selectors =
                ((activity as MainActivity).binding.mainSongList.adapter as ListSongSelectorAdapter).selectors
            val selected = selectors.filter { it.isSelected }.map { it.song }.toList()

            for (i in 0..selected.lastIndex) selected[i].index = i

            if (editText.text.toString() == allSongs) {
                Toast.makeText(
                    (activity as MainActivity),
                    "$allSongs is a reserved name choose another playlist name",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selected.isEmpty()) {
                Toast.makeText(
                    (activity as MainActivity),
                    "Add at least one song to your playlist",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editText.text.isBlank()) {
                Toast.makeText(
                    (activity as MainActivity),
                    "Add a name to your playlist",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                println("Playlist ${editText.text} with ${selected.size} songs added: ${selected.map { it.id }}")
                mainActivity.playlists[editText.text.toString()] = selected
                (activity as MainActivity).setMusicState()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainAddPlaylistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainAddPlaylistFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}