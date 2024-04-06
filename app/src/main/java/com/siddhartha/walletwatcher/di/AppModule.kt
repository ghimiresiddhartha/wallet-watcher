package com.siddhartha.walletwatcher.di

import com.siddhartha.walletwatcher.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
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