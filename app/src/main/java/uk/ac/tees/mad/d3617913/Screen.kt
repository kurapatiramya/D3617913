package uk.ac.tees.mad.d3617913

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object GroceryList : Screen("grocery_list")
    object AddEditItem : Screen("add_edit_item")
    object Categories : Screen("categories")
    object Profile : Screen("profile")
}