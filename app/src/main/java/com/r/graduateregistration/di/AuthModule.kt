package com.r.graduateregistration.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.r.graduateregistration.domain.data.login.LoginRepository
import com.r.graduateregistration.domain.data.login.LoginRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideLoginRepository(auth: FirebaseAuth): LoginRepository {
        return LoginRepositoryImpl(auth = auth)
    }


}