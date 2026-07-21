package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: AppViewModel) {
    val user by viewModel.user.collectAsState()
    
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var selectedCompany by remember { mutableStateOf(user?.goalCompany ?: "Google") }
    var targetRole by remember { mutableStateOf(user?.targetRole ?: "Software Engineer") }
    var resumeText by remember { mutableStateOf(user?.resumeText ?: "") }
    var targetAttendance by remember { mutableFloatStateOf(user?.targetAttendance ?: 75.0f) }

    val companies = listOf("Google", "Amazon", "Microsoft", "TCS", "Infosys", "Startup")
    var companyDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
            selectedCompany = it.goalCompany
            targetRole = it.targetRole
            resumeText = it.resumeText
            targetAttendance = it.targetAttendance
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = if (user == null) "Setup Your AI Hub" else "My AI Profile",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Personalize your target company and smart student preferences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("username_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Goal Company (Dropdown Menu)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCompany,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Goal Company") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { companyDropdownExpanded = true }
                                .testTag("company_select"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { companyDropdownExpanded = true }
                        )
                        DropdownMenu(
                            expanded = companyDropdownExpanded,
                            onDismissRequest = { companyDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            companies.forEach { company ->
                                DropdownMenuItem(
                                    text = { Text(company) },
                                    onClick = {
                                        selectedCompany = company
                                        companyDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Target Role
                    OutlinedTextField(
                        value = targetRole,
                        onValueChange = { targetRole = it },
                        label = { Text("Target Career Role") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("role_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Target Attendance Slider
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Required Target Attendance",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = "${targetAttendance.toInt()}%",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Slider(
                            value = targetAttendance,
                            onValueChange = { targetAttendance = it },
                            valueRange = 50f..100f,
                            steps = 9,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Resume / Background Text Area
                    OutlinedTextField(
                        value = resumeText,
                        onValueChange = { resumeText = it },
                        label = { Text("Paste Resume / Background Text") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("resume_input"),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Paste details of past projects, college branch, key skills like Python, Java, React, SQL...") },
                        maxLines = 8
                    )
                    
                    // Quick Resume template helpers
                    Text(
                        text = "Or load a resume template:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                resumeText = "Aaditya Raj - Pre-final year Computer Science Student. Skills: Java, Python, React, SQL, HTML/CSS. Completed a student portfolio project and database college project. Looking for Software Engineer roles."
                            },
                            label = { Text("CS Student") }
                        )
                        SuggestionChip(
                            onClick = {
                                resumeText = "Senior Android Developer. 3 years exp. Skills: Kotlin, Jetpack Compose, Coroutines, Room DB, Retrofit. Built 4 production grade applications, optimized database performance."
                            },
                            label = { Text("Android Dev") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        viewModel.saveProfile(
                            name = name,
                            email = email,
                            company = selectedCompany,
                            role = targetRole,
                            resume = resumeText,
                            targetAttendance = targetAttendance
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_profile_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (user == null) "Launch Dashboard" else "Update Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            if (user != null) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { viewModel.navigateTo("home") }
                ) {
                    Text("Go Back to Dashboard")
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
