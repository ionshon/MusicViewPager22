package com.inu.musicviewpager2.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.constant.PendinIntent.intent
import com.inu.musicviewpager2.service.ForegroundService.Companion.isServiceRunning

class MyWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    private val TAG = "MyWorker"
    override fun doWork(): Result {
//        Log.d(TAG, "doWork called for: " + this.id)
//        Log.d(TAG, "Service Running 1: " + isServiceRunning)
        if (!isServiceRunning!!) {
            ContextCompat.startForegroundService(context, intent!!)
//            Log.d(TAG, "starting service from doWork")
            intent = Intent(this.context, ForegroundService::class.java)
            intent?.action = MusicConstants.ACTION.START_ACTION
//            Log.d(TAG, "Service Running 2: " + isServiceRunning)
        }
        return Result.success()
    }

    override fun onStopped() {
        Log.d(TAG, "onStopped called for: " + this.id)
        super.onStopped()
    }
}