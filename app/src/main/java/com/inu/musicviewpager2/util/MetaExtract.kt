package com.inu.musicviewpager2.util

import com.inu.musicviewpager2.model.MusicDevice.song
import com.mpatric.mp3agic.Mp3File

class MetaExtract {

   fun getLyric(): String {
       val lyric: String? = try {
           val mp3file= Mp3File(song?.path)
           val id3v2Tag = mp3file.id3v2Tag
           id3v2Tag.lyrics
       } catch( e: java.lang.Exception){
           "\n\n     error!!     "
       } finally{
//           println("id3v2Tag.lyrics error!!")
       }

        return lyric?: "\n\n    No lyric!    "
   }

    /*   // MP3 파일 가사 태그 읽기
try (
    Metadata metadata = new Metadata("path/audio-Lyrics.mp3")) {
        MP3RootPackage root = metadata.getRootPackageGeneric();
        if (root.getLyrics3V2() != null) {
            System.out.println(root.getLyrics3V2().getLyrics());
            System.out.println(root.getLyrics3V2().getAlbum());
            System.out.println(root.getLyrics3V2().getArtist());
            System.out.println(root.getLyrics3V2().getTrack());
            // ...

            // 마찬가지로 태그 필드를 순회할 수 있습니다.
            for (LyricsField field : root.getLyrics3V2().toList()) {
                System.out.println(String.format("%s = %s", field.getID(), field.getData()));
            }
        }
    }*/
}