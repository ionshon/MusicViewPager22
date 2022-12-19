package com.inu.musicviewpager2.constant


object MusicConstants {
    const val NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503
    const val DELAY_SHUTDOWN_FOREGROUND_SERVICE: Long = 20000
    const val DELAY_UPDATE_NOTIFICATION_FOREGROUND_SERVICE: Long = 30000

    object ACTION {
        const val MAIN_ACTION = "music.action.main"
        const val PAUSE_ACTION = "music.action.pause"
        const val PLAY_ACTION = "music.action.play"
        const val REPLAY_ACTION = "music.action.replay"
        const val START_ACTION = "music.action.start"
        const val STOP_ACTION = "music.action.stop"
    }

    object STATE_SERVICE {
        const val PREPARE = 888
        const val PLAY = 777
        const val PAUSE = 503
        const val NOT_INIT = 0
    }

    object RADIO_ADDR {  // serpent0.duckdns.org = 116.39.207.4
//        val mUriRadioDefault = "https://nfw.ria.ru/flv/audio.aspx?ID=75651129&type=mp3"
        var serpent0duckdnsorg = "116.39.207.4"
        var kbs1Radio = "http://116.39.207.4:8088/kbs1radio.pls"// KBS 1라디오
        var kbsCoolFM = "http://116.39.207.4:8088/kbs2fm.pls" //KBS 쿨 FM
        var kbs2Radio = "http://116.39.207.4:8088/kbs2radio.pls"// KBS 해피 FM
        var kbsClassic = "http://116.39.207.4:8088/kbsfm.pls"
        var cbs939 = "http://aac.cbs.co.kr/cbs939/cbs939.stream/playlist.m3u8"
        var cbs981 = "http://aac.cbs.co.kr/cbs981/cbs981.stream/playlist.m3u8"
        var sbsPower = "http://116.39.207.4:8088/sbsfm.pls"
        var sbsLove = "http://116.39.207.4:8088/sbs2fm.pls"
        var mbcFm = "http://116.39.207.4:8088/mbcsfm.pls"
        var mbcFm4u = "http://116.39.207.4:8088/mbcfm.pls"
        var ytn945 = "http://slive.ytn.co.kr:1935/live/fmlive_0624_1.sdp/playlist.m3u8"
        var tbsfm = "https://cdnfm.tbs.seoul.kr/tbs/_definst_/tbs_fm_web_360.smil/playlist.m3u8"
        var tbsBusan = "http://210.96.79.115:1935/busan/myStream/playlist.m3u8?DVR"
        var arirang = "http://amdlive.ctnd.com.edgesuite.net:80/arirang_3ch/arirang_3ch_audio/playlist.m3u8"
        val radioAddrList = listOf<String>(
            "http://116.39.207.4:8088/kbs1radio.pls",// KBS 1라디오
            "http://116.39.207.4:8088/kbs2fm.pls", //KBS 쿨 FM
            "http://116.39.207.4:8088/kbs2radio.pls",// KBS 해피 FM
            "http://116.39.207.4:8088/kbsfm.pls",
            "http://aac.cbs.co.kr/cbs939/cbs939.stream/playlist.m3u8",
            "http://aac.cbs.co.kr/cbs981/cbs981.stream/playlist.m3u8",
            "http://116.39.207.4:8088/sbsfm.pls",
            "http://116.39.207.4:8088/sbs2fm.pls",
            "http://116.39.207.4:8088/mbcsfm.pls",
            "http://116.39.207.4:8088/mbcfm.pls",
            "http://slive.ytn.co.kr:1935/live/fmlive_0624_1.sdp/playlist.m3u8",
            "https://cdnfm.tbs.seoul.kr/tbs/_definst_/tbs_fm_web_360.smil/playlist.m3u8",
            "http://210.96.79.115:1935/busan/myStream/playlist.m3u8?DVR",
            "http://amdlive.ctnd.com.edgesuite.net:80/arirang_3ch/arirang_3ch_audio/playlist.m3u8"
        )
    }
}