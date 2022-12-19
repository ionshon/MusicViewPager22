package com.inu.musicviewpager2.service

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.USAGE_MEDIA
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.inu.musicviewpager2.MainActivity
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.constant.PendinIntent.lPauseIntent
import com.inu.musicviewpager2.constant.PendinIntent.intent
import com.inu.musicviewpager2.constant.PendinIntent.lPlayIntent
import com.inu.musicviewpager2.constant.PendinIntent.lReplayIntent
import com.inu.musicviewpager2.fragments.FragmentPlay.Companion.realTotalLen
import com.inu.musicviewpager2.fragments.FragmentPlay.Companion.totalLen
import com.inu.musicviewpager2.model.MusicDevice.dataSource
import com.inu.musicviewpager2.model.MusicDevice.index
import com.inu.musicviewpager2.model.MusicDevice.lyric
import com.inu.musicviewpager2.model.MusicDevice.mPlayer
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.model.MusicDevice.song
import com.inu.musicviewpager2.model.MusicDevice.titleDetail
import com.inu.musicviewpager2.model.MusicDevice.titleMain
import com.mpatric.mp3agic.Mp3File


class ForegroundService : Service(), MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

//    var isServiceRunning : Boolean? = null

    private val mLock = Any()
    private val mHandler = Handler(Looper.getMainLooper())//Handler()

    var t = 0
    val broadCastThread = Thread(Runnable {
        while (isServiceRunning as Boolean) {
//            for (i in 0..t) {
                val intent = Intent()
                intent.action = "test"
                intent.putExtra("value", t)
                sendBroadcast(intent)
//                Log.d("isServiceRunning broadCast", "$isServiceRunning")
                Thread.sleep(1000)
                t += 1
            }
//        }
    })

