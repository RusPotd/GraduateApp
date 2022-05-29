package com.r.graduateregistration.presentation.login.util

sealed class AuthEvents{
    object OnRegister: AuthEvents()
    object OnLogin: AuthEvents()
}
