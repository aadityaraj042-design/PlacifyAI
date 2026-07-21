package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Subject
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: AppViewModel) {
    val subjects by viewModel.subjects.collectAsState()
    val user by viewModel.user.collectAsState()
    val targetPercent = user?.targetAttendance ?: 75.0f

    var showAddSubjectDialog by remember { mutableStateOf(false) }

    // Math aggregates
    val totalAttended = subjects.sumOf { it.attendedClasses }
    val totalHeld = subjects.sumOf { it.totalClasses }
    val overallPercent = if (totalHeld > 0) (totalAttended.toFloat() / totalHeld) * 100f else 100f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo("home") },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Home")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Smart Planner",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Button(
                    onClick = { showAddSubjectDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Subject", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Prediction Indicator Card
                item {
                    PredictionCard(overallPercent = overallPercent, totalAttended = totalAttended, totalHeld = totalHeld, targetPercent = targetPercent)
                }

                // Daily AI Notification Alerter Scheduler Card
                item {
                    AttendanceSchedulerCard(viewModel = viewModel)
                }

                // AI Planner Skip Advisory Card
                item {
                    AIPlannerAdvisoryCard(subjects = subjects, targetPercent = targetPercent)
                }

                // Smart Calendar representation
                item {
                    SmartCalendarView(overallPercent = overallPercent, targetPercent = targetPercent)
                }

                // Teacher Strictness Risk Card
                item {
                    TeacherStrictnessRiskCard(subjects = subjects)
                }

                // Subject list title
                item {
                    Text(
                        text = "College Subjects Logs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (subjects.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ListAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Subjects Registered",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Add subjects to track attendance strictness and generate smart skip planners.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(subjects) { subject ->
                        SubjectTrackerCard(subject = subject, viewModel = viewModel, targetPercent = targetPercent)
                    }
                }
            }
        }

        // Add Subject Dialog Dialog
        if (showAddSubjectDialog) {
            AddSubjectDialog(
                onDismiss = { showAddSubjectDialog = false },
                onAdd = { name, teacher, strictness, total, attended ->
                    viewModel.addSubject(name, teacher, strictness, total, attended)
                    showAddSubjectDialog = false
                }
            )
        }
    }
}

