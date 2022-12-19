package com.inu.musicviewpager2

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.loader.content.CursorLoader
import com.inu.musicviewpager2.model.Music
import com.inu.musicviewpager2.util.MyApplication

object MusicProvider {

    var isMusicAllCalled = false
    // 컨텐트 리졸버로 음원 목록 가져오기
    // 1. 데이터 테이블 주소
    private val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    var musicList = mutableListOf<Music>()
    var genreList = mutableListOf<String>()
    // 2. 가져올 데이터 컬컴 정의
    private val proj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        arrayOf(
            MediaStore.Audio.AudioColumns._ID, // 0
            MediaStore.Audio.AudioColumns.TITLE, // 1
            MediaStore.Audio.AudioColumns.ARTIST,// 2
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 3
            MediaStore.Audio.AudioColumns.DURATION, // 4
            MediaStore.Audio.AudioColumns.DATA, // 5
            MediaStore.Audio.AudioColumns.GENRE //6 정은폰에러?
        )
    } else {
        arrayOf(
            MediaStore.Audio.AudioColumns._ID, // 0
            MediaStore.Audio.AudioColumns.TITLE, // 1
            MediaStore.Audio.AudioColumns.ARTIST,// 2
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 3
            MediaStore.Audio.AudioColumns.DURATION, // 4
            MediaStore.Audio.AudioColumns.DATA // 5
//            MediaStore.Audio.AudioColumns.GENRE //6 정은폰에러?
        )
    }

    fun getMusicList(context: Context): List<Music> {
        //3.  컨텐트 리졸버에 해당 데이터 요청
        val cursor = context.contentResolver.query(musicUri, proj, null, null,
            MediaStore.Audio.Media.ARTIST + " ASC")
        // 4. 커서로 전달받은 데이터를 꺼내서 저장

        Log.d("", "${cursor!!}")
        //    val defaultUri = Uri.parse("android.resource://com.inu.contentresolver/drawable/resource01")
        while (cursor.moveToNext()) {
            val title = cursor.getString(1)
            val duration = cursor.getLong(4)
            if (duration > 10000 && !title.contains("통화 녹음")) {  // 약 2분 이하 곡 제외
                val id = cursor.getInt(0)
                val artist = cursor.getString(2)
                val albumId = cursor.getLong(3) //Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) //cursor!!.getString(3)
                val albumUri = Uri.parse("content://media/external/audio/albumart/$albumId")
                val path = cursor.getString(5)
                val genre = cursor.getString(6)
                //   i += 1
//                Log.d("genreData","$path")

                val music = Music(id, title, artist, albumId, duration, albumUri, path, genre) //, albumArtBit)
                musicList.add(music)
            }
        }

        isMusicAllCalled = true
        cursor.close()

        return  musicList.shuffled()
    }

    fun getGenre(context: Context):List<Music> {
        val projGenre = arrayOf(
            MediaStore.Audio.Genres._ID, // 0
            MediaStore.Audio.Genres.NAME // 1
        )
        val musicUri: Uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI // MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val genreCursor = context.contentResolver.query(musicUri,projGenre, null, null,MediaStore.Audio.Genres.DEFAULT_SORT_ORDER)
        val idIndex: Int = genreCursor?.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) ?: 0
        // 4. 커서로 전달받은 데이터를 꺼내서 저장

        var i = 0
        //    val defaultUri = Uri.parse("android.resource://com.inu.contentresolver/drawable/resource01")
        if (genreCursor != null) {
            while (genreCursor.moveToNext()) {

                val id: Int = genreCursor.getLong(idIndex).toInt()
                val genreId  = genreCursor.getLong(genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
                val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
                val songCursor = context.contentResolver.query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    proj,
                    MediaStore.Audio.AudioColumns.IS_MUSIC + "=1",
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
//                val songIndex: Int = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
                var genre = genreCursor.getString(1)

                while (songCursor!!.moveToNext()) {
                    val title = songCursor.getString(1)
                    val duration = songCursor.getLong(4)
                    if (duration > 10000 && !title.contains("통화 녹음")) {  // 약 2분 이하 곡 제외
                        val id = songCursor.getInt(0)
                        val artist = songCursor.getString(2)
                        val albumId = songCursor.getLong(3) //Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) //cursor!!.getString(3)
                        val albumUri = Uri.parse("content://media/external/audio/albumart/$albumId")
                        val path = songCursor.getString(5)

                        val music = Music(id, title, artist, albumId, duration, albumUri, path, genre) //, albumArtBit)
                        musicList.add(music)
                    }
                }

                songCursor.close()
            }
            genreCursor?.close()
        }
        return  musicList.shuffled()
    }
    fun getGenresCursor(context: Context): CursorLoader {
        val uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME
        )
        val orderBy = MediaStore.Audio.Genres.NAME
        return CursorLoader(context, uri, columns, null, null, orderBy)
    }

    fun getNumberSongsOfGenre(genreID: Long, VOLUMENAME: String): Int {
        val uri = MediaStore.Audio.Genres.Members.getContentUri(
            VOLUMENAME,
            genreID
        )
        val c: Cursor? = MyApplication.applicationContext().contentResolver.query(uri, null, null, null, null)
        if (c == null || c.getCount() === 0) return -1
        val num: Int = c.getCount()
        c.close()
        return num
    }
}
