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
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
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
        return context.getSharedPreferences(BuildConfig.SHARED_PREF_NAME,
            Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRoomDb(
        @ApplicationContext context: Context
    ): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        "DATABASE_NAME"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    @Named("encrypt")
    fun provideCipherEncryptInstance(): Cipher {
        val cipher = Cipher.getInstance(BuildConfig.ALGORITHM)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), BuildConfig.MODE),
            IvParameterSpec(BuildConfig.IV_KEY.toByteArray())
        )
        return cipher
    }

    @Provides
    @Singleton
    @Named("decrypt")
    fun provideCipherDecryptInstance(): Cipher {
        val cipher = Cipher.getInstance(BuildConfig.ALGORITHM)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(BuildConfig.SECRET_KEY.toByteArray(), BuildConfig.MODE),
            IvParameterSpec(BuildConfig.IV_KEY.toByteArray())
        )
        return cipher
    }
}