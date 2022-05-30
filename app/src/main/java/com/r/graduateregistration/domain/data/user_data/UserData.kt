package com.r.graduateregistration.domain.data.user_data

import com.r.graduateregistration.domain.models.UserDetails

interface UserData {

    suspend fun isMobileNumAlreadyRegister(phoneNumber: String) : Boolean

    fun addUserData(userDetails: UserDetails)

    suspend fun getUserData(userId: String) : UserDetails


}