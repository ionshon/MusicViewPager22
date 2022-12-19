package com.inu.musicviewpager2.util

import android.os.Environment
import android.util.Base64
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class FtpConnect {

    val domain = "ftp://ftp.mozilla.org/README"

    fun test() {
        val url = URL(domain)
        val cn: URLConnection = url.openConnection()
        cn.setRequestProperty(
            "Authorization",
            "Basic " + Base64.encodeToString("anonymous:a@b.c".toByteArray(), Base64.DEFAULT)
        )

        val dir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fos = FileOutputStream(dir.path + "/README")

        val `is`: InputStream = cn.getInputStream()
        var bytesRead = -1
        val buf = ByteArray(8096)
        while (`is`!!.read(buf).also { bytesRead = it } != -1) {
            fos!!.write(buf, 0, bytesRead)
        }
        if (`is` != null) `is`.close()
        if (fos != null) {
            fos.flush()
            fos.close()
        }
//        println()
    }
}