package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.d3617913.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "GroceryGo", fontSize = 30.sp, fontWeight = FontWeight.SemiBold
            )
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate(Screen.AddEditItem.route) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }, bottomBar = {
        NavigationBar {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(Screen.Categories.route) },
                icon = {
                    Icon(imageVector = Icons.Default.Category, contentDescription = null)
                },
                label = {
                    Text(text = "Categories")
                })
            NavigationBarItem(
                selected = false,
                onClick = { /*TODO*/ },
                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                label = {
                    Text(text = "Profile")
                }
            )
        }

    }) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            Text(text = "Home")
        }
    }
}