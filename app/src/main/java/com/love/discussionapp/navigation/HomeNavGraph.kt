package com.love.discussionapp.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.love.discussionapp.feature_home.presentation.HomeScreen

fun NavGraphBuilder.HomeNavGraph(navHostController: NavHostController) {
    navigation(
        startDestination = Screen.Home.route,
        route = HOME_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navHostController)
        }
        composable(
            route = Screen.Community.route,
            arguments = listOf(navArgument(COMMUNITY_NAME_ARG) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            it.arguments?.getString(COMMUNITY_NAME_ARG)?.let {
                //community screen
            }
        }
        composable(
            route = Screen.Post.route,
            arguments = listOf(navArgument(POST_ID_ARG) {
                type = NavType.StringType
                defaultValue = ""
            })
        ) {
            it.arguments?.getString(POST_ID_ARG)?.let {
                //community screen
            }
        }
    }
}