@Composable
fun PredictionCard(overallPercent: Float, totalAttended: Int, totalHeld: Int, targetPercent: Float) {
    // Calculative predictions
    val tomorrowAbsentPercent = if (totalHeld > 0) {
        (totalAttended.toFloat() / (totalHeld + 1)) * 100f
    } else 0f
    
    val fiveDaysAbsentPercent = if (totalHeld > 0) {
        (totalAttended.toFloat() / (totalHeld + 5)) * 100f
    } else 0f

    // Calculate classes required to reach target
    var classesToAttendToSafe = 0
    if (overallPercent < targetPercent) {
        var currentAttended = totalAttended
        var currentHeld = totalHeld
        while ((currentAttended.toFloat() / currentHeld) * 100f < targetPercent) {
            classesToAttendToSafe++
            currentAttended++
            currentHeld++
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📊 AI Attendance Predictive Scenarios",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Column 1
                PredictionItemBox(
                    label = "If Absent Tomorrow",
                    value = "${String.format("%.1f", tomorrowAbsentPercent)}%",
                    color = if (tomorrowAbsentPercent < targetPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Column 2
                PredictionItemBox(
                    label = "If Absent 5 Days",
                    value = "${String.format("%.1f", fiveDaysAbsentPercent)}%",
                    color = if (fiveDaysAbsentPercent < targetPercent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            if (classesToAttendToSafe > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "To reach safety target of ${targetPercent.toInt()}%, you must attend the next $classesToAttendToSafe classes consecutively.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Attendance safe. You can bunk up to 1 class without falling below target.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PredictionItemBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
fun AIPlannerAdvisoryCard(subjects: List<Subject>, targetPercent: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "🎯 Smart Bunk Planner",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (subjects.isEmpty()) {
                Text("Register subjects to compute skipping advice.", style = MaterialTheme.typography.bodyMedium)
            } else {
                subjects.forEach { sub ->
                    val percentage = if (sub.totalClasses > 0) (sub.attendedClasses.toFloat() / sub.totalClasses) * 100f else 100f
                    val skippable = percentage > targetPercent
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sub.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (skippable) {
                            Badge(containerColor = Color(0xFFE2F5E2), contentColor = Color(0xFF2E6B2E)) {
                                Text(text = "Safe: Skip fine", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        } else {
                            Badge(containerColor = Color(0xFFFDE8E8), contentColor = Color(0xFF9B1C1C)) {
                                Text(text = "Must Attend", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartCalendarView(overallPercent: Float, targetPercent: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "📅 Smart Attendance Calendar Grid",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Daily predictive guide based on scheduled courses strictness:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Custom Drawn Calendar Circular Grid of Days
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (day in 1..7) {
                    val statusColor = when {
                        day % 3 == 0 -> Color(0xFF2E6B2E) // Green (Attend)
                        day % 4 == 0 -> Color(0xFF9B1C1C) // Red (Skipping limit reached)
                        else -> Color(0xFFD39E00) // Yellow (Risk)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$day",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when(day) {
                                1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"; 5 -> "Fri"; 6 -> "Sat"; else -> "Sun"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherStrictnessRiskCard(subjects: List<Subject>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "👩‍🏫 Teacher Wise Risk Matrix",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (subjects.isEmpty()) {
                Text("Register courses to construct the matrix.", style = MaterialTheme.typography.bodySmall)
            } else {
                subjects.forEach { sub ->
                    val riskLevel = when (sub.strictness) {
                        "Strict" -> "High Risk Bunk"
                        "Moderate" -> "Moderate Risk Bunk"
                        else -> "Low Risk Bunk"
                    }
                    val riskColor = when (sub.strictness) {
                        "Strict" -> MaterialTheme.colorScheme.error
                        "Moderate" -> Color(0xFFD39E00)
                        else -> Color(0xFF2E6B2E)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = sub.teacherName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = "${sub.name} (${sub.strictness})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = riskLevel,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = riskColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectTrackerCard(subject: Subject, viewModel: AppViewModel, targetPercent: Float) {
    val percentage = if (subject.totalClasses > 0) {
        (subject.attendedClasses.toFloat() / subject.totalClasses) * 100f
    } else {
        100f
    }
    val belowTarget = percentage < targetPercent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Instructor: ${subject.teacherName} (${subject.strictness})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${subject.attendedClasses}/${subject.totalClasses} classes attended",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Badge(
                        containerColor = if (belowTarget) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (belowTarget) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(text = "${String.format("%.1f", percentage)}%", modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }

            // Updation Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Log Absency / Skip
                IconButton(
                    onClick = { viewModel.updateSubjectAttendance(subject, 0, 1) },
                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Log Skip", tint = MaterialTheme.colorScheme.error)
                }

                // Log Attend
                IconButton(
                    onClick = { viewModel.updateSubjectAttendance(subject, 1, 1) },
                    modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer).size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Log Attend", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                
                // Delete
                IconButton(
                    onClick = { viewModel.deleteSubject(subject.id) }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var strictness by remember { mutableStateOf("Moderate") }
    var total by remember { mutableStateOf("10") }
    var attended by remember { mutableStateOf("8") }

    val strictnessOptions = listOf("Strict", "Moderate", "Relaxed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log New Subject") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_subject_name")
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Strictness row
                Text("Teacher strictness level:", style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    strictnessOptions.forEach { opt ->
                        FilterChip(
                            selected = strictness == opt,
                            onClick = { strictness = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = total,
                        onValueChange = { total = it },
                        label = { Text("Total Classes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = attended,
                        onValueChange = { attended = it },
                        label = { Text("Attended Classes") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && teacher.isNotBlank()) {
                        onAdd(name, teacher, strictness, total.toIntOrNull() ?: 0, attended.toIntOrNull() ?: 0)
                    }
                }
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceSchedulerCard(viewModel: AppViewModel) {
    val isEnabled by viewModel.isReminderEnabled.collectAsState()
    val reminderTime by viewModel.reminderTime.collectAsState()
    val countdown by viewModel.scheduledCountdown.collectAsState()
    val recentAlerts by viewModel.recentAlerts.collectAsState()

    var showTimeDropdown by remember { mutableStateOf(false) }
    var isLogExpanded by remember { mutableStateOf(false) }

    val times = listOf("07:30 AM", "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "06:00 PM")

    Card(
        modifier = Modifier.fillMaxWidth().testTag("attendance_scheduler_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Notification Scheduler",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Alert Scheduler",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { viewModel.toggleReminderEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Triggers browser / device notification alerts every morning advising you on mandatory classes versus safe bunks based on strictness and current percentage.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Schedule Time",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Predicted classes update check",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        Button(
                            onClick = { showTimeDropdown = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = reminderTime,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showTimeDropdown,
                            onDismissRequest = { showTimeDropdown = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            times.forEach { time ->
                                DropdownMenuItem(
                                    text = { Text(time, color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        viewModel.updateReminderTime(time)
                                        showTimeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Scheduler Sandbox Triggers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "TEST SCHEDULE SUITE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.triggerDirectAlertNow() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alert Now", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }

                            Button(
                                onClick = { if (countdown == -1) viewModel.scheduleTestAlert(5) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (countdown != -1) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (countdown != -1) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                if (countdown != -1) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Fires in ${countdown}s...", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                } else {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Test in 5s", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }

                // Expandable Logs list
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isLogExpanded = !isLogExpanded }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Show Alert History (${recentAlerts.size})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = if (isLogExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isLogExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        recentAlerts.take(4).forEach { log ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsNone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = log,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
