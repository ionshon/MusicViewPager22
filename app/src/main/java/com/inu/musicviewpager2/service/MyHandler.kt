package com.inu.musicviewpager2.service

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log

class MyHandler: Handler(Looper.getMainLooper()) {
   /* companion object {
        const val TAG = "MyHandler"
        const val MSG_DO_SOMETHING1 = 1
        const val MSG_DO_SOMETHING2 = 2
    }
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_DO_SOMETHING1 -> {
                Log.d("playFrag1", "Do something1")
            }
            MSG_DO_SOMETHING2 -> {
                Log.d("playFrag1", "Do something4, arg1: ${msg.arg1}," +
                        " arg2: ${msg.arg2}, obj: ${msg.obj}")
            }
        }
    }*/
}