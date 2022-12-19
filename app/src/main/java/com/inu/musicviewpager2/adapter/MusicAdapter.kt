package com.inu.musicviewpager2.adapter

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_GENRE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.with
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.constant.PendinIntent.lPauseIntent
import com.inu.musicviewpager2.constant.PendinIntent.lPlayIntent
import com.inu.musicviewpager2.databinding.ItemLayoutBinding
import com.inu.musicviewpager2.model.Music
import com.inu.musicviewpager2.model.MusicDevice
import com.inu.musicviewpager2.model.MusicDevice.dataSource
import com.inu.musicviewpager2.model.MusicDevice.index
import com.inu.musicviewpager2.model.MusicDevice.lyric
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.model.MusicDevice.song
import com.inu.musicviewpager2.model.MusicDevice.titleDetail
import com.inu.musicviewpager2.service.ForegroundService
import com.inu.musicviewpager2.util.BubbleAdapter
import com.inu.musicviewpager2.util.MyApplication
import com.inu.musicviewpager2.util.NetworkHelper.isInternetAvailable
import com.mpatric.mp3agic.Mp3File
import dlna.model.UpnpDevice
import java.text.SimpleDateFormat


class MusicAdapter : RecyclerView.Adapter<MusicAdapter.Holder>(), BubbleAdapter {

    val resId = R.drawable.outline_music_note_24
    private var intent: Intent? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val music = musicList[position]
        holder.setMusic(music)
        holder.binding.itemMain.setOnClickListener(View.OnClickListener{ v ->

/*
            System.out.println(
                "Length of this mp3 is: " + mp3file.getLengthInSeconds().toString() + " seconds"
            )
            System.out.println(
                "musicadapter: Bitrate: " + mp3file.getBitrate()
                    .toString() + " kbps " + if (mp3file.isVbr()) "(VBR)" else "(CBR)"
            )
            System.out.println("musicadapter: Sample rate: " + mp3file.getSampleRate().toString() + " Hz")
            println("musicadapter: Has ID3v1 tag?: " + if (mp3file.hasId3v1Tag()) "YES" else "NO")
            println("musicadapter: Has ID3v2 tag?: " + if (mp3file.hasId3v2Tag()) "YES" else "NO")
            println("musicadapter: Has custom tag?: " + if (mp3file.hasCustomTag()) "YES" else "NO")
            Log.d("musicadapter","position=> $mp3file")
            println("musicadapter: Track: " + id3v2Tag.track)
            println("musicadapter: Artist: " + id3v2Tag.artist)
            println("musicadapter: Title: " + id3v2Tag.title)
            println("musicadapter: Album: " + id3v2Tag.album)
            println("musicadapter: Year: " + id3v2Tag.year)
            println("musicadapter: Genre: " + id3v2Tag.genre + " (" + id3v2Tag.genreDescription + ")")
            println("musicadapter: Comment: " + id3v2Tag.comment)
            Log.d("musicadapter:"," Lyrics:  + ${id3v2Tag.lyrics}")
            println("musicadapter: Composer: " + id3v2Tag.composer)
            println("musicadapter: Publisher: " + id3v2Tag.publisher)
            println("musicadapter: Original artist: " + id3v2Tag.originalArtist)
            println("musicadapter: Album artist: " + id3v2Tag.albumArtist)
            println("musicadapter: Copyright: " + id3v2Tag.copyright)
            println("musicadapter: URL: " + id3v2Tag.url)
            println("musicadapter: Encoder: " + id3v2Tag.encoder)
            val albumImageData = id3v2Tag.albumImage
            if (albumImageData != null) {
                println("musicadapter: Have album image data, length: " + albumImageData.size + " bytes")
                println("musicadapter: Album image mime type: " + id3v2Tag.albumImageMimeType)
            }*/

//            Log.d("genreData:", "${MusicProvider.genreList[position]}")
            index = position
            song = music
            try {
                val mp3file= Mp3File(music.path)
                val id3v2Tag = mp3file.id3v2Tag
                lyric = id3v2Tag.lyrics
            } catch (e: Exception){
                    Toast.makeText(MyApplication.applicationContext(), "mp3 lyric error", Toast.LENGTH_SHORT).show()
                }
            dataSource = music.path
            when(ForegroundService.state) {
                MusicConstants.STATE_SERVICE.NOT_INIT -> {
                   /* if (!isInternetAvailable(v.context)) {
//                        Log.d("kkkkk in isInternetAvailable:", "${intent?.action},, ${music.id}, ${music.title}")
                        showError(v)
                        Snackbar.make(v, "No internet", Snackbar.LENGTH_LONG).show()
                        return@OnClickListener
                    }*/
                    intent = Intent(v.context, ForegroundService::class.java)
                    intent?.action = MusicConstants.ACTION.START_ACTION
                    MusicDevice.titleMain = "Music"
                    titleDetail = music.title
                    ContextCompat.startForegroundService(v.context, intent!!)
                }

                MusicConstants.STATE_SERVICE.PREPARE, MusicConstants.STATE_SERVICE.PLAY -> {
                    Log.d("mp3플레이:","titleDetail=> $titleDetail, music.title=> ${music.title}")
                    if (titleDetail != music.title) { // 다른 곡 선택 플레이
                        lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                        val lPendingPlayIntent = PendingIntent.getService(v.context, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPlayIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                        MusicDevice.titleMain = "Music"
                        MusicDevice.titleDetail = music.title
                    } else { // 같은 곡 멈춤
                        lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
                        val lPendingPauseIntent = PendingIntent.getService(v.context,0, lPauseIntent,PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPauseIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
//                        MusicDevice.titleMain = "Music"
//                        MusicDevice.titleDetail = music.title
                    }
//                    Log.d("kkkkk in adapter2: ", "${titleDetail}")
                }

                MusicConstants.STATE_SERVICE.PAUSE -> {
                    if (!isInternetAvailable(v.context)) {
                        showError(v)
                        return@OnClickListener
                    }
                    if (titleDetail == music.title) {
                        lPlayIntent.action = MusicConstants.ACTION.REPLAY_ACTION
                        val lPendingPlayIntent = PendingIntent.getService(v.context,0,lPlayIntent,PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPlayIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                    } else {
                        lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                        val lPendingPlayIntent = PendingIntent.getService(
                            v.context,
                            0,
                            lPlayIntent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                        try {
                            lPendingPlayIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                    }
                    MusicDevice.titleMain = "Music"
                    MusicDevice.titleDetail = music.title
//                    Log.d("kkkkk in adapter3: ", "${music.id}, ${music.title}")
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    inner class Holder(val binding: ItemLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun setMusic(music: Music) {
            with(binding) {
                textTitle.text = music.title
                textAtist.text = music.artist
                textViewGenre.text = music.genre
                val sdf = SimpleDateFormat("mm:ss")
                textDuration.text = sdf.format(music.duration)
            }

//                1. 로드할 대상 Uri    2. 입력될 이미지뷰
            with(binding.root.context)
                .load(music.albumUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(resId)
                .into(binding.imageAlbum)
//            Log.d("adpter : ", "${music.albumUri}")
        }
    }
    private fun showError(v: View) {
        Snackbar.make(v, "No internet", Snackbar.LENGTH_LONG).show()
    }

    fun updateList() {
        notifyDataSetChanged() // 리스트 변경을 adapter에 알림
    }

    override fun getBubbleItem(adapterPosition: Int): String {
        return "$adapterPosition"
    }
}

