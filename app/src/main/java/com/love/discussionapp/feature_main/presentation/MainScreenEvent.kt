package com.love.discussionapp.feature_main.presentation

sealed class MainScreenEvent {
    object Login: MainScreenEvent()
    object Logout: MainScreenEvent()
}