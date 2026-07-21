package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.database.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository layers
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(db.userDao(), db.subjectDao(), db.interviewSessionDao())
        val factory = AppViewModelFactory(application, repository)
        val viewModel: AppViewModel by viewModels { factory }

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()

                // State-driven routing wrapped in responsive navigation
                ResponsiveNavigationContainer(viewModel = viewModel) {
                    when (currentScreen) {
                        "profile" -> ProfileScreen(viewModel = viewModel)
                        "home" -> HomeScreen(viewModel = viewModel)
                        "interview_setup" -> InterviewScreen(viewModel = viewModel)
                        "interview_active" -> InterviewScreen(viewModel = viewModel)
                        "interview_report" -> ReportScreen(viewModel = viewModel)
                        "attendance_tracker" -> AttendanceScreen(viewModel = viewModel)
                        "attendance_chat" -> AttendanceChatScreen(viewModel = viewModel)
                        else -> HomeScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
