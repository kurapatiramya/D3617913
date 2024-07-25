package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(navController: NavController, listId: String) {
    val db = FirebaseFirestore.getInstance()
    var groceryItem by remember { mutableStateOf(GroceryItem()) }

    LaunchedEffect(listId) {
        db.collection("items").document(listId).get()
            .addOnSuccessListener { snapshot ->
                val id = snapshot.id
                val data = snapshot.data
                data?.let { document ->
                    groceryItem = GroceryItem(
                        id = id,
                        name = document["name"] as String? ?: "",
                        quantity = document["quantity"] as String? ?: "",
                        category = document["category"] as String? ?: "",
                        notes = document["notes"] as String? ?: "",
                        bought = document["bought"] as Boolean ?: false,
                        imageUri = document["imageUri"] as String? ?: "",
                    )
                }

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
                GroceryItemCard(groceryItem, navController, listId)
            }
        }
    }
}

@Composable
fun GroceryItemCard(groceryItem: GroceryItem, navController: NavController, listId: String) {
    val db = FirebaseFirestore.getInstance()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {

            },
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
                db.collection("items").document(listId).collection("items")
                    .document(groceryItem.id).delete()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
            Checkbox(
                checked = groceryItem.bought,
                onCheckedChange = { isChecked ->
                    db.collection("items").document(listId).collection("items")
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
    val id: String = "",
    val name: String = "",
    val quantity: String = "",
    val category: String = "",
    val notes: String = "",
    val bought: Boolean = false,
    val imageUri: String? = null
)