package com.love.discussionapp.feature_main.presentation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.love.discussionapp.R
import com.love.discussionapp.navigation.BottomBarNavGraph
import com.love.discussionapp.navigation.BottomBarNavigation
import com.love.discussionapp.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel()) {
    Log.d("MainScreen", "init")
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    MainScreenContent(
        navController = navController,
        user = vm.auth.user,
        onEvent = vm::onEvent,
        scaffoldState = scaffoldState
    )

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is MainViewModel.UiEvent.Login -> {
                    Log.d("MainScreen", "navigating to login")
                    navController.navigate(Screen.Account.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scaffoldState.drawerState.close()
                }
                is MainViewModel.UiEvent.Logout -> {
                    vm.auth.logout()
                }
            }
        }
    }
}

@Composable
fun MainScreenContent(
    navController: NavHostController,
    user: FirebaseUser?,
    onEvent: (MainScreenEvent) -> Unit,
    scaffoldState: ScaffoldState
) {
    Log.d("MainScreenContent", "init")
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerContent(user = user, onEvent = onEvent)
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val coroutineScope: CoroutineScope = rememberCoroutineScope()
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(60.dp)
                    .padding(5.dp)
                    .zIndex(1000f)
                    .clickable {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                painter = painterResource(id = R.drawable.ic_account),
                contentDescription = "account"
            )
            BottomBarNavGraph(navHostController = navController)
        }
    }
}

@Composable
fun DrawerContent(user: FirebaseUser?, onEvent: (MainScreenEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_account),
                contentDescription = "profile image"
            )
            user.email?.let { Text(text = it) }
            Divider()
            Spacer(modifier = Modifier.height(100.dp))
            Button(onClick = { onEvent(MainScreenEvent.Logout) }) {
                Text(text = "Logout")
            }
        } else {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_account),
                contentDescription = "profile image"
            )
            Divider()
            Spacer(modifier = Modifier.height(100.dp))
            Button(onClick = { onEvent(MainScreenEvent.Login) }) {
                Text(text = "Login")
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    Log.d("BottomBar", "init")
    val navItems = listOf(
        BottomBarNavigation.Home,
        BottomBarNavigation.Account
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    BottomNavigation() {
        navItems.forEach { screen ->
            AddItem(
                navItem = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    navItem: BottomBarNavigation,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        label = { Text(text = navItem.title) },
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
//        selectedContentColor = MaterialTheme.colors.primary,
        icon = {
            Icon(
                painter = painterResource(id = navItem.icon),
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == navItem.route
        } == true,
        onClick = {
            navController.navigate(navItem.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

@Composable
@Preview
fun MainScreenPreview() {
    MainScreenContent(
        navController = rememberNavController(),
        user = null,
        onEvent = {},
        scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open))
    )
}