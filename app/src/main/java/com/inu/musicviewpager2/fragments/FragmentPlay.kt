package com.inu.musicviewpager2.fragments

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context.POWER_SERVICE
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.adapter.MusicAdapter.Companion.index
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.constant.PendinIntent.lPauseIntent
import com.inu.musicviewpager2.constant.PendinIntent.lPlayIntent
import com.inu.musicviewpager2.database.MusicRoomDatabase
import com.inu.musicviewpager2.databinding.FragmentPlayBinding
import com.inu.musicviewpager2.model.MusicDevice
import com.inu.musicviewpager2.model.MusicDevice.dataSource
import com.inu.musicviewpager2.model.MusicDevice.isRadioOn
import com.inu.musicviewpager2.model.MusicDevice.mPlayer
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.model.MusicDevice.song
import com.inu.musicviewpager2.model.MusicDevice.titleDetail
import com.inu.musicviewpager2.play.SongTimer
import com.inu.musicviewpager2.service.ForegroundService.Companion.isAlbum
import com.inu.musicviewpager2.service.ForegroundService.Companion.state
import com.inu.musicviewpager2.service.MyHandler
import com.inu.musicviewpager2.util.EventListener
import com.inu.musicviewpager2.util.MetaExtract
import com.inu.musicviewpager2.util.MyApplication
import com.inu.musicviewpager2.util.VerticalScrollingTextViewKt
import com.inu.musicviewpager2.util.VerticalScrollingTextViewKt.Companion.mView
import com.mpatric.mp3agic.Mp3File
import kotlinx.coroutines.*
import kotlin.math.roundToInt


class FragmentPlay : Fragment(), EventListener {

    private var db: MusicRoomDatabase? = null
    lateinit var binding: FragmentPlayBinding
    lateinit var songTimer: SongTimer
    var isRadioAddrComplted = false
    lateinit var imagePlay: ImageView
    lateinit var radioImageView: ImageView
    lateinit var playingImage: ImageView
    lateinit var seekBar: SeekBar
    lateinit var textTitle: TextView
    lateinit var textLyric: TextView
    var currentPosition: Int = 0
    var isThreadRun = true
    private val mHandler = MyHandler() //Handler()
    private val r2: Runnable = object : kotlinx.coroutines.Runnable {
        override fun run() {
            Log.d("playFlag","핸들러 스레드 온")

            if (mPlayer != null && mPlayer?.isPlaying == true) {
//                Log.d("threadRun playFrag in thread 1:","${mPlayer?.isPlaying}, isThreadRun=> $isThreadRun, isAlbum=> $isAlbum")
                CoroutineScope(Dispatchers.Main).launch {
                    currentPosition = mPlayer?.currentPosition ?: 0
                    seekBar.max = totalLen
                    seekBar.progress = currentPosition
                }

                // 정적 화면, 브로드캐스트 갖다 붙임
//                if (mPlayer != null && mPlayer?.isPlaying == true) { //  플레이시
                if (realTotalLen != -1 && isAlbum) { // 라디오가 아니고 mp3일 때만
                    Log.d("playFrag1:mp3 play", "mp3 포지션=> ")
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.imagePlaying.visibility = VISIBLE
                        binding.tvTotalDuration.text = songTimer.milliSecondsToTimer(totalLen.toLong())
                        Glide.with(this@FragmentPlay).load(R.raw.musicloader96).into(playingImage)
                        binding.appTextView.text = "Music Playing..."
                        Glide.with(requireContext())
                            .load(MusicDevice.song?.albumUri)
                            .error(R.drawable.ic_not)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                                .centerInside()
                            .into(radioImageView)
                        binding.tvJudulLagu.text = song?.title // titleDetail
                        binding.textSinger.text = song?.artist ?: ""
                        binding.tvCurrentDuration.text = "${index+1}/${musicList.size}"
                        binding.textViewGenreInplay.text ="${song?.album}"
                        textLyric.text = MusicDevice.lyric
//                            textLyric.scrollY = 0
//                            binding.textViewWrapper.scrollTo(0, 0)
                        textLyric.scrollY = 0 // 적용 안됨

                        Glide.with(requireContext())
                            .load(R.drawable.ic_pause)
                            .into(imagePlay)
                    }
                    isAlbum = false // 정적화면 변경용
                    mPlayer?.isLooping = false


                } else if (realTotalLen == -1 && isAlbum) { // 라디오일 때
//                        Log.d("playFrag", "라디오 isPlaying=> ${mPlayer?.isPlaying}")
                    binding.tvTotalDuration.text = songTimer.milliSecondsToTimer(totalLen.toLong())
                    binding.tvCurrentDuration.text = "0:00"
                    binding.textViewGenreInplay.text = ""
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.imagePlaying.visibility = VISIBLE
                        Glide.with(this@FragmentPlay).load(R.raw.colorloading96).into(playingImage)
                        binding.textView.visibility = INVISIBLE
                        binding.appTextView.text = "Radio Playing..."
                        binding.tvJudulLagu.text = titleDetail
                        binding.textSinger.text = "방송"
                        textLyric.text = "                     "
                        Glide.with(requireContext())
                            .load(MusicDevice.imageRadioPlaySource)
                            .error(R.drawable.ic_not)
                            .into(radioImageView)
                    }
                    isAlbum = false
                    CoroutineScope(Dispatchers.Main).launch {
                        Glide.with(requireContext())
                            .load(R.drawable.ic_pause)
                            .into(imagePlay)
                    }
                }
            } /*else { // 플레이 아닐때
//                    Log.d("playFrag", "플레이 아닐때 isPlaying=> ${mPlayer?.isPlaying}")
                CoroutineScope(Dispatchers.Main).launch {
                    playingImage.visibility = INVISIBLE
                }
            }*/
//                    imagePlay.setImageDrawable(ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_media_play))

