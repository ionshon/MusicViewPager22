package dlna

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.inu.musicviewpager2.databinding.ActivityAbout2Binding
import dlna.model.UpnpDevice
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kotlin.concurrent.thread


class AboutActivity : AppCompatActivity() {
    val binding by lazy { ActivityAbout2Binding.inflate(layoutInflater) }

    private var mWebView: WebView? = null
//    private val mWebSettings: WebSettings? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mWebView = binding.webviewLayout
        val upnpDevice = intent.getParcelableExtra<UpnpDevice>("key0")
        val locPresen = intent.getStringExtra("key1") //"http://192.168.0.12:38520"//
        if (upnpDevice != null) {
            mWebView?.loadUrl(upnpDevice.location.toString())
            binding.textViewUrl.text = upnpDevice.location.toString()
            Log.d("textViewUrl:", " => ${upnpDevice.location}")
        } else if (locPresen != null) {
//            loadContents(locPresen)
            mWebView?.loadUrl(locPresen)
        } //else if (locPresen != null && )

    }

    fun loadContents(loc: String) {
        Log.d("updateSpeck", "loc: $loc")
        val mClient = OkHttpClient()
        var mRawXml: String?
//        var mProperties: HashMap<String, String>? = null
        var mFriendlyName: String
        val postBody = "<?xml version=\"1.0\"?>\n" +
                "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "  <s:Body>\n" +
                "    <u:Browse xmlns:u=\"urn:schemas-upnp-org:service:ContentDirectory:1\">\n" +
                "      <ObjectID>a</ObjectID>\n" +
                "      <BrowseFlag>BrowseDirectChildren</BrowseFlag>\n" +
                "      <Filter>*</Filter>\n" +
                "      <StartingIndex>0</StartingIndex>\n" +
                "      <RequestedCount>0</RequestedCount>\n" +
                "      <SortCriteria></SortCriteria>\n" +
                "    </u:Browse>\n" +
                "  </s:Body>\n" +
                "</s:Envelope>"
        val requestBody: RequestBody = postBody
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        thread { // 여기서는 스레트 안해도 오류 안나지만 받아오지 못함
            val request: Request = Request.Builder()
                .url((loc + "/service/ContentDirectory_control"))
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("SOAPAction", "\\\"urn:schemas-upnp-org:service:ContentDirectory:1#Browse\\\"")
                .post(requestBody)
                .build()
            Log.d("updateSpeck", "request: $request")
            val response = mClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.d("updateSpeck", "response.isSuccessful: $response")
                throw IOException("Unexpected code $response")
            }
            mRawXml = response.body.string()
            Log.d("updateSpeck", "mRawXml: $mRawXml")
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
   /*         mFriendlyName = xPath.compile("//friendlyName").evaluate(doc)
            //            mFriendlyName = mProperties!!["xml_friendly_name"]
//            Devices.friendlyName = mFriendlyName
            Devices.deviceList[loc.toString()]?.iconUrl = "${loc.protocol}://${loc.host}:${loc.port}${xPath.compile("//icon/url").evaluate(doc)}"
            Devices.deviceList[loc.toString()]?.friendlyName = mFriendlyName
            Devices.deviceList[loc.toString()]?.deviceType = xPath.compile("//deviceType").evaluate(doc)
            if (xPath.compile("//presentationURL").evaluate(doc) == "") {
                Devices.deviceList[loc.toString()]?.presentationUrl = "maybe mediaplayer"
            } else {
                Devices.deviceList[loc.toString()]?.presentationUrl = xPath.compile("//presentationURL").evaluate(doc)
            }

            Log.d("updateSpeck: ", "friendly: ${Devices.deviceList[loc.toString()]?.friendlyName}")
            Log.d("updateSpeck: ", "iconUrl: ${Devices.deviceList[loc.toString()]?.iconUrl}")
            Log.d("updateSpeck: ", "presentationURL: ${Devices.deviceList[loc.toString()]?.presentationUrl}")
            Log.d("updateSpeck: ", "deviceType: ${Devices.deviceList[loc.toString()]?.deviceType}")
      */  }
    }
}