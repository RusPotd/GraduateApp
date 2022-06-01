package com.r.graduateregistration.domain.models

data class GraduateData(
    val id: Long,
    val name: String,
    val gender: String,
    val mobile: String,
    val district: String,
    val taluka: Any? = null,
    val refer: Long,
)