            if (isThreadRun) {
                val pm = requireContext().getSystemService(POWER_SERVICE) as PowerManager
                if (pm.isInteractive) {
                    mHandler.postDelayed(this, 1000)
                } else {
                    mHandler.removeCallbacksAndMessages(null)
                }
            }

            // 라디오 주소 수신 완료확인
            if (!isRadioAddrComplted && MusicDevice.radioAddr.values.indexOf("") == -1) {
                Toast.makeText(requireContext(),"Radio Ready!!", Toast.LENGTH_SHORT).show()
                isRadioAddrComplted = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isThreadRun = true
        if (!isRadioOn) {
            VerticalScrollingTextViewKt.continuousScrolling = true
        }
        threadRun()

//        Log.d("threadRun", "onResume() called")
        
        if (mPlayer?.isPlaying == false || mPlayer == null) {
            isPlaying = false
            Glide.with(requireContext())
                .load(R.drawable.ic_play)
                .into(imagePlay)
            Glide.with(requireContext())
                .load(R.drawable.ic_favorite)
                .into(playingImage)
        } else {
//            if (MusicDevice.titleMain == "Music") {           }
            isPlaying = true
            Glide.with(requireContext())
                .load(R.drawable.ic_pause)
                .into(binding.imagePlay)
            if (MusicDevice.titleMain == "Music") {
                Glide.with(requireContext())
                    .load(R.raw.musicloader96)
                    .into(binding.imagePlaying)
            } else {
                Glide.with(requireContext())
                    .load(R.raw.colorloading96)
                    .into(binding.imagePlaying)
            }
        }
    }

