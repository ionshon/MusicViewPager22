package com.inu.musicviewpager2.util

import android.util.Log
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.model.MusicDevice
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread

class SetStreamUrl {

    private val LOGTAG = "GetStreamingUrl"

//    private val cacheDir: File = MyApplication.ApplicationContext().cacheDir

    fun setStreamUrl(url: String?) { //: LinkedList<String?>? {
        var br: BufferedReader
        var murl: String = ""
//        var murls: LinkedList<String?>? = null
        Log.i(LOGTAG, "cachedir, url=> ${url}")

        thread {
            try {
                val mUrl = URL(url).openConnection()
                Log.d("openConnection","$murl")

                br = BufferedReader(
                    InputStreamReader(mUrl.getInputStream())
                )

                mUrl.connectTimeout = 5000
                Log.i(LOGTAG, "cachedir, murl => ${murl}")
                while (true) {
                    try {
                        val line = br.readLine() ?: break
                        murl = parseLine(line)
                        if (murl != "") {
//                            MusicDevice.temp.add(murl)
                            Log.d(LOGTAG, "라인1: $murl")
                            when (url) {
                                MusicConstants.RADIO_ADDR.kbs1Radio -> {
                                    MusicConstants.RADIO_ADDR.kbs1Radio = murl
                                    MusicDevice.radioAddr["kbs1Radio"] = murl
                                    Log.d(LOGTAG, "라인0 kbs1Radio: $murl")
                                }
                                MusicConstants.RADIO_ADDR.kbs2Radio -> {
                                    MusicConstants.RADIO_ADDR.kbs2Radio = murl
                                    MusicDevice.radioAddr["kbs2Radio"] = murl
                                    Log.d(LOGTAG, "라인 kbs1Radio: $murl")
                                }
                                MusicConstants.RADIO_ADDR.kbsCoolFM -> {
                                    MusicConstants.RADIO_ADDR.kbsCoolFM = murl
                                    MusicDevice.radioAddr["kbsCoolFM"] = murl
                                    Log.d(LOGTAG, "라인 kbs1Radio: $murl")
                                }
                                MusicConstants.RADIO_ADDR.kbsClassic -> {
                                    MusicConstants.RADIO_ADDR.kbsClassic = murl
                                    MusicDevice.radioAddr["kbsClassic"] = murl
                                    Log.d(LOGTAG, "라인 kbsClassic: $murl")
                                }
                                MusicConstants.RADIO_ADDR.sbsPower -> {
                                    MusicConstants.RADIO_ADDR.sbsPower = murl
                                    MusicDevice.radioAddr["sbsPower"] = murl
                                    Log.d(LOGTAG, "라인 sbsPower: $murl")
                                }
                                MusicConstants.RADIO_ADDR.sbsLove -> {
                                    MusicConstants.RADIO_ADDR.sbsLove = murl
                                    MusicDevice.radioAddr["sbsLove"] = murl
                                    Log.d(LOGTAG, "라인 sbsLove: $murl")
                                }
                                MusicConstants.RADIO_ADDR.mbcFm -> {
                                    MusicConstants.RADIO_ADDR.mbcFm = murl
                                    MusicDevice.radioAddr["mbcFm"] = murl
                                    Log.d(LOGTAG, "라인 mbcFm: $murl")
                                }
                                MusicConstants.RADIO_ADDR.mbcFm4u -> {
                                    MusicConstants.RADIO_ADDR.mbcFm4u = murl
                                    MusicDevice.radioAddr["mbcFm4u"] = murl
                                    Log.d(LOGTAG, "라인 mbcFm4u: $murl")
                                }
                            }
                        } //   else MusicDevice.radioAddr.add(url.toString())

                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.e(LOGTAG, "MalformedURLException 1")
                        }
                    }
                    br.close()
            } catch (e: MalformedURLException) {
                Log.e(LOGTAG, "MalformedURLException 2")
                e.printStackTrace()
            }
        }
//        loadCache()
    }

    private fun parseLine(line: String): String {
        val trimmed = line.trim { it <= ' ' }
        return if (trimmed.indexOf("http") >= 0) {
            trimmed.substring(trimmed.indexOf("http"))
        } else ""
    }
}
    /*
    var radioAddrs = listOf<String>("kbs1Radio","kbs2Radio","kbsCoolFM", "kbsClassic","sbsPower","sbsLove","mbcFm","mbcFm4u")

    private fun saveCache(data: String, index: Int) {
        when(index) {
            0 -> {
                try {
                val file = File(cacheDir, "kbs1Radio")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
//            Log.d("data.toByteArray()", "saved=> $data")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            1 -> {
                val file = File(cacheDir, "kbs2Radio")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            2 -> {
                val file = File(cacheDir, "kbsCoolFM")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            3 -> {
                val file = File(cacheDir, "kbsClassic")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            4 -> {
                val file = File(cacheDir, "sbsPower")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            5 -> {
                val file = File(cacheDir, "sbsLove")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            6 -> {
                val file = File(cacheDir, "mbcFm")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
            7 -> {
                val file = File(cacheDir, "mbcFm4u")
                val outputStream = FileOutputStream(file)
                outputStream.write(data.toByteArray())
                outputStream.close()
            }
        }
        
    }
    
    private fun loadCache(){
        try {
            for (i in 0..7) {
                val file = File(cacheDir, radioAddrs[i])
                if (!file.exists()) file.createNewFile()
                val inputStream = FileInputStream(file)
                val s = Scanner(inputStream)
                var text = "" //mutableListOf<String>()
                while (s.hasNext()) {
                    text+=s.hasNext()
                }
                Log.d("라인1","${radioAddrs[i]} => $text")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/



/*for (i in 0 until text.size) {
    Log.d("FileInputStream1", text[i])
//                Log.d("FileInputStream2", MusicDevice.temp[i])
}*/
//            loggingData =text