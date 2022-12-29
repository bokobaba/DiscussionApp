package com.love.discussionapp.feature_home.presentation

sealed class HomeScreenEvent {
    data class SelectCommunity(val name: String): HomeScreenEvent()
}