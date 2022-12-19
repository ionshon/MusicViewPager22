package dlna

import android.util.Log
import dlna.model.UpnpDevice
import dlna.utils.UpdateSpecs
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import java.io.IOException
import java.net.*
import java.util.*
import java.util.regex.Pattern

class UPnPDeviceFinder {

    private val TAG = UPnPSocket::class.java.name
    private var mInetDeviceAdr: InetAddress? = null
    private var mSock: UPnPSocket? = null

    fun setMSock(IPV4: Boolean) {
       mInetDeviceAdr = getDeviceLocalIP(IPV4)
//        Log.e(TAG, "IP is: $mInetDeviceAdr")
        try {
            mSock = UPnPSocket(mInetDeviceAdr) // 클래스 변수
//            println("SetmSock:  ${mSock}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun observe(): Observable<UpnpDevice> { // 관찰할 수 있는 <UPnPDevice> 를 에미트함
        return Observable.create(object : ObservableOnSubscribe<UpnpDevice> { // 데이터 생산
            override fun subscribe(emitter: ObservableEmitter<UpnpDevice>) {
                setMSock(true)
                if (mSock == null) {
                    emitter.onError(Exception("socket is null...")) // 에러 잡고
                    println("msock = null($mSock")
                    return
                }
                try {
                    // Broadcast SSDP search messages
                    mSock!!.sendMulticastMsg()

                    // Listen to responses from network until the socket timeout
                    while (true) {
                        Log.i(TAG, "wait for dev. response")
                        val dp = mSock!!.receiveMulticastMsg()
                        var receivedString = String(dp.data)
                        receivedString = receivedString.substring(0, dp.length)
                        Log.i(TAG, "found dev: $receivedString")
                        val uPnPDevice: UPnPDevice? = UPnPDevice().getInstance(receivedString)
                        UpdateSpecs.updateSpecs(uPnPDevice?.mLocation!!)

                        val upnpDevice = UpnpDevice()
                        upnpDevice.location = uPnPDevice.mLocation
                        upnpDevice.server = uPnPDevice.mServer.toString()
//                        upnpDevice.friendlyName = Devices.friendlyName
                        upnpDevice.cachedIconUrl = uPnPDevice.mCachedIconUrl.toString()

//                        Devices.deviceList[upnpDevice.location.toString()] = upnpDevice

                        emitter.onNext(upnpDevice)  // onNext 잡고
                    }
                } catch (e: IOException) {
                    //sock timeout will get us out of the loop
                    Log.e(TAG, "time out")
                    mSock!!.close()
                    emitter.onComplete()  // onComplete 잡고
                }
            }
        })
    }

    val MULTICAST_ADDRESS = "239.255.255.250"
    val PORT = 1900
    val MAX_REPLY_TIME = 60
    val MSG_TIMEOUT = MAX_REPLY_TIME * 1000 + 1000
    ////////////////////////////////////////////////////////////////////////////////
    // UPnPSocket
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // UPnPSocket
    ////////////////////////////////////////////////////////////////////////////////
    inner class UPnPSocket(deviceIp: InetAddress?) {
        private val mMulticastGroup: SocketAddress
        private val mMultiSocket: MulticastSocket?
        private val  upnpDeviceFinder: UPnPDeviceFinder = UPnPDeviceFinder()

        init {
            Log.e(TAG, "UPnPSocket")
            mMulticastGroup = InetSocketAddress(MULTICAST_ADDRESS, PORT)
            mMultiSocket = MulticastSocket(InetSocketAddress(deviceIp, 5656))
            mMultiSocket.setSoTimeout(MSG_TIMEOUT)
        }

        @Throws(IOException::class)
        fun sendMulticastMsg() {
            val ssdpMsg: String =  upnpDeviceFinder.buildSSDPSearchString()
            Log.i(TAG, "sendMulticastMsg: $ssdpMsg")
            val dp = DatagramPacket(ssdpMsg.toByteArray(), ssdpMsg.length, mMulticastGroup)
            mMultiSocket!!.send(dp)
        }

        @Throws(IOException::class)
        fun receiveMulticastMsg(): DatagramPacket {
            val buf = ByteArray(2048)
            val dp = DatagramPacket(buf, buf.size)
            mMultiSocket!!.receive(dp)
            return dp
        }

        /**
         * Closing the Socket.
         */
        fun close() {
            mMultiSocket?.close()
        }
/*
        companion object {
            private val TAG = UPnPSocket::class.java.name
        }*/
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Utils
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // Utils
    ////////////////////////////////////////////////////////////////////////////////
    val NEWLINE = "\r\n"

    private fun buildSSDPSearchString(): String {
        val content = StringBuilder()
        content.append("M-SEARCH * HTTP/1.1").append(NEWLINE)
        content.append("Host: $MULTICAST_ADDRESS:$PORT").append(NEWLINE)
        content.append("Man:\"ssdp:discover\"").append(NEWLINE)
        content.append("MX: $MAX_REPLY_TIME").append(NEWLINE)
        content.append("ST: upnp:rootdevice").append(NEWLINE)
        content.append(NEWLINE)
//        Log.i(TAG, content.toString())
        return content.toString()
    }

    private fun getDeviceLocalIP(useIPv4: Boolean): InetAddress? {
//        Log.i(TAG, "getDeviceLocalIP")
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
//            println("interfaces: $interfaces")
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
//                    println("addrs: $addr")
                    if (!addr.isLoopbackAddress) {
//                        Log.e(TAG, "IP from inet is: $addr")
                        val sAddr = addr.hostAddress?.uppercase(Locale.getDefault())
                        val isIPv4 = isIPv4Address(sAddr!!)
                        if (useIPv4) {
                            if (isIPv4) {
                                Log.e(TAG, "IP v4, $sAddr")
                                return addr
                            }
                        } else {
                            if (!isIPv4) {
                                Log.e(TAG, "IP v6, $sAddr")
                                //int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                //return delim<0 ? sAddr : sAddr.substring(0, delim);
                                return addr
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("에러:", "getDeviceLocalIP 에러")
        } // for now eat exceptions
        return null
    }

    // From Apache InetAddressUtils
    // https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/conn/util/InetAddressUtils.html
    private val IPV4_PATTERN =
        Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$")

    private fun isIPv4Address(input: String): Boolean {
        return IPV4_PATTERN.matcher(input).matches()
    }
}