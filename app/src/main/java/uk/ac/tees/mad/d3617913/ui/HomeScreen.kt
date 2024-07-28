package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.d3617913.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val db = Firebase.firestore
    var groceryLists by remember { mutableStateOf(listOf<GroceryList>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        groceryLists = db.collection("items").get().await().documents.map { document ->
            GroceryList(
                id = document.id,
                name = document.getString("name") ?: "",
                category = document.getString("category") ?: "",
                imageLink = document.getString("imageUri") ?: "",
                notes = document.getString("notes") ?: "",
                quantity = document.getString("quantity") ?: ""
            )
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GroceryGo",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddEditItem.route + "/ ") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
//        bottomBar = {
//        NavigationBar {
//            NavigationBarItem(
//                selected = false,
//                onClick = { navController.navigate(Screen.Categories.route) },
//                icon = {
//                    Icon(imageVector = Icons.Default.Category, contentDescription = null)
//                },
//                label = {
//                    Text(text = "Categories")
//                })
//            NavigationBarItem(
//                selected = false,
//                onClick = { navController.navigate(Screen.Profile.route) },
//                icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
//                label = {
//                    Text(text = "Profile")
//                }
//            )
//        } }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Grocery Lists",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (groceryLists.isEmpty()) {
                    Text(text = "No grocery lists found.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groceryLists) { groceryList ->
                            GroceryListItem(groceryList, navController)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GroceryListItem(groceryList: GroceryList, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.GroceryList.route + "/" + groceryList.id)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(groceryList.imageLink),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = groceryList.name, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "Quantity: ${groceryList.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

data class GroceryList(
    val id: String,
    val name: String,
    val category: String,
    val imageLink: String,
    val notes: String,
    val quantity: String,
)