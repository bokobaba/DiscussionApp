package com.love.discussionapp.feature_community.domain.model

data class Community(
    val name: String,
    val bannerUrl: String,
    val subscribed: Boolean,
    val subscribers: Int,
)
