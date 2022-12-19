package com.inu.musicviewpager2.constant

import android.content.Intent
import com.inu.musicviewpager2.service.ForegroundService
import com.inu.musicviewpager2.util.MyApplication

object PendinIntent {
    val lPauseIntent = Intent(MyApplication.applicationContext(), ForegroundService::class.java)
    val lPlayIntent = Intent(MyApplication.applicationContext(), ForegroundService::class.java)
    val lReplayIntent = Intent(MyApplication.applicationContext(), ForegroundService::class.java)

    var intent: Intent? = null

}