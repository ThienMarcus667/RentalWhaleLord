package com.msdc.rentalwheels.appnavigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgument
import com.msdc.rentalwheels.ui.screens.BookingScreen
import com.msdc.rentalwheels.ui.screens.BrowseScreen
import com.msdc.rentalwheels.ui.screens.HomeScreen
import com.msdc.rentalwheels.ui.screens.SettingsScreen
import com.msdc.rentalwheels.ui.theme.ThemeMode
import com.msdc.rentalwheels.ui.theme.ThemeState
import com.msdc.rentalwheels.ui.theme.rememberThemeState
import com.msdc.rentalwheels.utils.CarDetailRoute
import com.msdc.rentalwheels.viewmodel.CarViewModel
import com.msdc.rentalwheels.uistates.HomeScreenState

@Composable
fun RentalWheelsApp(
    viewModel: CarViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    themeState: ThemeState = rememberThemeState()
) {
    val themeMode by themeState.themeMode
    val colors = when (themeMode) {
        ThemeMode.Light -> lightColorScheme()
        ThemeMode.Dark -> darkColorScheme()
    }

    val uiState by viewModel.uiState.collectAsState()
    val carDetailState by viewModel.carDetailState.collectAsState()
    val homeState by viewModel.homeState.collectAsState()
    val favorites by viewModel.favoriteCarIds.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val shouldShowBottomBar = when {
        currentRoute?.startsWith("car_detail/") == true -> false
        else -> true
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography()
    ) {
        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomNavigation(
                        currentDestination = navController.currentDestination,
                        onNavigate = { screen ->
                            navController.navigate(screen.route)
                        },
                        themeState = themeState
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onCarClick = { carId ->
                            navController.navigate(Screen.DetailedCarScreen.createRoute(carId))
                        }
                    )
                }
                composable(Screen.Bookings.route) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Vui lòng quay lại Home\nđể chọn xe\nvà tiếp tục quá trình thuê",
                            color = Color.Red,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
                composable(
                    route = Screen.DetailedCarScreen.route,
                    arguments = listOf(navArgument("carId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val carId = backStackEntry.arguments?.getString("carId") ?: ""
                    CarDetailRoute(
                        carId = carId,
                        onBackClick = { navController.navigateUp() },
                        navController = navController
                    )
                }
                composable(Screen.Browse.route) {
                    if (homeState is HomeScreenState.Success) {
                        val state = homeState as HomeScreenState.Success
                        BrowseScreen(
                            cars = state.recommendedCars,
                            favorites = favorites,
                            onCarClick = { car ->
                                viewModel.selectCar(car.id)
                                navController.navigate(Screen.DetailedCarScreen.createRoute(car.id))
                            },
                            onFavoriteClick = { car ->
                                viewModel.toggleFavorite(car)
                            }
                        )
                    }
                }

                composable("booking/{carId}/{carName}") { backStackEntry ->
                    val carId = backStackEntry.arguments?.getString("carId") ?: ""
                    val carName = backStackEntry.arguments?.getString("carName") ?: ""

                    BookingScreen(
                        carId = carId,
                        carName = carName,
                        onBookingConfirmed = {
                            // Có thể chuyển về màn hình chính hoặc popup thông báo khác
                        }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
