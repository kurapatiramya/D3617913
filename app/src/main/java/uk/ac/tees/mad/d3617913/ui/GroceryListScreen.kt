package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(navController: NavController, listId: String) {
    val db = FirebaseFirestore.getInstance()
    var groceryItems by remember { mutableStateOf(listOf<GroceryItem>()) }

    LaunchedEffect(listId) {
        groceryItems = db.collection("groceryLists").document(listId).collection("items").get()
            .await().documents.map { document ->
                GroceryItem(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    quantity = document.getString("quantity") ?: "",
                    category = document.getString("category") ?: "",
                    notes = document.getString("notes") ?: "",
                    bought = document.getBoolean("bought") ?: false,
                    imageUri = document.getString("imageUri")
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grocery List") },
                actions = {
                    IconButton(onClick = { /* Navigate to Add Item Screen */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Items",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(groceryItems) { groceryItem ->
                        GroceryItemRow(groceryItem, navController, listId)
                    }
                }
            }
        }
    }
}

@Composable
fun GroceryItemRow(groceryItem: GroceryItem, navController: NavController, listId: String) {
    val db = FirebaseFirestore.getInstance()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Navigate to Edit Item Screen */ },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = groceryItem.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = groceryItem.quantity, style = MaterialTheme.typography.bodyMedium)
            Text(text = groceryItem.category, style = MaterialTheme.typography.bodyMedium)
            Text(text = groceryItem.notes, style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            IconButton(onClick = {
                db.collection("groceryLists").document(listId).collection("items")
                    .document(groceryItem.id).delete()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
            Checkbox(
                checked = groceryItem.bought,
                onCheckedChange = { isChecked ->
                    db.collection("groceryLists").document(listId).collection("items")
                        .document(groceryItem.id).update("bought", isChecked)
                }
            )
        }
    }

    groceryItem.imageUri?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Crop
        )
    }
}

data class GroceryItem(
    val id: String,
    val name: String,
    val quantity: String,
    val category: String,
    val notes: String,
    val bought: Boolean,
    val imageUri: String?
)