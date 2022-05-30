package com.r.graduateregistration.domain.data.user_data

import com.r.graduateregistration.domain.models.UserDetails

interface UserData {

    fun isMobileNumAlreadyRegister(phoneNumber: String) : Boolean

    fun addUserData(userDetails: UserDetails)

    fun getUserData(phoneNum: String) : UserDetails

    fun updateUserData(userDetails: UserDetails)

}