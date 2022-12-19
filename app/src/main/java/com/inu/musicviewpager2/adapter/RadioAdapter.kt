package com.inu.musicviewpager2.adapter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.constant.PendinIntent
import com.inu.musicviewpager2.constant.PendinIntent.intent
import com.inu.musicviewpager2.model.MusicDevice
import com.inu.musicviewpager2.model.MusicDevice.dataSource
import com.inu.musicviewpager2.model.MusicDevice.imageRadioPlaySource
import com.inu.musicviewpager2.model.Radio
import com.inu.musicviewpager2.module.GlideApp
import com.inu.musicviewpager2.service.ForegroundService
import com.inu.musicviewpager2.util.NetworkHelper
import com.inu.musicviewpager2.util.NetworkHelper.isInternetAvailable
import com.inu.musicviewpager2.util.SetStreamUrl

class RadioAdapter: RecyclerView.Adapter<RadioAdapter.GridAdapter>(){
    class GridAdapter(val layout: View): RecyclerView.ViewHolder(layout)

    var listData = mutableListOf<Radio>()
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  GridAdapter {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.radio_item, parent, false)
        return GridAdapter(view)
    }

    private fun showError(v: View) {
//        Snackbar.make(v, "No internet", Snackbar.LENGTH_LONG).show()
        val snackBar = Snackbar.make(v, "인터넷연결이 안되어 있습니다.", Snackbar.LENGTH_SHORT)
        val snackBarView = snackBar.view
        val snackBarLayout = snackBarView.layoutParams as FrameLayout.LayoutParams
        snackBarLayout.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER // 레이아웃 위치 조정
//                snackBarLayout.width = 800 // 너비 조정
//                snackBarLayout.height = 500 // 높이 조정
        snackBar.show()
    }
    override fun onBindViewHolder(holder: GridAdapter, position: Int) {

        val data = listData[position]
        var radioImageView = holder.layout.findViewById<ImageView>(R.id.image_radio_item)

        if (data.title != "") {
            GlideApp.with(holder.itemView.context)
                .load(data.radioAddr)
                .into(radioImageView)
        } else {
            GlideApp.with(holder.itemView.context)
                .load(R.drawable.ic_clear)
                .into(radioImageView)
        }
        holder.itemView.setOnClickListener { v ->
            if (!isInternetAvailable(v.context)) {
//                        Log.d("kkkkk in isInternetAvailable:", "${intent?.action},, ${music.id}, ${music.title}")
                showError(v)
                return@setOnClickListener
            }

            if (data.title == "") {
                SetStreamUrl().setStreamUrl(MusicConstants.RADIO_ADDR.radioAddrList[position])
                Log.d("setOnClickListener","${MusicConstants.RADIO_ADDR.radioAddrList[position]}")
            } else {

                when (ForegroundService.state) {
                    MusicConstants.STATE_SERVICE.NOT_INIT -> {
                       /* if (!NetworkHelper.isInternetAvailable(v.context)) {
                            Snackbar.make(v, "No internet", Snackbar.LENGTH_LONG).show()
                            return@setOnClickListener
                        }*/
                        intent = Intent(v.context, ForegroundService::class.java)
                        intent?.action = MusicConstants.ACTION.START_ACTION
                        dataSource = data.title
                        MusicDevice.titleMain = "Radio"
                        MusicDevice.titleDetail = data.detail
                        MusicDevice.imageRadioPlaySource = data.radioAddr
                        ContextCompat.startForegroundService(v.context, intent!!)
                    }

                    MusicConstants.STATE_SERVICE.PREPARE, MusicConstants.STATE_SERVICE.PLAY -> {
                        if (MusicDevice.titleDetail != data.detail) {
                            PendinIntent.lPauseIntent.action = MusicConstants.ACTION.PLAY_ACTION
                            dataSource = data.title
                            MusicDevice.titleMain = "Radio"
                            MusicDevice.titleDetail = data.detail
                            MusicDevice.imageRadioPlaySource = data.radioAddr
                            val lPendingPauseIntent = PendingIntent.getService(
                                v.context,
                                0,
                                PendinIntent.lPauseIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            try {
                                lPendingPauseIntent.send()
                            } catch (e: PendingIntent.CanceledException) {
                                e.printStackTrace()
                            }
                        } else {
                            PendinIntent.lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
                            val lPendingPauseIntent = PendingIntent.getService(
                                v.context, 0,
                                PendinIntent.lPauseIntent, PendingIntent.FLAG_IMMUTABLE
                            )
                            try {
                                lPendingPauseIntent.send()
                            } catch (e: PendingIntent.CanceledException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    MusicConstants.STATE_SERVICE.PAUSE -> {
                        if (!NetworkHelper.isInternetAvailable(v.context)) {
                            showError(v)
                            return@setOnClickListener
                        }
                        if (MusicDevice.titleDetail != data.detail) { //  액션플에이
//                    Log.d("radoiTest","정지상태, 방송 재목 다를때, ${MusicDevice.titleDetail}, ${data.detail}")
                            PendinIntent.lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                            dataSource = data.title
                            MusicDevice.titleMain = "Radio"
                            MusicDevice.titleDetail = data.detail
                            imageRadioPlaySource = data.radioAddr
                            val lPendingPlayIntent = PendingIntent.getService(
                                v.context,
                                0,
                                PendinIntent.lPlayIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            try {
                                lPendingPlayIntent.send()
                            } catch (e: PendingIntent.CanceledException) {
                                e.printStackTrace()
                            }
                        } else { // 자체 포즈
//                    Log.d("radoiTest","정지상태, 라디오 일때")
                            PendinIntent.lReplayIntent.action = MusicConstants.ACTION.REPLAY_ACTION
                            val lPendingPlayIntent = PendingIntent.getService(
                                v.context,
                                0,
                                PendinIntent.lReplayIntent,
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            try {
                                lPendingPlayIntent.send()
                            } catch (e: PendingIntent.CanceledException) {
                                e.printStackTrace()
                            }

                        }
                    }
                } // when
            }
        } //holder.itemView.setOnClickListener

    }

    override fun getItemCount(): Int {
        return listData.size
    }
}
