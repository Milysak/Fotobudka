package com.example.fotobudka.dataM.database

import androidx.room.*

@Dao
interface SettingsDao {
    @Query("SELECT photosNumber FROM settings")
    suspend fun getPhotosNumber(): Int

    @Query("SELECT intervalBetween FROM settings")
    suspend fun getIntervalBetween(): Int

    @Query("SELECT actualBanner FROM settings")
    suspend fun getActualBanner(): Int

    @Query("SELECT actualFilter FROM settings")
    suspend fun getActualFilter(): Int

    @Insert
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)

    @Delete
    suspend fun delete(settings: Settings)

    @Query("DELETE FROM settings")
    suspend fun deleteAll()
}