package com.inu.musicviewpager2.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import com.inu.musicviewpager2.R
import org.acra.BuildConfig
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class MyApplication : Application(){
    init{
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }

    @SuppressLint("Range")
    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            //each plugin you chose above can be configured in a block like this:
            toast {
                text = getString(R.string.acra_toast_text)
                length = Toast.LENGTH_SHORT
                //opening this block automatically enables the plugin.
            }
            mailSender {
                //required
                mailTo = "isson33@naver.com"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = getString(R.string.mail_subject)
                //defaults to empty
                body = getString(R.string.mail_body)
            }
        }
    }
}