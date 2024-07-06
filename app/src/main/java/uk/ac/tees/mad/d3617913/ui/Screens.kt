package uk.ac.tees.mad.d3617913.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Text(text = "Home Screen")
}

@Composable
fun GroceryListScreen(navController: NavController) {
    Text(text = "Grocery List Screen")
}

@Composable
fun AddEditItemScreen(navController: NavController) {
    Text(text = "Add/Edit Item Screen")
}

@Composable
fun CategoriesScreen(navController: NavController) {
    Text(text = "Categories Screen")
}

@Composable
fun ProfileScreen(navController: NavController) {
    Text(text = "Profile Screen")
}