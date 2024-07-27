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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.ac.tees.mad.d3617913.AppDatabase
import uk.ac.tees.mad.d3617913.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()
    }
    val categoryDao = db.categoryDao()

    var categories by remember { mutableStateOf(listOf<Category>()) }
    var newCategory by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf<String?>(null) }
    var editCategoryName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            categories = categoryDao.getAllCategories()
        }
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
                            Text(text = category.name)
                            Row {
                                IconButton(onClick = {
                                    editCategory = category.name
                                    editCategoryName = category.name
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        withContext(Dispatchers.IO) {
                                            categoryDao.deleteCategory(category)
                                            categories = categoryDao.getAllCategories()
                                        }
                                    }
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
                                scope.launch {
                                    categoryDao.deleteCategory(Category(editCategory!!))
                                    categoryDao.insertCategory(Category(editCategoryName))
                                    categories = categoryDao.getAllCategories()
                                    editCategoryName = ""
                                    editCategory = null
                                }
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
                            if (newCategory.isBlank()) return@IconButton
                            println(newCategory)
                            scope.launch(Dispatchers.Main) {
                                categoryDao.insertCategory(Category(name = newCategory))
                                categories = categoryDao.getAllCategories()
                                newCategory = ""
                            }
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