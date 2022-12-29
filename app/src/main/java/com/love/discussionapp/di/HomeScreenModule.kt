package com.love.discussionapp.di

import com.love.discussionapp.feature_home.presentation.HomeState
import com.love.discussionapp.feature_home.presentation.IHomeState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeScreenModule {
    @Binds
    @Singleton
    abstract fun bindHomeScreenState(homeState: HomeState): IHomeState
}