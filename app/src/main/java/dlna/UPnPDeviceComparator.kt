package dlna

import dlna.model.UpnpDevice
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.Nullable
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException

class UPnPDeviceComparator: Comparator<UpnpDevice> {
    override fun compare(lhs: UpnpDevice?, rhs: UpnpDevice?): Int {
        // Handle null objects

        // Handle null objects
        var compare: Int = compareNull(lhs, rhs)
        if (compare == 0 && lhs == null) {
            return compare
        }

        val mine: URL? = lhs?.location
        val hers: URL? = rhs?.location
        compare = compareNull(mine, hers)
        if (compare == 0 && lhs == null) {
            return compare
        }

        // Compare ip addresses

        // Compare ip addresses
        compare = compareInetAddresses(mine!!, hers!!)
        if (compare != 0) {
            return compare
        }

        // Compare ports

        // Compare ports
        compare = mine.port - hers.port
        return if (compare != 0) {
            compare
        } else mine.path.compareTo(hers.path)

        // String compare paths

        // String compare paths
    }

    ///////////////////////////////////////////////////////////////////////////
    // Null
    // a "null" object is less than a populated one
    // If they're both null or both non-null, then they're equal
    ///////////////////////////////////////////////////////////////////////////
    fun compareNull(@Nullable lhs: Any?, @Nullable rhs: Any?): Int {
        if (lhs == null) {
            return if (rhs == null) 0 else -1
        } else if (rhs == null) {
            return 1
        }
        return 0
    }

    ///////////////////////////////////////////////////////////////////////////
    // IP Address
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // IP Address
    ///////////////////////////////////////////////////////////////////////////
    fun compareInetAddresses(@NonNull lhs: URL, @NonNull rhs: URL): Int {
        var mine: InetAddress? = null
        var hers: InetAddress? = null
        try {
            mine = InetAddress.getByName(lhs.host)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        try {
            hers = InetAddress.getByName(rhs.host)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        val compare = compareNull(mine, hers)
        return if (compare == 0 && mine == null) {
            0
        } else compareInetAddress(mine, hers)
    }

    fun compareInetAddress(@NonNull adr1: InetAddress?, @NonNull adr2: InetAddress?): Int {
        val ba1 = adr1!!.address
        val ba2 = adr2!!.address

        // general ordering: ipv4 before ipv6
        if (ba1.size < ba2.size) return -1
        if (ba1.size > ba2.size) return 1

        // we have 2 ips of the same type, so we have to compare each byte
        for (i in ba1.indices) {
            val b1 = unsignedByteToInt(ba1[i])
            val b2 = unsignedByteToInt(ba2[i])
            if (b1 == b2) continue
            return if (b1 < b2) -1 else 1
        }
        return 0
    }

    private fun unsignedByteToInt(b: Byte): Int {
        return b.toInt() and 0xFF
    }
}

/*

class UPnPDeviceComparator: Comparator<UPnPDevice> {
    override fun compare(lhs: UPnPDevice?, rhs: UPnPDevice?): Int {
        // Handle null objects

        // Handle null objects
        var compare: Int = compareNull(lhs, rhs)
        if (compare == 0 && lhs == null) {
            return compare
        }

        val mine: URL? = lhs?.getLocation()
        val hers: URL? = rhs?.getLocation()
        compare = compareNull(mine, hers)
        if (compare == 0 && lhs == null) {
            return compare
        }

        // Compare ip addresses

        // Compare ip addresses
        compare = compareInetAddresses(mine!!, hers!!)
        if (compare != 0) {
            return compare
        }

        // Compare ports

        // Compare ports
        compare = mine.port - hers.port
        return if (compare != 0) {
            compare
        } else mine.path.compareTo(hers.path)

        // String compare paths

        // String compare paths
    }

    ///////////////////////////////////////////////////////////////////////////
    // Null
    // a "null" object is less than a populated one
    // If they're both null or both non-null, then they're equal
    ///////////////////////////////////////////////////////////////////////////
    fun compareNull(@Nullable lhs: Any?, @Nullable rhs: Any?): Int {
        if (lhs == null) {
            return if (rhs == null) 0 else -1
        } else if (rhs == null) {
            return 1
        }
        return 0
    }

    ///////////////////////////////////////////////////////////////////////////
    // IP Address
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // IP Address
    ///////////////////////////////////////////////////////////////////////////
    fun compareInetAddresses(@NonNull lhs: URL, @NonNull rhs: URL): Int {
        var mine: InetAddress? = null
        var hers: InetAddress? = null
        try {
            mine = InetAddress.getByName(lhs.host)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        try {
            hers = InetAddress.getByName(rhs.host)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        }
        val compare = compareNull(mine, hers)
        return if (compare == 0 && mine == null) {
            0
        } else compareInetAddress(mine, hers)
    }

    fun compareInetAddress(@NonNull adr1: InetAddress?, @NonNull adr2: InetAddress?): Int {
        val ba1 = adr1!!.address
        val ba2 = adr2!!.address

        // general ordering: ipv4 before ipv6
        if (ba1.size < ba2.size) return -1
        if (ba1.size > ba2.size) return 1

        // we have 2 ips of the same type, so we have to compare each byte
        for (i in ba1.indices) {
            val b1 = unsignedByteToInt(ba1[i])
            val b2 = unsignedByteToInt(ba2[i])
            if (b1 == b2) continue
            return if (b1 < b2) -1 else 1
        }
        return 0
    }

    private fun unsignedByteToInt(b: Byte): Int {
        return b.toInt() and 0xFF
    }
}*/
