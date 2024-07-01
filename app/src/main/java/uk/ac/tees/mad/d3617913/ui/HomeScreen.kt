package uk.ac.tees.mad.d3617913.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    Scaffold(Modifier.fillMaxSize()) { ip ->
        Column(Modifier.padding(ip)) {
            Text(text = "Home")
        }
    }
}