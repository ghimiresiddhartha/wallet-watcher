package com.siddhartha.walletwatcher.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.siddhartha.walletwatcher.BuildConfig
import com.siddhartha.walletwatcher.framework.local.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Singleton
    @Provides
    @Named("shared")
    fun provideSharedPreference(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            BuildConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideRoomDb(
        @ApplicationContext context: Context
    ): AppDb = Room.databaseBuilder(
        context, AppDb::class.java, BuildConfig.DATABASE_NAME
    ).fallbackToDestructiveMigration().build()
}