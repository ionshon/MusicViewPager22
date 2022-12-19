package dlna.utils

import android.app.Application
import android.util.Log
import com.inu.musicviewpager2.MainActivity
import com.inu.musicviewpager2.util.CacheInterceptor
import com.inu.musicviewpager2.util.MyApplication
import dlna.model.Devices
import okhttp3.*
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.IOException
import java.io.StringReader
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kotlin.concurrent.thread


object  UpdateSpecs {

    fun updateSpecs(loc: URL) {
    /*    val retrofit = Retrofit.Builder()
            .baseUrl(loc)
            .client(OkHttpClient())
            .build()
        val service = retrofit.create(ApiService::class.java)*/

//        Log.d("updateSpeck", "loc: $loc")
//        val mClient = OkHttpClient()
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(MyApplication.applicationContext().cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request =
                        chain.request().newBuilder().addHeader("Connection", "close").build()
                    return chain.proceed(request)
                }
            })
            .build()
        val mClient: OkHttpClient = OkHttpClient.Builder()
//            .retryOnConnectionFailure(true)
            .cache(myCache)
            .addInterceptor(CacheInterceptor(MyApplication.applicationContext()))
            .build()
        var mRawXml: String?
//        var mProperties: HashMap<String, String>? = null
        var mFriendlyName: String
        thread { // 여기서는 스레트 안해도 오류 안나지만 받아오지 못함
            val request: Request = Request.Builder()
                .url(loc)
//                .addHeader("Connection", "close")
                .build()
            val response = mClient.newCall(request).execute()
            Log.d("updateSpeck", "response.isSuccessful: $response")
            if (!response.isSuccessful) {
                throw IOException("Unexpected_code $response")
            }
            mRawXml = response.body.string()
//            Log.d("updateSpeck", "mRawXml: $mRawXml")
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val source = InputSource(StringReader(mRawXml))
            val doc: Document = try {
                db.parse(source)
            } catch (e: SAXParseException) {
                return@thread
            }
//            Log.d("updateSpeck: ", "doc: $doc")
            val xPath = XPathFactory.newInstance().newXPath()

    //            generateIconUrl()
//            mProperties?.set("xml_friendly_name", xPath.compile("//friendlyName").evaluate(doc))
//            mFriendlyName = mProperties?.get("xml_friendly_name").toString()
            mFriendlyName = xPath.compile("//friendlyName").evaluate(doc)
    //            mFriendlyName = mProperties!!["xml_friendly_name"]
//            Devices.friendlyName = mFriendlyName

            Devices.deviceList[loc.toString()]?.urlBase = xPath.compile("//URLBase").evaluate(doc)
            Devices.deviceList[loc.toString()]?.iconUrl = "${loc.protocol}://${loc.host}:${loc.port}${xPath.compile("//icon/url").evaluate(doc)}"
            Devices.deviceList[loc.toString()]?.friendlyName = mFriendlyName
            Devices.deviceList[loc.toString()]?.deviceType = xPath.compile("//deviceType").evaluate(doc)
            if (xPath.compile("//presentationURL").evaluate(doc) == "") {
                Devices.deviceList[loc.toString()]?.presentationUrl = loc.toString()
            } else {
                Devices.deviceList[loc.toString()]?.presentationUrl = xPath.compile("//presentationURL").evaluate(doc)
            }

            Log.d("updateSpeck: ", "friendly: ${Devices.deviceList[loc.toString()]?.friendlyName}")
            Log.d("updateSpeck: ", "iconUrl: ${Devices.deviceList[loc.toString()]?.iconUrl}")
            Log.d("updateSpeck: ", "presentationURL: ${Devices.deviceList[loc.toString()]?.presentationUrl}")
            Log.d("updateSpeck: ", "deviceType: ${Devices.deviceList[loc.toString()]?.deviceType}")
        }
    }
}