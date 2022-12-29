package com.love.discussionapp.feature_main.presentation

sealed class MainScreenEvent {
    object Login: MainScreenEvent()
    object Logout: MainScreenEvent()
    object ShowPopup: MainScreenEvent()
    object DismissPopup: MainScreenEvent()
    data class SaveProfileImage(val url: String): MainScreenEvent()
}