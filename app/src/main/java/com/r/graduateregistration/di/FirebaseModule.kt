package com.r.graduateregistration.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.r.graduateregistration.domain.data.user_data.UserData
import com.r.graduateregistration.domain.data.user_data.UserDataImpl
import com.r.graduateregistration.domain.data.user_data.UserMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    @Named("details")
    fun provideUserRef(db: FirebaseFirestore) = db.collection("user_details")

    @Provides
    @Singleton
    @Named("withId")
    fun provideUserRefWithId(db: FirebaseFirestore) = db.collection("user_details_with_id")

    @Provides
    @Singleton
    fun provideUserDataRepository(
        firestore: FirebaseFirestore,
        @Named("details")  userRef: CollectionReference,
        @Named("withId") userRefWithId: CollectionReference,
        mapper: UserMapper
    ): UserData = UserDataImpl(firestore,userRef, userRefWithId, mapper)



}