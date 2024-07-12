package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var categories by remember { mutableStateOf(listOf<String>()) }
    var newCategory by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf<String?>(null) }
    var editCategoryName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        categories =
            db.collection("categories").get().await().documents.map { it.getString("name") ?: "" }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            })
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (categories.isEmpty()) {

                    Text(text = "No items present")

                }
                LazyColumn(modifier = Modifier.weight(1f)) {

                    items(categories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = category)
                            Row {
                                IconButton(onClick = {
                                    editCategory = category
                                    editCategoryName = category
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    db.collection("categories").document(category).delete()
                                    categories = categories.filter { it != category }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
                if (editCategory != null) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                        OutlinedTextField(
                            value = editCategoryName,
                            onValueChange = { editCategoryName = it },
                            label = { Text("Edit Category") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            )
                        )
                        IconButton(
                            onClick = {
                                db.collection("categories").document(editCategory!!)
                                    .set(mapOf("name" to editCategoryName))
                                categories =
                                    categories.map { if (it == editCategory) editCategoryName else it }
                                editCategory = null
                                editCategoryName = ""
                                focusManager.clearFocus()

                            },
                            colors = IconButtonDefaults.iconButtonColors()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                        OutlinedTextField(
                            value = newCategory,
                            onValueChange = { newCategory = it },
                            label = { Text("New Category") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            )
                        )
                        IconButton(onClick = {
                            db.collection("categories").document(newCategory)
                                .set(mapOf("name" to newCategory))
                            categories = categories + newCategory
                            newCategory = ""
                            focusManager.clearFocus()

                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            }
        }
    }
}