//    private var mUriRadio: Uri? = null
    private var notification: Notification? = null
    private var mNotificationManager: NotificationManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    private var mWiFiLock: WifiManager.WifiLock? = null
    /* private val mTimerUpdateHandler = Handler(Looper.getMainLooper()) //Handler()
     private val mTimerUpdateRunnable: Runnable = object : Runnable {
         override fun run() {
             mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())
             mTimerUpdateHandler.postDelayed(this, MusicConstants.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE)
         }
     }*/

    var isClickedStop = false
    var isClickedPause = false

    override fun onCreate() {
        super.onCreate()
        Log.d("MusicViewPager22", "onCreate()")
        state = MusicConstants.STATE_SERVICE.NOT_INIT
        mNotificationManager = getSystemService(NotificationManager::class.java) //Context.NOTIFICATION_SERVICE) as NotificationManager
        isServiceRunning = true
       /* if (broadCastThread.isAlive) {
            broadCastThread.interrupt()
        }*/
//        broadCastThread.start() // 브로드캐스트, 수신 쓰레드 지우고...
//        mUriRadio = MusicConstants.RADIO_ADDR.mUriRadioDefault
//        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.file)
    }

    override fun startService(service: Intent?): ComponentName? {
        Log.d("MusicViewPager22", "startService() called")
        if (!isServiceRunning!!) {
            val serviceIntent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        return super.startService(service)
    }

    private fun createNotificationChannel() {
        /*val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()*/
        val serviceChannel = NotificationChannel(FOREGROUND_CHANNEL_ID, "My_FOREGROUND", NotificationManager.IMPORTANCE_HIGH)
        serviceChannel.setSound(null, null) //soundUri, audioAttributes)
//        serviceChannel.enableVibration(false)
        mNotificationManager?.createNotificationChannel(serviceChannel)
    }

    private fun prepareNoti(): Notification {
        createNotificationChannel()

        intent = Intent(this, MainActivity::class.java).also {
            it.action = Intent.ACTION_MAIN
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        val mainPendingIntent = PendingIntent.getActivity(this,0, intent,  PendingIntent.FLAG_IMMUTABLE)

        val notificationIntent = Intent(this, ForegroundService::class.java)
        notificationIntent.action = MusicConstants.ACTION.MAIN_ACTION
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

//        val lPlayIntent = Intent(this, ForegroundService::class.java)
        lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
        val lPendingPlayIntent = PendingIntent.getService(this, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
        lReplayIntent.action = MusicConstants.ACTION.REPLAY_ACTION
        val lPendingReplayIntent = PendingIntent.getService(this, 0, lReplayIntent, PendingIntent.FLAG_IMMUTABLE)

        lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
        val lPendingPauseIntent = PendingIntent.getService(this, 0, lPauseIntent, PendingIntent.FLAG_IMMUTABLE)

        val lStopIntent = Intent(this, ForegroundService::class.java)
        lStopIntent.action = MusicConstants.ACTION.STOP_ACTION
        val lPendingStopIntent = PendingIntent.getService(this, 0, lStopIntent, PendingIntent.FLAG_IMMUTABLE)

        val lRemoteViews = RemoteViews(packageName, R.layout.radio_notification)
        lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_close_button, lPendingStopIntent)  // stop 인텐트 실행하라

        lRemoteViews.setOnClickPendingIntent(R.id.linearLayout, mainPendingIntent) // 앱 띄우기

        when(state) {
            MusicConstants.STATE_SERVICE.PAUSE -> {
                Log.d("PAUSE in Noti", "$state")
                lRemoteViews.setTextViewText(R.id.text_title1, titleMain)
                lRemoteViews.setTextViewText(R.id.text_title2, titleDetail)
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
                lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingReplayIntent)
                lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_play_arrow_white)
            }
            MusicConstants.STATE_SERVICE.PLAY -> {
                Log.d("PLAY in Noti", "$state")
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
                lRemoteViews.setTextViewText(R.id.text_title1, titleMain)
                lRemoteViews.setTextViewText(R.id.text_title2, titleDetail)
                lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingPauseIntent)
                lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_pause_white)
//                lRemoteViews.setTextViewText(R.id.textTitle, dataSource)
            }
            MusicConstants.STATE_SERVICE.PREPARE -> {
                Log.d("PREPARE in Noti", "$state")
                lRemoteViews.setTextViewText(R.id.text_title1, titleMain)
                lRemoteViews.setTextViewText(R.id.text_title2, titleDetail)
                lRemoteViews.setViewVisibility(R.id.ui_notification_progress_bar, View.INVISIBLE)
                lRemoteViews.setOnClickPendingIntent(R.id.ui_notification_player_button, lPendingPauseIntent)
                lRemoteViews.setImageViewResource(R.id.ui_notification_player_button, R.drawable.ic_pause_white)
            }
        }

        notification = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContent(lRemoteViews)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSound(null)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        return notification as Notification
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("MusicViewPager22", "onStartCommand()")
        Log.d("MusicViewPager22", "isServiceRunning=> ${isServiceRunning}")
        Log.d("MusicViewPager22", "broadCastThread=> ${broadCastThread.isAlive}")

        isServiceRunning = true
