package com.enriquebecerra.snaketracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enriquebecerra.snaketracker.ui.screens.addpet.AddPetScreen
import com.enriquebecerra.snaketracker.ui.screens.calendar.CalendarioScreen
import com.enriquebecerra.snaketracker.ui.screens.defecation.AddDefecationScreen
import com.enriquebecerra.snaketracker.ui.screens.editpet.EditPetScreen
import com.enriquebecerra.snaketracker.ui.screens.expense.AddExpenseScreen
import com.enriquebecerra.snaketracker.ui.screens.expense.GastosScreen
import com.enriquebecerra.snaketracker.ui.screens.feeding.AddFeedingScreen
import com.enriquebecerra.snaketracker.ui.screens.health.AddHealthRecordScreen
import com.enriquebecerra.snaketracker.ui.screens.length.AddLengthScreen
import com.enriquebecerra.snaketracker.ui.screens.petdetail.PetDetailScreen
import com.enriquebecerra.snaketracker.ui.screens.petlist.PetListScreen
import com.enriquebecerra.snaketracker.ui.screens.shedding.AddSheddingScreen
import com.enriquebecerra.snaketracker.ui.screens.terrarium.AddTerrariumLogScreen
import com.enriquebecerra.snaketracker.ui.screens.weight.AddWeightScreen

@Composable
fun SnakeTrackerNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.PetList.route) {

        composable(Screen.PetList.route) {
            PetListScreen(
                onAddPetClick = { navController.navigate(Screen.AddPet.route) },
                onPetClick = { petId ->
                    navController.navigate(Screen.PetDetail.createRoute(petId))
                },
                onExpensesClick = { navController.navigate(Screen.Expenses.route) },
                onCalendarClick = { navController.navigate(Screen.Calendar.route) }
            )
        }

        composable(Screen.AddPet.route) {
            AddPetScreen(
                onPetSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PetDetail.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            PetDetailScreen(
                onBackClick = { navController.popBackStack() },
                onPetDeleted = { navController.popBackStack(Screen.PetList.route, inclusive = false) },
                onEditProfileClick = { petId ->
                    navController.navigate(Screen.EditPet.createRoute(petId))
                },
                onAddFeedingClick = { petId ->
                    navController.navigate(Screen.AddFeeding.createRoute(petId))
                },
                onAddWeightClick = { petId ->
                    navController.navigate(Screen.AddWeight.createRoute(petId))
                },
                onAddLengthClick = { petId ->
                    navController.navigate(Screen.AddLength.createRoute(petId))
                },
                onAddSheddingClick = { petId ->
                    navController.navigate(Screen.AddShedding.createRoute(petId))
                },
                onAddDefecationClick = { petId ->
                    navController.navigate(Screen.AddDefecation.createRoute(petId))
                },
                onAddHealthClick = { petId ->
                    navController.navigate(Screen.AddHealth.createRoute(petId))
                },
                onAddTerrariumClick = { petId ->
                    navController.navigate(Screen.AddTerrarium.createRoute(petId))
                }
            )
        }

        composable(
            route = Screen.EditPet.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            EditPetScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddFeeding.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddFeedingScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddWeight.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddWeightScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddLength.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddLengthScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddShedding.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddSheddingScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddDefecation.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddDefecationScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddHealth.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddHealthRecordScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddTerrarium.route,
            arguments = listOf(navArgument("petId") { type = NavType.LongType })
        ) {
            AddTerrariumLogScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Expenses.route) {
            GastosScreen(
                onBackClick = { navController.popBackStack() },
                onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarioScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
