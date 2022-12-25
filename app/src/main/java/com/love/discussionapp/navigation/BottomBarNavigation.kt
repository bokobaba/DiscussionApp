package com.love.discussionapp.navigation

import com.love.discussionapp.R

sealed class BottomBarNavigation (
    val route: String,
    val title: String,
    val icon: Int,
) {
    object Home : BottomBarNavigation(
        route = HOME_ROUTE,
        icon = R.drawable.ic_home,
        title = "Home"
    )

    object Account: BottomBarNavigation(
        route = ACCOUNT_ROUTE,
        icon = R.drawable.ic_account,
        title = "Account"
    )
}