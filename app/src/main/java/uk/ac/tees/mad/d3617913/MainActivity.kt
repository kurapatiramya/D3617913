package uk.ac.tees.mad.d3617913

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable(Screen.GroceryList.route) {
            GroceryListScreen(navController)
        }
        composable(Screen.AddEditItem.route) {
            AddEditItemScreen(navController)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

    }
}
