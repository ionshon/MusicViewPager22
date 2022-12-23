package com.inu.musicviewpager2.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inu.musicviewpager2.model.Music

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Query("SELECT * FROM music_bookmark WHERE id like :i")
    fun  getMusic(i : Int) : Music

    @Query("SELECT * FROM music_bookmark")// ORDER BY music ASC")
    fun getAllMusics() : MutableList<Music>

    @Query("DELETE FROM music_bookmark")
    suspend fun delete()

    @Query("DELETE FROM music_bookmark")
    suspend fun deleteAllMusic()


}