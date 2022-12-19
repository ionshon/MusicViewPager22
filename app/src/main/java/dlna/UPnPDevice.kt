package dlna


import java.net.MalformedURLException
import java.net.URL
import java.util.*

class UPnPDevice {

    var mRawUPnP: String? = null
    var mRawXml: String? = null
    var mLocation: URL? = null
    var mServer: String? = null
    var mFriendlyName: String? = null
    var mCachedIconUrl: String? = null

    lateinit var mProperties: HashMap<String, String>

    fun getInstance(raw: String): UPnPDevice? {
        val parsed = parseRaw(raw)
        return try {
            val device = UPnPDevice()
            device.mRawUPnP = raw
            device.mProperties = parsed
            device.mLocation = URL(parsed["upnp_location"])
            device.mServer = parsed["upnp_server"]

//            downloadSpecs()
//            device.mFriendlyName = mProperties!!["xml_friendly_name"]
            device
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    private fun parseRaw(raw: String): HashMap<String, String> {
        val results = HashMap<String, String>()
        for (line in raw.split("\r\n").toTypedArray()) {
            val colon = line.indexOf(":")
            if (colon != -1) {
                val key = line.substring(0, colon).trim { it <= ' ' }.lowercase(Locale.getDefault())
                val value = line.substring(colon + 1).trim { it <= ' ' }
                results["upnp_$key"] = value
            }
        }
        return results
    }
}

    ////////////////////////////////////////////////////////////////////////////////
    // UPnP Specification Downloading / Parsing
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // UPnP Specification Downloading / Parsing
    ////////////////////////////////////////////////////////////////////////////////
    /*
   @Transient
   private val mClient = OkHttpClient()

  @Throws(Exception::class)
   fun downloadSpecs() {
       thread {
           val request: Request = Request.Builder()
               .url(mLocation!!)
               .build()
           val response = mClient.newCall(request).execute()
           if (!response.isSuccessful) {
               throw IOException("Unexpected code $response")
           }
           mRawXml = response.body!!.string()
           val dbf = DocumentBuilderFactory.newInstance()
           val db = dbf.newDocumentBuilder()
           val source = InputSource(StringReader(mRawXml))
           val doc: Document
           doc = try {
               db.parse(source)
           } catch (e: SAXParseException) {
               return@thread
           }
//            Log.d("downloadSpeck: ", "$mRawXml")
           val xPath = XPathFactory.newInstance().newXPath()
           mProperties!!["xml_icon_url"] = xPath.compile("//icon/url").evaluate(doc)
//            generateIconUrl()
           mProperties!!["xml_friendly_name"] = xPath.compile("//friendlyName").evaluate(doc)
           mFriendlyName = mProperties["xml_friendly_name"]
//            mFriendlyName = mProperties!!["xml_friendly_name"]
           Log.d("downloadSpeck: ", "${mFriendlyName}")
       }
   }
*/
//    private fun UPnPDevice() {}
    /*
       fun getHost(): String {
           return mLocation!!.host
       }
      @Throws(UnknownHostException::class)
       fun getInetAddress(): InetAddress? {
           return InetAddress.getByName(getHost())
       }

       fun getLocation(): URL? {
           return mLocation
       }

       fun getRawUPnP(): String? {
           return mRawUPnP
       }

       fun getRawXml(): String? {
           return mRawXml
       }

       fun getServer(): String? {
           return mServer
       }

       fun getIconUrl(): String? {
           return mCachedIconUrl
       }

       fun generateIconUrl() { //: String? {
           var path = mProperties!!["xml_icon_url"]
           if (TextUtils.isEmpty(path)) {
               mCachedIconUrl = null
           }
           if (path!!.startsWith("/")) {
               path = path.substring(1)
           }
           mCachedIconUrl =
               mLocation!!.protocol + "://" + mLocation!!.host + ":" + mLocation!!.port + "/" + path
   //        return mCachedIconUrl
       }

       fun getFriendlyName(): String? {
           return mProperties!!["xml_friendly_name"]
       }

       fun getScrubbedFriendlyName(): String? {
           var friendlyNm = mProperties!!["xml_friendly_name"]
           Log.d("friendlyName in getScrubbed: ", "${friendlyNm}")

           // Special case for SONOS: remove the leading ip address from the friendly name
           // "192.168.1.123 - Sonos PLAY:1" => "Sonos PLAY:1"
           if (friendlyNm != null && friendlyNm.startsWith(getHost() + " - ")) {
               friendlyNm = friendlyNm.substring(getHost().length + 3)
           }
           return friendlyNm
       }
   */