//        broadCastThread.start()
        when (intent?.action) {
            MusicConstants.ACTION.START_ACTION -> {
//                radioAddr = intent.getStringExtra("radio")
                Log.i(TAG, "Received start Intent, ${dataSource} ")
                state = MusicConstants.STATE_SERVICE.PREPARE
//                Log.d("kkkk in lremoteView:", "${bindingNoti?.textTitle2?.text}")
                startForeground(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())

                destroyPlayer()
                initPlayer()
                play()
            }

            MusicConstants.ACTION.PAUSE_ACTION -> {
                isClickedPause = true
                state = MusicConstants.STATE_SERVICE.PAUSE
                mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())
                Log.i(TAG,"Clicked pause0, $dataSource")
                mPlayer?.pause()
//                destroyPlayer()
//                mHandler.postDelayed(mDelayedShutdown, MusicConstants.DELAY_SHUTDOWN_FOREGROUND_SERVICE)
            }

            MusicConstants.ACTION.STOP_ACTION -> {
                isClickedStop = true
                Log.i(TAG, "Received stop Intent,  ${mPlayer?.isPlaying}")
                stopForeground(STOP_FOREGROUND_REMOVE)
                destroyPlayer()
                stopSelf()
                finish()
            }

            MusicConstants.ACTION.PLAY_ACTION -> {
//                play()
//                radioAddr = lPauseIntent.getStringExtra("radio")
                state = MusicConstants.STATE_SERVICE.PREPARE
                mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())
                Log.i(TAG, "Clicked play")

                destroyPlayer()
                initPlayer() // MediaPlayer 할당, 리스너 설정
                play() //
            }

            MusicConstants.ACTION.REPLAY_ACTION -> {
//                play()
//                radioAddr = lPauseIntent.getStringExtra("radio")
                state = MusicConstants.STATE_SERVICE.PREPARE
                mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())
                Log.i(TAG, "Clicked replay")

