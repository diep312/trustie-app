package com.example.trustie

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.trustie.navigation.AppNavigation
import com.example.trustie.ui.base.BaseAuthenticatedActivity

class MainActivity : BaseAuthenticatedActivity() {
    
    private var navController: NavHostController? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    @Composable
    override fun MainContent() {
        navController = rememberNavController()
        AppNavigation(navController = navController!!)
    }
}
