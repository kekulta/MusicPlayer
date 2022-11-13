package ru.russkikh.musicplayer

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.russkikh.musicplayer.databinding.ActivityMainBinding
import ru.russkikh.musicplayer.songlist.ListSongAdapter
import ru.russkikh.musicplayer.songlist.ListSongSelectorAdapter
import ru.russkikh.musicplayer.utils.*



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val handler = Handler(Looper.getMainLooper())
    val mainPlayerControllerFragment = MainPlayerControllerFragment()
    val controllerSync: Runnable = object : Runnable {

        override fun run() {
//            print("Controller synced at time of ${player!!.currentPosition} millis. ")
//
//            println("Current SDK >= Q: ${Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q}")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                println("Sync callbacks: ${handler.hasCallbacks(this)}")
//            }
            if (currentTrack.state != SongState.PLAY) return

            mainPlayerControllerFragment.binding.controllerSeekBar.progress =
                player!!.currentPosition / 1000

            mainPlayerControllerFragment.binding.controllerTvCurrentTime.text =
                millisToClock(player!!.currentPosition)
            handler.postDelayed(this, 1000)

        }
    }
    //val playlists: MutableMap<String, List<Song>> = HashMap()
    lateinit var playlists: PlaylistsStorage

    var loadedPlaylist = ""
    var currentPlaylist = ""
    var currentTrack = Track()
    var player: MediaPlayer? = null
    private val mainAddPlaylistFragment = MainAddPlaylistFragment()
    private var currentState = State.PLAY_MUSIC

    private lateinit var db: SQLiteDatabase
    private lateinit var storage: MusicStore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val helper = DbHelper(this)
        SongSource.init(contentResolver)
        db = helper.writableDatabase
        storage = MusicStore(db)
        playlists = PlaylistsStorage(storage)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainSongList.layoutManager = LinearLayoutManager(this)

        currentTrack.setOnSongUpdatedListener {
            onSongUpdatedListener()
        }

        currentTrack.setOnStateUpdatedListener {
            onStateUpdatedListener()
        }

        binding.mainButtonSearch.setOnClickListener {
            mainSearch()
        }

        setMusicState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mainMenuAddPlaylist -> {
                if (currentState == State.ADD_PLAYLIST) return true
                if (playlists[allSongs]?.isEmpty() != false) {
                    Toast.makeText(
                        this,
                        "no songs loaded, click search to load songs",
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                }
                loadedPlaylist = allSongs
                setAddState()
                return true
            }
            R.id.mainMenuLoadPlaylist -> {
                AlertDialog.Builder(this).apply {
                    setTitle("choose playlist to load")
                    println("Playlists number: ${playlists.keys.size}")
                    val keys = playlists.keys.toTypedArray().sortedArray()
                    setItems(keys) { _, i ->
                        when (currentState) {
                            State.PLAY_MUSIC -> {
                                currentPlaylist = keys[i]
                                setMusicState()
                            }
                            State.ADD_PLAYLIST -> {
                                loadedPlaylist = keys[i]
                                setAddState((binding.mainSongList.adapter as ListSongSelectorAdapter).selectors)
                            }
                        }
                    }
                    setNegativeButton("cancel", null)
                    show()
                }
                return true
            }
            R.id.mainMenuDeletePlaylist -> {
                val keys = playlists.keys.filter { it != allSongs }.toTypedArray()
                AlertDialog.Builder(this).apply {
                    setTitle("choose playlist to delete")
                    setItems(keys) { _, i ->
                        if (currentPlaylist == keys[i]) currentPlaylist = allSongs
                        if (currentState == State.PLAY_MUSIC) setMusicState()
                        if (currentState == State.ADD_PLAYLIST) {
                            loadedPlaylist = allSongs
                            setAddState()
                        }
                        playlists.remove(keys[i])
                        println("Playlist ${keys[i]} was deleted.")
                    }
                    setNegativeButton("cancel", null)
                    show()
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        println("Permissions requested: $permissions")
        when(requestCode) {
            MEDIA_REQUEST_CODE -> {
                print("Was permission granted: ")
                if (grantResults.isNotEmpty() ) {
                    println(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Songs cannot be loaded without permission", Toast.LENGTH_SHORT).show()
                    }else{
                        mainSearch()
                    }
                }


            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        storage.close()
        db.close()
    }

    fun setMusicState() {
        println("Player is in music state now.")
        currentState = State.PLAY_MUSIC
        val currPlaylist = playlists[currentPlaylist] ?: listOf()

        if (currPlaylist.isNotEmpty()) {
            if (currentTrack.song == null) {
                currentTrack.song = currPlaylist[0]
                currentTrack.stop()
            } else if (!currPlaylist.contains(currentTrack.song)) {
                currentTrack.song = currPlaylist[0]
                currentTrack.stop()
            } else {
                currentTrack.song!!.index = currPlaylist.indexOf(currentTrack.song)
            }


        }

        binding.mainSongList.adapter = ListSongAdapter(currPlaylist, this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, mainPlayerControllerFragment).commit()



    }

    fun setAddState(selector: List<SongSelector> = listOf()) {
        println("Player is in adding state now.")
        currentState = State.ADD_PLAYLIST
        binding.mainSongList.adapter = ListSongSelectorAdapter(
            playListToSelector(
                playlists[loadedPlaylist] ?: listOf(),
                selector
            )
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, mainAddPlaylistFragment).commit()

    }

    fun startSync() {
        handler.post(controllerSync)
        println("Sync started")
    }

    private fun runtimePermissions() {
        println("Permission check, isPermission granted: ${ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED}")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                println("Request for permission")
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MEDIA_REQUEST_CODE
                )
            }

        }
    }

    private fun onSongUpdatedListener(){
        if (currentTrack.song != null) {
            println("Track updated")
            val externalUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            println(currentTrack.song?.id)
            val songUri = ContentUris.withAppendedId(externalUri, currentTrack.song?.id ?: 0)
            player?.release()
            player = MediaPlayer.create(this, songUri)
            player!!.setOnCompletionListener {
                currentTrack.stop()
            }

            mainPlayerControllerFragment.binding.controllerSeekBar.max =
                (currentTrack.song!!.duration) / 1000
            mainPlayerControllerFragment.binding.controllerTvTotalTime.text =
                millisToClock(currentTrack.song!!.duration)
        }
    }

    private fun onStateUpdatedListener(){
        if (currentTrack.song != null) {
            println("State updated, current song state: ${currentTrack.state}, current song: ${currentTrack.song}")

            binding.mainSongList.adapter?.notifyItemChanged(currentTrack.song!!.index)

            when (currentTrack.state) {
                SongState.PAUSE -> {
                    player!!.pause()
                }
                SongState.STOP -> {
                    player!!.stop()
                    player!!.prepare()
                    player!!.seekTo(0)
                    mainPlayerControllerFragment.binding.controllerSeekBar.progress = 0
                }
                SongState.PLAY -> {
                    player!!.start()
                    startSync()
                }
            }
        }
    }

    private fun mainSearch() {
        runtimePermissions()



        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            //playlists[allSongs] = (0..9).map { Song(id = it+1, index = it) }
            playlists[allSongs] = SongSource.findSongs()

            if (currentState == State.PLAY_MUSIC) {
                currentPlaylist = allSongs
            }
            when (currentState) {
                State.PLAY_MUSIC -> setMusicState()
                State.ADD_PLAYLIST -> {
                    loadedPlaylist = allSongs
                    setAddState((binding.mainSongList.adapter as ListSongSelectorAdapter).selectors)
                }
            }
        }
    }
}