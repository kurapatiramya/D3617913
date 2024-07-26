package uk.ac.tees.mad.d3617913.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(navController: NavController, listId: String) {
    val firestore = Firebase.firestore
    var groceryItem by remember { mutableStateOf(GroceryItem()) }

    LaunchedEffect(listId) {
        firestore.collection("items").document(listId).get()
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
                        bought = document["bought"] as Boolean? ?: false,
                        imageUri = document["imageUri"] as String? ?: "",
                    )
                }

            }


    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grocery List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {

            Column(modifier = Modifier.padding(16.dp)) {
                GroceryItemCard(groceryItem, navController, listId)
            }
        }
    }
}

@Composable
fun GroceryItemCard(groceryItem: GroceryItem, navController: NavController, listId: String) {
    val firestore = Firebase.firestore
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {

            }
    ) {
        Text(
            text = groceryItem.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        groceryItem.imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Column {
            Text(text = groceryItem.quantity, style = MaterialTheme.typography.bodyMedium)
            Text(text = groceryItem.category, style = MaterialTheme.typography.bodyMedium)
            Text(text = groceryItem.notes, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    firestore.collection("items").document(listId).collection("items")
                        .document(groceryItem.id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()

                        }.addOnFailureListener {
                            it.printStackTrace()
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                        }

                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Delete")
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (groceryItem.bought) {
                Button(
                    onClick = {
                        firestore.collection("items").document(listId).update("bought", false)
                    },
                    modifier = Modifier.weight(1f)

                ) {
                    Text(text = "Bought")
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(checked = groceryItem.bought, onCheckedChange = null)
                }
            } else {
                OutlinedButton(
                    onClick = {
                        firestore.collection("items").document(listId).update("bought", true)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Mark as bought")
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(checked = groceryItem.bought, onCheckedChange = null)
                }
            }

        }
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