package uk.ac.tees.mad.d3617913.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@Composable
fun AddEditItemScreen(navController: NavController, itemId: String? = null) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance()

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(itemId) {
        if (itemId != null) {
            val item = db.collection("items").document(itemId).get().await()
            name = TextFieldValue(item.getString("name") ?: "")
            quantity = TextFieldValue(item.getString("quantity") ?: "")
            category = TextFieldValue(item.getString("category") ?: "")
            notes = TextFieldValue(item.getString("notes") ?: "")
            imageUri = item.getString("imageUri")?.let { Uri.parse(it) }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Add/Edit Item", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                ) {
                    if (name.text.isEmpty()) {
                        Text(text = "Name", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = quantity,
            onValueChange = { quantity = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                ) {
                    if (quantity.text.isEmpty()) {
                        Text(text = "Quantity", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = category,
            onValueChange = { category = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                ) {
                    if (category.text.isEmpty()) {
                        Text(text = "Category", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                ) {
                    if (notes.text.isEmpty()) {
                        Text(text = "Notes", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text(text = "Pick Image")
        }

        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(100.dp).padding(top = 8.dp),
                contentScale = ContentScale.Crop
            )
        }

        Button(
            onClick = {
                val itemData = hashMapOf(
                    "name" to name.text,
                    "quantity" to quantity.text,
                    "category" to category.text,
                    "notes" to notes.text,
                    "imageUri" to imageUri.toString()
                )

                if (itemId == null) {
                    db.collection("items").add(itemData)
                } else {
                    db.collection("items").document(itemId).set(itemData)
                }

                navController.popBackStack()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Save")
        }
    }
}