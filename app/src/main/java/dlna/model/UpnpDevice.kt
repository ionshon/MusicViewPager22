package dlna.model

import android.os.Parcel
import android.os.Parcelable
import java.net.URL

class UpnpDevice() : Parcelable  {
    var iconUrl: String = ""
    var location: URL? = null
    var urlBase: String = ""
    var server: String = ""
    var friendlyName: String = ""
    var cachedIconUrl: String = ""
    var presentationUrl: String = ""
    var deviceType: String = ""

    constructor(parcel: Parcel) : this() {
        iconUrl = parcel.readString()!!
        location = URL(parcel.readString())
        server = parcel.readString()!!
        urlBase = parcel.readString()!!
        friendlyName = parcel.readString()!!
        cachedIconUrl = parcel.readString()!!
        presentationUrl = parcel.readString()!!
        deviceType = parcel.readString()!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(iconUrl)
        dest.writeString(location.toString())
        dest.writeString(server)
        dest.writeString(urlBase)
        dest.writeString(friendlyName)
        dest.writeString(cachedIconUrl)
        dest.writeString(presentationUrl)
        dest.writeString(deviceType)
    }

    companion object CREATOR : Parcelable.Creator<UpnpDevice> {
        override fun createFromParcel(parcel: Parcel): UpnpDevice {
            return UpnpDevice(parcel)
        }

        override fun newArray(size: Int): Array<UpnpDevice?> {
            return arrayOfNulls(size)
        }
    }

}
