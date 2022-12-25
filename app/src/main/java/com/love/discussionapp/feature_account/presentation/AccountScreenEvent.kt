package com.love.discussionapp.feature_account.presentation

sealed class AccountScreenEvent {
    data class Login(val email: String, val password: String): AccountScreenEvent()
    data class CreateAccount(val email: String, val password: String): AccountScreenEvent()
    object Back: AccountScreenEvent()
}