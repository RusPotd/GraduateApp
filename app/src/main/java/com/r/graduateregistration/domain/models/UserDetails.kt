package com.r.graduateregistration.domain.models

data class UserDetails(
    val userId: String ="",
    val universityName: String ="",
    val fullName: String="",
    val mobileNumber: String="",
    val email: String = "",
    val district: String = "",
    val taluka: String = "",
    val profileImg: String = "",
    val uniqueID: String = ""
)
