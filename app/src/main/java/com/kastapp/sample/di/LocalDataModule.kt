package com.kastapp.sample.di

import androidx.room.Room
import com.kastapp.sample.BuildConfig
import com.kastapp.sample.data.local.db.AppDatabase
import com.kastapp.sample.data.local.prefs.Preferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object LocalDataModule {
    val module = module {
        single { Preferences(androidContext()) }
        single {
            Room.databaseBuilder(
                androidApplication(),
                AppDatabase::class.java,
                BuildConfig.DB_NAME
            ).build()
        }
        single { get<AppDatabase>().feedDao() }
    }
}
