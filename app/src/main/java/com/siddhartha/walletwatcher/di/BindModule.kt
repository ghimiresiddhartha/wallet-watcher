package com.siddhartha.walletwatcher.di

import com.siddhartha.walletwatcher.data.datasource.OnBoardingDataSource
import com.siddhartha.walletwatcher.data.repository.OnBoardingRepositoryImpl
import com.siddhartha.walletwatcher.domain.repository.OnBoardingRepository
import com.siddhartha.walletwatcher.framework.datasource.OnBoardingDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    abstract fun bindOnBoardingDataSourceImpl(
        onBoardingDataSource: OnBoardingDataSourceImpl
    ): OnBoardingDataSource

    @Binds
    abstract fun provideOnBoardingRepositoryImpl(
        onBoardingRepository: OnBoardingRepositoryImpl
    ): OnBoardingRepository

}