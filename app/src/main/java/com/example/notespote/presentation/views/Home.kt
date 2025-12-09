package com.example.notespote.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.notespote.presentation.components.BottomAppBar

@Composable
fun Home(navController: NavController){

    Scaffold(
        bottomBar = {
            BottomAppBar(navController = navController)
        }
    ) {
        Content(it)
    }
}

@Composable
fun Content(innerPadding: PaddingValues) {
    Column() {
        Text(text = "Hola")
    }
}