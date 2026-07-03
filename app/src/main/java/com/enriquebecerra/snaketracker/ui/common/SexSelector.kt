package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.enriquebecerra.snaketracker.domain.model.PetSexOptions

@Composable
fun SexSelector(
    selectedSex: String,
    onSexChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SegmentedSelector(options = PetSexOptions, selected = selectedSex, onSelect = onSexChange, modifier = modifier)
}
