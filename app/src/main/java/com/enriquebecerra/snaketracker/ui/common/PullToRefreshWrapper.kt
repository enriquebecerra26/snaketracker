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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.delay

/**
 * Como todas las listas de la app están respaldadas por Flows de Room (se actualizan solas ante
 * cualquier cambio en la base de datos), no existe una operación de "refresco" real que ejecutar.
 * Este wrapper ofrece el gesto de pull-to-refresh como confirmación táctil de que los datos ya
 * están al día, mediante una breve pausa antes de cerrar el indicador.
 *
 * [PullToRefreshContainer] se posiciona en la parte superior y, en reposo, se traslada fuera de
 * su propio contenedor (hacia arriba) mediante un `graphicsLayer` interno para quedar oculto.
 * Compose no recorta ese contenido por defecto, así que sin [clipToBounds] el indicador queda
 * visible como un círculo fijo sobre lo que esté inmediatamente encima de este wrapper (por
 * ejemplo, la barra de tabs en pantallas que no usan un TopAppBar de Scaffold para taparlo).
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

    Box(modifier.fillMaxSize().clipToBounds().nestedScroll(state.nestedScrollConnection)) {
        content()
        PullToRefreshContainer(
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
