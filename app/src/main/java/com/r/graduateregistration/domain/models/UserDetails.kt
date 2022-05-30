package com.r.graduateregistration.domain.models

data class UserDetails(
    val userId: String ="",
    val fullName: String="",
    val mobileNumber: String="",
    val city: String = "",
    val taluka: String = "",
    val profileImg: String = "",
    val email: String = "",
    val dist: String = "",
)
