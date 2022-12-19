package com.inu.musicviewpager2.model

import android.net.Uri

//@Parcelize
data class Music(
    var id:Int,
    var title:String?,
    var artist:String?,
    var albumId:Long?,
    var duration: Long?,
    var albumUri: Uri?,
    var path : String?,
    var genre: String?
) //: Parcelable