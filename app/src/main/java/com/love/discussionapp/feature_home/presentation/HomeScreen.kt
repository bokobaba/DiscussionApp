package com.love.discussionapp.feature_home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.love.discussionapp.R
import com.love.discussionapp.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel = hiltViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    HomeScreenContent(state = vm.state, onEvent = vm::onEvent)

    LaunchedEffect(key1 = true) {
        vm.eventFlow.collectLatest { event ->
            when (event) {
                is HomeViewModel.UiEvent.FetchDataError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is HomeViewModel.UiEvent.CommunitySelected -> {
                    navController.navigate(route = Screen.Community.passName(event.name))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: IHomeViewState,
    onEvent: (HomeScreenEvent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Button(onClick = { }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                title = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "home"
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CommunityList(state = state, onEvent = onEvent)
        }
    }
}

@Composable
fun CommunityList(state: IHomeViewState, onEvent: (HomeScreenEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier
    ) {
        items(state.communities) { c ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(HomeScreenEvent.SelectCommunity(c.name))
                    }
            ) {
                Text(text = c.name, style = MaterialTheme.typography.bodyMedium)
            }
            Divider()
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreenContent(
        state = HomeState.previewState,
        onEvent = {},
    )
}