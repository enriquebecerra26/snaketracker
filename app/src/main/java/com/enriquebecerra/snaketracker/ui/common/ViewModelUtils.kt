package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.enriquebecerra.snaketracker.SnakeTrackerApplication

@Composable
inline fun <reified VM : ViewModel> snakeTrackerViewModel(
    crossinline creator: (SnakeTrackerApplication) -> VM
): VM {
    val application = LocalContext.current.applicationContext as SnakeTrackerApplication
    val factory = viewModelFactory {
        initializer { creator(application) }
    }
    return viewModel(factory = factory)
}

@Composable
inline fun <reified VM : ViewModel> snakeTrackerViewModelWithSavedState(
    crossinline creator: (SnakeTrackerApplication, SavedStateHandle) -> VM
): VM {
    val application = LocalContext.current.applicationContext as SnakeTrackerApplication
    val factory = viewModelFactory {
        initializer { creator(application, createSavedStateHandle()) }
    }
    return viewModel(factory = factory)
}
