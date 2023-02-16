package com.example.fotobudka.dataM.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="settings")

data class Settings (
    @PrimaryKey(autoGenerate=true) var _id:Int?,
    @ColumnInfo(name="photosNumber")var photosNumber: Int?,
    @ColumnInfo(name="intervalBetween")var intervalBetween: Int?,
    @ColumnInfo(name="actualBanner")var actualBanner: Int?,
    @ColumnInfo(name="actualFilter")var actualFilter: Int?){

    @Ignore
    constructor(photosNumber: Int, intervalBetween: Int, actualBanner: Int, actualFilter: Int) : this (null, photosNumber, intervalBetween, actualBanner, actualFilter)
}