package com.love.discussionapp.di

import com.love.discussionapp.feature_community.presentation.CommunityState
import com.love.discussionapp.feature_community.presentation.ICommunityState
import com.love.discussionapp.feature_home.presentation.IHomeState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityScreenModule {
    @Binds
    @Singleton
    abstract fun bindCommunityScreenState(state: CommunityState): ICommunityState
}