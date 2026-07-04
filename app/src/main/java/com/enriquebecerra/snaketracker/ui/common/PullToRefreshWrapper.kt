package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.delay

/**
 * Como todas las listas de la app están respaldadas por Flows de Room (se actualizan solas ante
 * cualquier cambio en la base de datos), no existe una operación de "refresco" real que ejecutar.
 * Este wrapper ofrece el gesto de pull-to-refresh como confirmación táctil de que los datos ya
 * están al día, mediante una breve pausa antes de cerrar el indicador.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberPullToRefreshState()

    if (state.isRefreshing) {
        LaunchedEffect(state) {
            delay(400)
            state.endRefresh()
        }
    }

    Box(modifier.fillMaxSize().nestedScroll(state.nestedScrollConnection)) {
        content()
        PullToRefreshContainer(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
