package com.r.graduateregistration.di

import com.r.graduateregistration.domain.data.user_data.UserMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    companion object {
        @Singleton
        @Provides
        fun provideSellerMapper(): UserMapper {
            return UserMapper()
        }
    }
}