package com.inu.musicviewpager2.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_bookmark")
//@Parcelize
data class Music(
    @PrimaryKey //(autoGenerate = true)
    var id: Int,
    var album:String,
    var title:String?,
    var artist:String?,
    var albumId:Long?,
    var duration: Long?,
    var albumUri: Uri?,
    var path : String?,
    var genre: String?,
    var isSelected: Boolean, // playing song
    var isBookmarked: Boolean
) {

}