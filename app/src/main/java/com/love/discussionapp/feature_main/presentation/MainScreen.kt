package com.love.discussionapp.feature_main.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseUser
import com.love.discussionapp.R
import com.love.discussionapp.navigation.BottomBarNavGraph
import com.love.discussionapp.navigation.BottomBarNavigation
import com.love.discussionapp.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel()) {
    Log.d("MainScreen", "init")
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showPopup = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    MainScreenContent(
        navController = navController,
        user = vm.auth.user,
        onEvent = vm::onEvent,
        drawerState = drawerState,
        showPopup = showPopup,
        profileImage = vm.profileImage,
        profileName = vm.profileName
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
                    drawerState.close()
                }
                is MainViewModel.UiEvent.Logout -> {
                    vm.auth.logout()
                }
                is MainViewModel.UiEvent.DismissPopup -> showPopup.value = false
                is MainViewModel.UiEvent.ShowPopup -> showPopup.value = true
                is MainViewModel.UiEvent.SaveProfileImage -> {
                    showPopup.value = false
                    vm.auth.updateProfileImage(
                        url = event.url,
                        onSuccess = {
                            vm.updateProfileImage(event.url)
                        },
                        onFail = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Unable to Update Profile Image")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    navController: NavHostController,
    user: FirebaseUser?,
    onEvent: (MainScreenEvent) -> Unit,
    drawerState: DrawerState,
    showPopup: MutableState<Boolean>,
    profileImage: State<String>,
    profileName: State<String>,
) {
    Log.d("MainScreenContent", "init")

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(
            user = user,
            onEvent = onEvent,
            profileImage = profileImage,
            profileName = profileName,
        ) }
    ) {
        Scaffold(
            bottomBar = { BottomBar(navController = navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                AccountButton(
                    drawerState = drawerState,
                    navController = navController
                )
                BottomBarNavGraph(navHostController = navController)
            }
        }
    }

    ProfileImagePopup(showPopup = showPopup, imageUrl = profileImage.value, onEvent = onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.AccountButton(drawerState: DrawerState, navController: NavController) {
    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    if (navBackStackEntry?.destination?.route == Screen.Account.route) return

    Log.d("AccountButton", "init")
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    Icon(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(60.dp)
            .padding(5.dp)
            .zIndex(1000f)
            .clickable {
                Log.d("AccountButton", "click")
                coroutineScope.launch {
                    drawerState.open()
                }
            },
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "account"
    )
}

@Composable
fun DrawerContent(
    user: FirebaseUser?,
    profileName: State<String>,
    onEvent: (MainScreenEvent) -> Unit,
    profileImage: State<String>,
) {
    Log.d("DrawerContent", "init")
    Log.d("DrawerContent", "ImageUrl = ${user?.photoUrl}")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(profileImage = profileImage, onEvent = onEvent)
        Text(text = profileName.value)
        Divider()
        Spacer(modifier = Modifier.height(100.dp))

        if (user == null) {
            Button(onClick = { onEvent(MainScreenEvent.Login) }) {
                Text(text = "Login")
            }
        } else {
            Button(onClick = { onEvent(MainScreenEvent.Logout) }) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
fun ProfileImage(profileImage: State<String>, onEvent: (MainScreenEvent) -> Unit) {
    val imageModifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .clickable { onEvent(MainScreenEvent.ShowPopup) }

    if (profileImage.value != "") {
        AsyncImage(
            modifier = imageModifier,
            model = profileImage.value,
            contentScale = ContentScale.Crop,
            contentDescription = "profile image",
        )
    } else {
        Icon(
            modifier = imageModifier,
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "profile image"
        )
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    Log.d("BottomBar", "init")
    val navItems = listOf(
        BottomBarNavigation.Home,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar() {
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
    NavigationBarItem(
        label = { Text(text = navItem.title) },
//        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
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
fun ProfileImagePopup(
    showPopup: MutableState<Boolean>,
    imageUrl: String,
    onEvent: (MainScreenEvent) -> Unit
) {
    if (!showPopup.value) return
    Dialog(
        onDismissRequest = { onEvent(MainScreenEvent.DismissPopup) },
    ) {
        Surface() {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var url by remember { mutableStateOf(imageUrl) }
                OutlinedTextField(
                    label = { Text(text = "Profile Image Url") },
                    value = url,
                    maxLines = 1,
                    onValueChange = {
                        url = it
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { onEvent(MainScreenEvent.DismissPopup) }) {
                        Text(text = "Cancel")
                    }
                    Button(onClick = { onEvent(MainScreenEvent.SaveProfileImage(url)) }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainScreenPreview() {
    MainScreenContent(
        navController = rememberNavController(),
        user = null,
        onEvent = {},
        drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
        showPopup = remember { mutableStateOf(true) },
        profileImage = remember { mutableStateOf("") },
        profileName = remember { mutableStateOf("Love") }
    )
}