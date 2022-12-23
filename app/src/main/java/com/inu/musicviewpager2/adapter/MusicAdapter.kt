package com.inu.musicviewpager2.adapter

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_GENRE
import android.opengl.Visibility
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
import com.inu.musicviewpager2.model.MusicDevice.deviceMusics
import com.inu.musicviewpager2.model.MusicDevice.isRadioOn
import com.inu.musicviewpager2.model.MusicDevice.lyric
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.model.MusicDevice.song
import com.inu.musicviewpager2.model.MusicDevice.titleDetail
import com.inu.musicviewpager2.module.GlideApp
import com.inu.musicviewpager2.service.ForegroundService
import com.inu.musicviewpager2.util.BubbleAdapter
import com.inu.musicviewpager2.util.MyApplication
import com.mpatric.mp3agic.Mp3File
import java.text.SimpleDateFormat


class MusicAdapter : RecyclerView.Adapter<MusicAdapter.Holder>(), BubbleAdapter {
    val resId = R.drawable.outline_music_note_24
    private var intent: Intent? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(view)
    }

    companion object {
        var oldPosition = 0
        var currentSongID = 0
        var index: Int = -1 // 초기 라디오실행시 -1 필요
        var searching = false
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val music = musicList[position]
        holder.setMusic(music)
        holder.binding.itemMain.setOnClickListener(View.OnClickListener{ v ->

            isRadioOn = false
            index = position
            song = music
//            Log.d("mp3플레이:","song=> ${song!!.title}")

            lyric = try {
                val mp3file= Mp3File(music.path)
                val id3v2Tag = mp3file.id3v2Tag
                id3v2Tag.lyrics
            } catch (e: Exception){
                Toast.makeText(MyApplication.applicationContext(), "mp3 lyric error", Toast.LENGTH_SHORT).show()
                "\n\n     error!!     "
            } finally {
            }

            dataSource = music.path
            when(ForegroundService.state) {
                MusicConstants.STATE_SERVICE.NOT_INIT -> {
                    musicList[position].isSelected = true
//                    Log.d("mp3플레이:","position=> $position, oldPosition=> ${oldPosition}")
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
//                    holder.binding.imageViewIsplay.visibility = View.VISIBLE
                }

                MusicConstants.STATE_SERVICE.PREPARE, MusicConstants.STATE_SERVICE.PLAY -> {

                    if (titleDetail != music.title) { // 다른 곡 선택 플레이
                        if (!searching) {
                            musicList[oldPosition].isSelected = false
                        }
                        searching = false
//                        Log.d("mp3플레이:","position=> $position, oldPosition=> ${oldPosition}")
                        musicList[position].isSelected = true
//                        Log.d("mp3플레이:","position=> $position, oldPosition=> ${oldPosition}")

                        lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                        val lPendingPlayIntent = PendingIntent.getService(v.context, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPlayIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                        MusicDevice.titleMain = "Music"
                        MusicDevice.titleDetail = music.title

//                        holder.binding.imageViewIsplay.visibility = View.VISIBLE

                    } else { // 같은 곡 멈춤
                        lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
                        val lPendingPauseIntent = PendingIntent.getService(v.context,0, lPauseIntent,PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPauseIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                        holder.binding.imageViewIsplay.visibility = View.INVISIBLE
                        musicList[position].isSelected = false
                        musicList[oldPosition].isSelected = false
                    }
                }

                MusicConstants.STATE_SERVICE.PAUSE -> {
                    /*if (!isInternetAvailable(v.context)) {
                        showError(v)
                        return@OnClickListener
                    }*/
                    if (titleDetail == music.title) {
                        lPlayIntent.action = MusicConstants.ACTION.REPLAY_ACTION
                        val lPendingPlayIntent = PendingIntent.getService(v.context,0,lPlayIntent,PendingIntent.FLAG_IMMUTABLE)
                        try {
                            lPendingPlayIntent.send()
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }
                        musicList[position].isSelected = true
//                        Log.d("mp3플레이:","position=> $position, oldPosition=> ${oldPosition}")
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
                        musicList[position].isSelected = true
                        musicList[oldPosition].isSelected = false
//                        Log.d("mp3플레이:","position=> $position, oldPosition=> ${oldPosition}")
                    }
                    MusicDevice.titleMain = "Music"
                    MusicDevice.titleDetail = music.title
//                    Log.d("kkkkk in adapter3: ", "${music.id}, ${music.title}")
                }
            }
            updateItem()
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
            GlideApp.with(binding.root.context)
                .load(music.albumUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(resId)
                .error(resId)
//                .thumbnail(GlideApp.with(binding.root.context).load(resId).override(100, 100))
                .into(binding.imageAlbum)


            GlideApp.with(binding.root.context)
                .load(R.raw.bounc)
                .into(binding.imageViewIsplay)

            binding.imageViewIsplay.visibility = View.INVISIBLE
            if (music.isSelected) {
                binding.imageViewIsplay.visibility = View.VISIBLE
            }
//            GlideApp.with(binding.root.context)
//                .load(R.raw.bounc)
//                .into(binding.imageViewIsplay)
//            binding.imageViewIsplay.visibility = View.INVISIBLE
//            Log.d("adpter : ", "${music.albumUri}")
        }
    }
    private fun showError(v: View) {
        Snackbar.make(v, "No internet", Snackbar.LENGTH_LONG).show()
    }

    fun updateList() {
        notifyDataSetChanged() // 리스트 변경을 adapter에 알림
    }
    fun updateItem() {
//        Log.d("mp3플레이(updateitem):","position=> $index, oldPosition=> ${oldPosition}")
        var temp = oldPosition
        notifyItemChanged(temp)
        notifyItemChanged(oldPosition)
        notifyItemChanged(index)
        oldPosition = index
    }

    override fun getBubbleItem(adapterPosition: Int): String {
        return "$adapterPosition"
    }
}

