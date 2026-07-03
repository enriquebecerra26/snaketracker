package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.enriquebecerra.snaketracker.domain.model.PetSexOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexSelector(
    selectedSex: String,
    onSexChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        PetSexOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selectedSex == option,
                onClick = { onSexChange(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = PetSexOptions.size)
            ) {
                Text(option)
            }
        }
    }
}
