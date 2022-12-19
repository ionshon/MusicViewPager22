package com.inu.musicviewpager2.model

import android.media.MediaPlayer
import com.inu.musicviewpager2.constant.MusicConstants


object MusicDevice {
    val deviceMusics = mutableListOf<Music>()
    val musicList = mutableListOf<Music>()
    var song :Music? = null
    var lyric: String? = ""
    var dataSource: String? = null
    var index: Int = 0
    var titleMain: String? = null
    var titleDetail: String? = null
    var imageRadioPlaySource: Int? = null
    var mPlayer: MediaPlayer? = null
    var pausePosition: Int = 0
    var radioAddr = mutableMapOf("kbs1Radio" to "","kbsCoolFM" to "",
                        "kbs2Radio" to "", "kbsClassic" to "",
                        "cbs939" to MusicConstants.RADIO_ADDR.cbs939/*http://aac.cbs.co.kr/cbs939/cbs939.stream/playlist.m3u8*/, "cbs981" to MusicConstants.RADIO_ADDR.cbs981/*http://aac.cbs.co.kr/cbs981/cbs981.stream/playlist.m3u8"*/,
                        "sbsLove" to "", "sbsPower" to "",
                        "mbcFm" to "", "mbcFm4u" to "",
                        "ytnNews" to MusicConstants.RADIO_ADDR.ytn945, "arirangFM" to MusicConstants.RADIO_ADDR.arirang,
                        "tbsFM" to MusicConstants.RADIO_ADDR.tbsfm, "tbsBusan" to MusicConstants.RADIO_ADDR.tbsBusan
                        )
    var temp = mutableListOf<String>()
//    var currentSongIndex = 0
}