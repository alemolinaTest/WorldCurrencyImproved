package com.amolina.worldcurrency.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amolina.worldcurrency.presentation.ui.screen.ConversionDetailScreen
import com.amolina.worldcurrency.presentation.ui.screen.CurrencyScreen
import com.amolina.worldcurrency.presentation.ui.screen.HistoryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = "converter"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable("converter") {
            CurrencyScreen(navController = navController)
        }

        composable("history") {
            HistoryScreen(navController = navController)
        }

        composable("history/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            id?.let {
                ConversionDetailScreen(navController = navController, conversionId = it)
            }
        }
    }
}