    companion object {
        var totalLen = 30000
        var realTotalLen = 0
        var isPlaying = false
    }
    fun threadRun() {
        if (totalLen >= 0) { // 음악이면
            mHandler.removeCallbacksAndMessages(null)
            mHandler.post(r2)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        Log.d("threadRun", "onCreateView() called")
        binding = FragmentPlayBinding.inflate(inflater, container, false)
        songTimer = SongTimer()

        radioImageView = binding.imageView
        playingImage = binding.imagePlaying
        imagePlay = binding.imagePlay
        textTitle = binding.tvJudulLagu
        textTitle.setHorizontallyScrolling(true)
        textTitle.isSelected = true
        textLyric = binding.textViewLyric

        Glide.with(requireContext())
            .load(R.drawable.ic_play)
            .into(binding.imagePlay)

        val seekBarHint: TextView = binding.textView
        seekBar = binding.seekBar

        db = MusicRoomDatabase.getInstance(requireContext())
        binding.imageViewBookmark.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val temp: Deferred<Boolean> = async(Dispatchers.IO) { // async 로 결과를 반환
                    val isBookmarked = db!!.musicDao().getMusic(song!!.id).isBookmarked
                    if (isBookmarked) { // 이미 즐겨찾기 되어있으면 삭제
//                        db!!.parkDao().deleteBookmark(bookmarkData)
                        Log.d("bookmark ised","${song!!.title}")
                        false


                    } else { // 없으면 즐겨찾기 저장
                        db!!.musicDao().insert(song!!)
                        Log.d("bookmark insert","${song!!.title}")
                        true
                    }
                }// UI 변경
                val selected = temp.await() // async 작업이 완료 되고 난 후 호출.
                it.isSelected = selected
            }

        }
        textLyric.setOnLongClickListener(OnLongClickListener {
            if (textLyric.text != "                     ")
            textLyric.text = "                     "
            else textLyric.text = MusicDevice.lyric
            true //true 설정
        })
        imagePlay.setOnClickListener(View.OnClickListener {
//            Log.d("playFrag","state재생=> ${state}, ${mPlayer!!.isPlaying}, ${mPlayer!!.currentPosition}," ) //에러 발생
            if (state == MusicConstants.STATE_SERVICE.PAUSE) { // 정지상태일 때 재생으로
                musicList[index].isSelected = true
//                VerticalScrollingTextViewKt scrollStop() //startSroll()
////                computeScroll()
//                ((VerticalScrollingTextViewKt) isScrolling = true

                state = MusicConstants.STATE_SERVICE.PLAY
                lPlayIntent.action = MusicConstants.ACTION.REPLAY_ACTION
                val lPendingPlayIntent = PendingIntent.getService(context,0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
                try {
                    lPendingPlayIntent.send()
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }

                isPlaying = true
                Glide.with(requireContext())
                    .load(R.drawable.ic_pause)
                    .into(binding.imagePlay)
                if (binding.appTextView.text.contains("Music")) {
                    Glide.with(requireContext())
                        .load(R.raw.musicloader96)
                        .into(binding.imagePlaying)
                } else {
                    Glide.with(requireContext())
                        .load(R.raw.colorloading96)
                        .into(binding.imagePlaying)
                }
            } else { // 재생에서 정지로
                if (mPlayer == null) {

                } else {
//                    Log.d("playFrag","state정지=> ${state}, ${mPlayer!!.isPlaying}, ${mPlayer!!.currentPosition}," )
                    musicList[index].isSelected = false
                    state = MusicConstants.STATE_SERVICE.PAUSE
                    lPauseIntent.action = MusicConstants.ACTION.PAUSE_ACTION
                    val lPendingPauseIntent = PendingIntent.getService(
                        context,
                        0,
                        lPauseIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    try {
                        lPendingPauseIntent.send()
                    } catch (e: PendingIntent.CanceledException) {
                        e.printStackTrace()
                    }

                    isPlaying = false
                    Glide.with(requireContext())
                        .load(R.drawable.ic_play)
                        .into(imagePlay)
                    Glide.with(requireContext())
                        .load(R.drawable.ic_favorite)
                        .into(playingImage)
                }
            }

//            Log.d("EventListener(setOnclick)", "$isPlaying")
            listenStart()
        })

        binding.imageNext.setOnClickListener {
            if (binding.appTextView.text.contains("Music")) {
                if (index == musicList.size-1) {
                    musicList[index].isSelected = false
                    index = 0
                    musicList[index].isSelected = true
                }
                else {
                    musicList[index].isSelected = false
                    index += 1
                    musicList[index].isSelected = true
                }
                dataSource = musicList[index].path
                MusicDevice.titleMain = "Music"
                titleDetail = musicList[index].title
                song = musicList[index]
//                MusicAdapter().updateItem()
                MusicDevice.lyric = MetaExtract().getLyric()

                lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                val lPendingPlayIntent =
                    PendingIntent.getService(context, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
                try {
                    lPendingPlayIntent.send()
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        }
        binding.imagePrev.setOnClickListener {
            if (binding.appTextView.text.contains("Music")) {
                if (index == 0) {
                    musicList[index].isSelected = false
                    index = musicList.size - 1
                    musicList[index].isSelected = true
                }
                else {
                    musicList[index].isSelected = false
                    index += -1
                    musicList[index].isSelected = true
                }
                dataSource = musicList[index].path
                MusicDevice.titleMain = "Music"
                titleDetail = musicList[index].title
                song = musicList[index]
                MusicDevice.lyric = MetaExtract().getLyric()

                lPlayIntent.action = MusicConstants.ACTION.PLAY_ACTION
                val lPendingPlayIntent =
                    PendingIntent.getService(context, 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
                try {
                    lPendingPlayIntent.send()
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        }

        val seekForwardTime = 10000
        binding.imageForward.setOnClickListener {
//            Log.d("currentPosition","$currentPosition")
            if (currentPosition + seekForwardTime <= realTotalLen) {
                mPlayer?.seekTo(currentPosition + seekForwardTime)
            } else {
                mPlayer?.seekTo(realTotalLen)
            }
        }
        binding.imageRewind.setOnClickListener {
//            Log.d("currentPosition","$currentPosition")
            if (currentPosition - seekForwardTime >= 0) {
                mPlayer?.seekTo(currentPosition - seekForwardTime)
            } else {
                mPlayer?.seekTo(0)
            }
        }
       /* var isRepeat = false
        var isShuffle = true
        binding.imageRepeat.setOnClickListener {
            if (isRepeat) {
                isRepeat = false
//                Toast.makeText(requireContext(), "반복 끄기", Toast.LENGTH_SHORT).show()
                binding.imageRepeat.setImageResource(R.drawable.btn_repeat)
            } else {
                isRepeat = true
//                Toast.makeText(requireContext(), "노래 반복", Toast.LENGTH_SHORT).show()
                isShuffle = false
                binding.imageRepeat.setImageResource(R.drawable.btn_repeat_focused)
//                binding.imageShuffle.setImageResource(R.drawable.btn_shuffle)
            }
        }*/

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBarHint.visibility = View.VISIBLE
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromTouch: Boolean) {
                seekBarHint.visibility = View.VISIBLE
//                val x = ceil((progress / 1000f).toDouble()).toInt()
//                if (x < 10) seekBarHint.text = "0:0$x"
                seekBarHint.text = songTimer.milliSecondsToTimer(currentPosition.toLong())
//                if (x in 0..59) seekBarHint.text = "0:$x"
//                if (x >= 60) seekBarHint.text = "${(x / 60)}:${x % 60}"

                val percent = progress / seekBar.max.toDouble()
                val offset = seekBar.thumbOffset
                val seekWidth = seekBar.width
                val `val` = (percent * (seekWidth - 2 * offset)).roundToInt()
                val labelWidth = seekBarHint.width
//                seekBarHint.x = (offset + seekBar.x + `val` - (percent * offset).roundToInt()
//                        - (percent * labelWidth / 2).roundToInt())
                binding.frameTextviewPlaytime.x =  (offset + seekBar.x + `val` - (percent * offset).roundToInt()
                        - (percent * labelWidth / 2).roundToInt())
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mPlayer != null && mPlayer?.isPlaying == true) {
                    mPlayer!!.seekTo(seekBar.progress)
                }
            }
        })
        //브로드캐스트리시버 등록
//        var br = MyBR()
//        var filter = IntentFilter()
//        filter.addAction("test")
//        registerReceiver(requireContext(), br, filter, RECEIVER_NOT_EXPORTED)
        return binding.root
    }
    override fun onPause() {
        super.onPause()
        Log.d("threadRun", "onPause() called")
        isThreadRun = false
        VerticalScrollingTextViewKt.continuousScrolling = false
    }

    override fun onStop() {
        super.onStop()
        Log.d("threadRun", "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("threadRun", "onDestroy() called")

    }

    override fun onDetach() {
        super.onDetach()
        lPlayIntent.action = MusicConstants.ACTION.STOP_ACTION
        val lPendingStopIntent = PendingIntent.getService(MyApplication.applicationContext(), 0, lPlayIntent, PendingIntent.FLAG_IMMUTABLE)
        try {
            lPendingStopIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }

    override fun onEvent(event: Boolean) {
        Log.d("EventListener(vertival)", "$isPlaying")
    }

    fun listenStart(){
        val eventGenerator = VerticalScrollingTextViewKt(requireContext(), attrs = null)	// A가 B를 리스너를 통해 구독하는 시점
        eventGenerator.generate()
    }
/*
    inner class MyBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.d("playFrag", "onReceive()=> ${mPlayer?.isPlaying}")
//            if(intent?.action == "test")
//            if (intent != null) {
                binding.tvCurrentDuration.text = intent?.getIntExtra("value", 0).toString()
//            }
            if (mPlayer != null && mPlayer?.isPlaying == true) { //  플레이시
            Log.d("playFrag", "플레이시=> ${mPlayer?.isPlaying}")
                imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        android.R.drawable.ic_btn_speak_now
                    )
                )

                binding.imagePlaying.visibility = VISIBLE
                Glide.with(this@FragmentPlay).load(R.raw.playing).into(playingImage)
                binding.tvTotalDuration.text = "" + mPlayer?.duration?.toLong()
                    ?.let { songTimer.milliSecondsToTimer(it) }

                if (mPlayer?.duration != -1 && isAlbum) { // 라디오가 아니고 mp3일 때만
                    Log.d("playFrag1:mp3 play", "mp3 포지션=> $pausePosition")
                    binding.appTextView.text = "Music Playing..."
//                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, MusicDevice.song?.albumUri));

//                    if (isAlbum) {
                        Glide.with(requireContext())
                            .load(MusicDevice.song?.albumUri)
                            .error(R.drawable.ic_not)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerInside()
                            .into(binding.imageView)
                        isAlbum = false
//                    }
//                    binding.imageView.setImageBitmap(R.drawable.ic_not.toBit)

//                    if (titleDetail != null) {
                        textTitle.text = titleDetail

                        mPlayer?.isLooping = false
                        seekBar.max = mPlayer!!.duration

                       *//* val myThread = Thread(r)
                        if (myThread.isAlive) {
                            myThread.interrupt()
                        }
                        myThread.start()*//*
//                    } // if (isAlbum)
                } else if (mPlayer?.duration == -1 && isAlbum) {
                    Log.d("playFrag", "라디오 isPlaying=> ${mPlayer?.isPlaying}")
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.textView.visibility = INVISIBLE
                        binding.appTextView.text = "Radio Playing..."
    //                    if (titleDetail != null) {
                        binding.tvJudulLagu.text = titleDetail
                        binding.textSinger.text = song?.artist ?: ""
                        Glide.with(requireContext())
                            .load(MusicDevice.imageRadioPlaySource)
                            .error(R.drawable.ic_not)
                            .into(radioImageView)
                    }
//                    }
                    isAlbum = false
                    mHandler.removeCallbacksAndMessages(null)
                }
            } else  { // 플레이 아닐때
                Log.d("playFrag", "플레이 아닐때 isPlaying=> ${mPlayer?.isPlaying}")
                CoroutineScope(Dispatchers.Main).launch {
                    playingImage.visibility = INVISIBLE
                    imagePlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            android.R.drawable.ic_media_play
                        )
                    )
                }
            }
        }
    } // 브로트캐스트*/
}

/*interface EventListener {
    fun onEvent(event: Boolean)
}*/
/*

// 브로드캐스트용
private val r: Runnable = object : Runnable {
    override fun run() {
        Log.d("playFrag in thread 1:","${mPlayer?.isPlaying}")
        if (mPlayer != null) {
            Log.d("playFrag in thread 2:","${mPlayer?.isPlaying}")
            try {
                Log.d("playFrag in thread 3:","${mPlayer?.isPlaying}")
                var currentPosition: Int = mPlayer?.currentPosition ?: 0
//                    pausePosition = currentPosition
                val total: Int = mPlayer?.duration ?: 30000
                while (currentPosition < total) {
                    currentPosition = try {
                        Log.d("playFrag in thread 4:","${mPlayer?.isPlaying}")
                        Thread.sleep(1000)
                        mPlayer!!.currentPosition
                    } catch (e: InterruptedException) {
                        Log.d("playFrag in thread error:","${mPlayer?.isPlaying}")
                        return
                    } catch (e: Exception) {
                        Log.d("playFrag in thread error:","${mPlayer?.isPlaying}")
                        return
                    }
                    seekBar.progress = currentPosition
                    binding.tvCurrentDuration.text = currentPosition.toString()

                    Log.d("playFrag in thread 5:","${mPlayer?.isPlaying}, ${currentPosition}")
                }
            } catch (e: Exception) {
                Log.d("playFrag in thread error:","${mPlayer?.isPlaying}")
            }
        }
//            mHandler.postDelayed(this, 1000) // MusicConstants.DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE)
    }
}
*/
