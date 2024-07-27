package uk.ac.tees.mad.d3617913.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import uk.ac.tees.mad.d3617913.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListScreen(navController: NavController, listId: String) {
    val firestore = Firebase.firestore
    var groceryItem by remember { mutableStateOf(GroceryItem()) }
    val context = LocalContext.current

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
                },
                actions = {
                    IconButton(onClick = {
                        firestore.collection("items").document(listId).collection("items")
                            .document(groceryItem.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()

                            }.addOnFailureListener {
                                it.printStackTrace()
                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT)
                                    .show()
                            }

                    }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            Text(
                text = groceryItem.name,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Quantity: ", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = groceryItem.quantity, style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Category: ", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = groceryItem.category, fontSize = 17.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(Modifier.fillMaxWidth()) {
//                Text(text = "Notes: ", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Card(
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {

                    Text(
                        text = "Notes - ${groceryItem.notes}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.AddEditItem.route + "/${groceryItem.id}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Edit")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
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