package com.shubhanya.fingenienxt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.shubhanya.fingenienxt.ui.navigation.AppNavigation
import com.shubhanya.fingenienxt.ui.theme.FinGenieNxtTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // For Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinGenieNxtTheme { // app's theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Set AppNavigation as the content
                }
            }
        }
    }
}


//```
//**Explanation:**
//* **`@AndroidEntryPoint`**: This Hilt annotation is crucial for enabling field injection in your Activity (though we are not directly injecting here, it's needed for Hilt to work with Activities that host Hilt ViewModels or other Hilt components).
//* The `setContent` block now calls `AppNavigation()`, which manages all screen transitio