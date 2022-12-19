package com.inu.musicviewpager2.util.ACRA

import android.content.Context
import android.util.Log
import com.google.auto.service.AutoService
import org.acra.config.CoreConfiguration
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

class CrashReportSender : ReportSender {

    override fun send(context: Context, errorContent: CrashReportData) {
        Log.d("[ACRA-Example]", "Report Sent!")
        // Iterate over the CrashReportData instance and do whatever
        // you need with each pair of ReportField key / String value
    }

}

@AutoService(ReportSenderFactory::class)
class MySenderfactory : ReportSenderFactory {

    // requires a no arg constructor.
/*
    override fun create(context: Context, config: ACRAConfiguration) : ReportSender {
        return CrashReportSender()
    }*/
    override fun create(context: Context, config: CoreConfiguration): ReportSender {
        return CrashReportSender()
    }

    //optional implementation in case you want to disable your sender in certain cases
    override fun enabled(config : CoreConfiguration) : Boolean {
        return true
    }
    /*override fun enabled(config: CoreConfiguration): Boolean {
        return super.enabled(config)
    }*/
}