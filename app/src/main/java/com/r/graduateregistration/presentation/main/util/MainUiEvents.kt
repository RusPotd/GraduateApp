package com.r.graduateregistration.presentation.main.util


sealed class MainUiEvents {
    object OnWelcome: MainUiEvents()
    object OnLoggedIn: MainUiEvents()
}