//                destroyPlayer()
//                initPlayer() // MediaPlayer 할당, 리스너 설정
//                play() //
                mPlayer?.start()
            }
        }
        return START_STICKY // super.onStartCommand(intent, flags, startId) //
    }

    private fun finish() {
        MainActivity().moveTaskToBack(true)						// 태스크를 백그라운드로 이동
        MainActivity().finishAndRemoveTask();					// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid())	// 앱 프로세스 종료
    }
    private fun destroyPlayer() {
        if (mPlayer != null) {
            try {
                mPlayer!!.reset()
                mPlayer!!.release()
                Log.d(TAG, "Player destroyed")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                Log.d(TAG, "Player nulled")
                mPlayer = null
            }
        }
        unlockWiFi()
        unlockCPU()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        isServiceRunning = false
//        stopForeground(true)
//        destroyPlayer()
        state = MusicConstants.STATE_SERVICE.NOT_INIT
        /*try {
            mTimerUpdateHandler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        // call MyReceiver which will restart this service via a worker
        if (!isClickedStop && !isClickedPause){
            val broadcastIntent = Intent(this, MyReceiver::class.java)
            sendBroadcast(broadcastIntent) // 여기가 시작점
        }

        super.onDestroy()
    }

    private fun initPlayer() {
        Log.d(TAG,"initPlayer() called")
        val aa = AudioAttributes.Builder()
        .setUsage(USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
        mPlayer = MediaPlayer()
        mPlayer!!.setAudioAttributes(aa)
        mPlayer!!.setOnErrorListener(this)
        mPlayer!!.setOnPreparedListener(this)
        mPlayer!!.setOnBufferingUpdateListener(this)
        mPlayer!!.setOnInfoListener { mp, what, extra ->
            Log.d(TAG, "Player onInfo(), what:$what, extra:$extra, 네트워크 단절시")
            if (what == 701) { // 네트워크 단절시 MediaPlayer is temporarily pausing playback internally in order to buffer more data.
                play()
            }
            false
        }
        lockWiFi()
        lockCPU()
    }

    private fun play() {
        try {
            mHandler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        synchronized(mLock) {
            try {
                if (mPlayer == null) {
                    Log.d("worker Play()", "mPlayer == null")
                    initPlayer() // 플레이어 할당, 리스너 설정
                }
                Log.d("play in service:","isClickedPause=> $isClickedPause")

//                if (!isClickedPause) {
                mPlayer?.reset()
                mPlayer?.setVolume(1.0f, 1.0f)
                Log.d(TAG, "dataSource in play(): $dataSource")
                mPlayer?.setDataSource(this, Uri.parse(dataSource))
//                }
                mPlayer?.prepareAsync()

//                    mPlayer?.start()

            } catch (e: Exception) {
                destroyPlayer()
                e.printStackTrace()
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {

        realTotalLen = mPlayer?.duration ?: 0
        totalLen = if (realTotalLen !=  -1) {
            mPlayer?.duration!!
        } else 9900000
        isAlbum = true

        state = MusicConstants.STATE_SERVICE.PLAY
        mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti()) //prepareNotification())
        try {
            mPlayer?.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        tvTotalLen = SongTimer().milliSecondsToTimer(totalLen.toLong())//totalLen..toLong()?.let { songTimer.milliSecondsToTimer(it) }
        mPlayer?.start()
        mPlayer?.setOnCompletionListener(this)

//        Log.d(TAG, "Player onPrepared(), tvTotalLen=> ${tvTotalLen}")
//        mTimerUpdateHandler.postDelayed(mTimerUpdateRunnable, 0)
    }

    companion object {
        var isServiceRunning: Boolean? = null
        var isAlbum: Boolean = true
        private const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        private val TAG = ForegroundService::class.java.simpleName
        var state = MusicConstants.STATE_SERVICE.NOT_INIT
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }


    private val mDelayedShutdown = Runnable {
//        unlockWiFi()
//        unlockCPU()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun lockCPU() {
        val mgr = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.javaClass.simpleName)
        mWakeLock?.acquire(10*60*1000L /*10 minutes*/)
        Log.d(TAG, "Player lockCPU()")
    }

    private fun unlockCPU() {
        mWakeLock?.let {
            if (it.isHeld) {
                it.release()

                Log.d(TAG, "Player unlockCPU()")
            }
        }
        mWakeLock = null
    }

    private fun lockWiFi() {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val lWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if(connectivityManager.isDefaultNetworkActive){
            //인터넷 됨
            val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            manager.let {
                mWiFiLock = manager.createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF /*WIFI_MODE_FULL*/, ForegroundService::class.java.simpleName)
                mWiFiLock?.acquire()
            }
            Log.d(TAG, "Player lockWiFi() 인터넷 됨")
        }else{
            Log.d(TAG, "Player lockWiFi() 인터넷 안됨")
            //인터넷 안됨
        }
    }

    private fun unlockWiFi() {
        if (mWiFiLock != null && mWiFiLock!!.isHeld) {
            mWiFiLock!!.release()
            mWiFiLock = null
            Log.d(TAG, "Player unlockWiFi()")
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "Player onError() what:$what")
        destroyPlayer()
        mHandler.postDelayed(mDelayedShutdown, MusicConstants.DELAY_SHUTDOWN_FOREGROUND_SERVICE)
        mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti()) //prepareNotification())
        state = MusicConstants.STATE_SERVICE.NOT_INIT
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        Log.d(TAG, "Player onBufferingUpdate():$percent")
    }

    override fun onCompletion(mp: MediaPlayer?) { // 실행 안됨
 /*       state = MusicConstants.STATE_SERVICE.PAUSE
        lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
        val lPendingPauseIntent = PendingIntent.getService(applicationContext,0,lPauseIntent,PendingIntent.FLAG_IMMUTABLE)
        try {
            lPendingPauseIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }*/
        isClickedPause = true
        state = MusicConstants.STATE_SERVICE.PAUSE
        mNotificationManager?.notify(MusicConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNoti())

        if (index == musicList.size-1) index = 0
        else index += 1
        dataSource = musicList[index].path
        titleMain = "Music"
        titleDetail = musicList[index].title
        song = musicList[index]
        try {
            val mp3file= Mp3File(song?.path)
            val id3v2Tag = mp3file.id3v2Tag
            lyric = id3v2Tag.lyrics
        } catch (e: Exception) {
            lyric = "getlyric error"
        }

        lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
        val lPendingPlayIntent =
            PendingIntent.getService(applicationContext, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
        try {
            lPendingPlayIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
        /*index += 1
        dataSource = musicList[index].path
        MusicDevice.titleMain = "Music"
        titleDetail = musicList[index].title
        song = musicList[index]*/
        Log.i(TAG,"Clicked pause in onComplete, $dataSource")
        play()
//        mPlayer?.pause()
    }
}