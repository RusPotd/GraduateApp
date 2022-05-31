package com.r.graduateregistration.presentation.main.util

import com.r.graduateregistration.domain.models.UserDetails


sealed class MainUiEvents {
    object OnWelcome: MainUiEvents()
    object OnLoggedIn: MainUiEvents()
    data class UpdateUser(val userDetails: UserDetails) : MainUiEvents()

}
