package uk.ac.tees.mad.d3617913.ui

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddEditItemScreen(navController: NavController, itemId: String? = null) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val storage = Firebase.storage
    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf(TextFieldValue("")) }
    var notes by remember { mutableStateOf(TextFieldValue("")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var categories by remember { mutableStateOf(listOf<String>()) }
    var expanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap ->
            val file = File(context.filesDir, "image.jpg")
            file.outputStream().use { out ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
            imageUri = Uri.fromFile(file)
        }

    val cameraPermission = rememberPermissionState(
        permission = android.Manifest.permission.CAMERA,
        onPermissionResult = {
            if (it) {
                cameraLauncher.launch(null)
            }
        }
    )


    LaunchedEffect(itemId) {
        if (itemId != null) {
            val item = db.collection("items").document(itemId).get().await()
            name = TextFieldValue(item.getString("name") ?: "")
            quantity = TextFieldValue(item.getString("quantity") ?: "")
            category = TextFieldValue(item.getString("category") ?: "")
            notes = TextFieldValue(item.getString("notes") ?: "")
            imageUri = item.getString("imageUri")?.let { Uri.parse(it) }
        }
        categories =
            db.collection("categories").get().await().documents.map { it.getString("name") ?: "" }

    }
    Scaffold() { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add/Edit Item",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text(text = "Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text(text = "Quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    TextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { categoryItem ->
                            DropdownMenuItem(
                                text = { Text(text = categoryItem) },
                                onClick = {
                                    category = categoryItem.let { TextFieldValue(it) }
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text(text = "Notes") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text(text = "Pick Image")
                    }


                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (cameraPermission.status.isGranted) {
                                cameraLauncher.launch(null)
                            } else {
                                cameraPermission.launchPermissionRequest()
                            }
                        }) {
                        Text(text = "Take Photo")
                    }
                }

                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }


                Button(
                    onClick = {
                        isLoading = true
                        val itemData = hashMapOf<String, Any>(
                            "name" to name.text,
                            "quantity" to quantity.text,
                            "category" to category.text,
                            "notes" to notes.text
                        )

                        if (imageUri != null) {
                            val storageRef =
                                storage.reference.child("images/${UUID.randomUUID()}")
                            storageRef.putFile(imageUri!!).addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    itemData["imageUri"] = uri.toString()
                                    saveItemToFirestore(
                                        db,
                                        itemId,
                                        itemData,
                                        onSuccess = {
                                            navController.popBackStack()
                                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT)
                                                .show()
                                            isLoading = false
                                        },
                                        onFailure = {
                                            Toast.makeText(context, it, Toast.LENGTH_SHORT)
                                                .show()
                                            isLoading = false

                                        }
                                    )
                                }
                            }
                        } else {
                            saveItemToFirestore(
                                db, itemId, itemData,
                                onSuccess = {
                                    navController.popBackStack()
                                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT)
                                        .show()
                                    isLoading = false
                                },
                                onFailure = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                })
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Save")
                    }
                }
            }
        }
    }


}

fun saveItemToFirestore(
    db: FirebaseFirestore,
    itemId: String?,
    itemData: HashMap<String, Any>,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    if (itemId == null) {
        db.collection("items").add(itemData).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke("Failed to save item")
            it.printStackTrace()
        }
    } else {
        db.collection("items").document(itemId).set(itemData).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke("Failed to save item")
            it.printStackTrace()
        }
    }
}

