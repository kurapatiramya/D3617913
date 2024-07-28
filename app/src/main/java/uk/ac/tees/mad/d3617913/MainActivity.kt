package uk.ac.tees.mad.d3617913

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.d3617913.ui.AddEditItemScreen
import uk.ac.tees.mad.d3617913.ui.CategoriesScreen
import uk.ac.tees.mad.d3617913.ui.GroceryListScreen
import uk.ac.tees.mad.d3617913.ui.HomeScreen
import uk.ac.tees.mad.d3617913.ui.ProfileScreen
import uk.ac.tees.mad.d3617913.ui.SplashScreen
import uk.ac.tees.mad.d3617913.ui.theme.GroceryGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryGoTheme {
                GroceryGoApp()

            }
        }
    }
}

@Composable
fun GroceryGoApp() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen {
                scope.launch(Dispatchers.Main) {
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                }
            }
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.GroceryList.route + "/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                }
            )
        ) {
            val itemId = it.arguments?.getString("itemId") ?: ""
            GroceryListScreen(navController, itemId)
        }

        composable(
            route = Screen.AddEditItem.route + "/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.StringType
                }
            )
        ) {

            val itemId = it.arguments?.getString("itemId")
            println("itemId: $itemId")
            AddEditItemScreen(navController, itemId)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(navController)
        }
//        composable(Screen.Profile.route) {
//            ProfileScreen(navController)
//        }

    }
}
