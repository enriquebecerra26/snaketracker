package com.enriquebecerra.snaketracker.ui.screens.expense

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.ExpenseCategoryOptions
import com.enriquebecerra.snaketracker.domain.model.ExpenseRecord
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetExpensesUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.ui.common.BarChart
import com.enriquebecerra.snaketracker.ui.common.BarChartEntry
import com.enriquebecerra.snaketracker.ui.common.PullToRefreshWrapper
import com.enriquebecerra.snaketracker.ui.common.formatDate
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

private const val FilterAll = -2L
private const val FilterGeneral = -1L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    onBackClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    viewModel: GastosViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        GastosViewModel(GetExpensesUseCase(app.expenseRepository), GetPetsUseCase(app.petRepository))
    }
) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val pets by viewModel.pets.collectAsStateWithLifecycle()
    var selectedFilter by rememberSaveable { mutableStateOf(FilterAll) }

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val expensesThisYear = remember(expenses, currentYear) {
        expenses.filter { yearOf(it.date) == currentYear }
    }
    val annualTotal = remember(expensesThisYear) {
        expensesThisYear.sumOf { it.amountMXN.toDouble() }.toFloat()
    }
    val categoryEntries = remember(expensesThisYear) {
        ExpenseCategoryOptions.map { category ->
            BarChartEntry(
                label = category,
                value = expensesThisYear.filter { it.category == category }.sumOf { it.amountMXN.toDouble() }.toFloat()
            )
        }
    }
    val filteredExpenses = remember(expenses, selectedFilter) {
        when (selectedFilter) {
            FilterAll -> expenses
            FilterGeneral -> expenses.filter { it.petId == null }
            else -> expenses.filter { it.petId == selectedFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gastos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar gasto")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            AnnualTotalCard(total = annualTotal, year = currentYear)
            BarChart(
                entries = categoryEntries,
                emptyMessage = "Sin gastos registrados este año"
            )
            PetFilterRow(
                pets = pets,
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )
            PullToRefreshWrapper(modifier = Modifier.weight(1f)) {
                if (filteredExpenses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Sin gastos registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredExpenses, key = { it.id }) { expense ->
                            ExpenseRow(expense = expense, petName = petNameFor(expense.petId, pets))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnualTotalCard(total: Float, year: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                text = "Total gastado en $year",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMXN(total),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PetFilterRow(pets: List<Pet>, selectedFilter: Long, onFilterChange: (Long) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == FilterAll,
            onClick = { onFilterChange(FilterAll) },
            label = { Text("Todas") }
        )
        FilterChip(
            selected = selectedFilter == FilterGeneral,
            onClick = { onFilterChange(FilterGeneral) },
            label = { Text("General") }
        )
        pets.forEach { pet ->
            FilterChip(
                selected = selectedFilter == pet.id,
                onClick = { onFilterChange(pet.id) },
                label = { Text(pet.name) }
            )
        }
    }
}

@Composable
private fun ExpenseRow(expense: ExpenseRecord, petName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(expense.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${expense.category} • $petName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatMXN(expense.amountMXN),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun petNameFor(petId: Long?, pets: List<Pet>): String =
    pets.find { it.id == petId }?.name ?: "General"

private fun yearOf(millis: Long): Int =
    Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.YEAR)

private fun formatMXN(amount: Float): String =
    NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(amount)
