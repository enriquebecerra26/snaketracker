package com.enriquebecerra.snaketracker.ui.navigation

sealed class Screen(val route: String) {
    object PetList : Screen("pet_list")
    object AddPet : Screen("add_pet")

    object PetDetail : Screen("pet_detail/{petId}") {
        fun createRoute(petId: Long) = "pet_detail/$petId"
    }

    object EditPet : Screen("edit_pet/{petId}") {
        fun createRoute(petId: Long) = "edit_pet/$petId"
    }

    object AddFeeding : Screen("add_feeding/{petId}") {
        fun createRoute(petId: Long) = "add_feeding/$petId"
    }

    object AddWeight : Screen("add_weight/{petId}") {
        fun createRoute(petId: Long) = "add_weight/$petId"
    }

    object AddLength : Screen("add_length/{petId}") {
        fun createRoute(petId: Long) = "add_length/$petId"
    }
}
