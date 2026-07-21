package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch

enum class NavDestination(val route: String, val title: String, val icon: ImageVector) {
    HOME("home", "Dashboard", Icons.Default.Home),
    INTERVIEW("interview_setup", "AI Interviewer", Icons.Default.ModelTraining),
    ATTENDANCE("attendance_tracker", "Smart Planner", Icons.Default.DateRange),
    CHAT("attendance_chat", "BunkBuddy AI", Icons.Default.Chat),
    PROFILE("profile", "My Profile", Icons.Default.ManageAccounts)
}

@Composable
fun ResponsiveNavigationContainer(
    viewModel: AppViewModel,
    content: @Composable () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val currentDestination = when (currentScreen) {
        "home" -> NavDestination.HOME
        "interview_setup", "interview_active", "interview_report" -> NavDestination.INTERVIEW
        "attendance_tracker" -> NavDestination.ATTENDANCE
        "attendance_chat" -> NavDestination.CHAT
        "profile" -> NavDestination.PROFILE
        else -> NavDestination.HOME
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp
        
        if (isWideScreen) {
            // Responsive Tablet/Desktop Side-Navigation Layout
            Row(modifier = Modifier.fillMaxSize()) {
                SideNavigationRail(
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        viewModel.navigateTo(dest.route)
                    }
                )
                
                // Fine high-tech divider
                VerticalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxHeight()
                )
                
                // Screen content
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        } else {
            // Mobile Layout with Collapsible Side Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.background,
                        drawerContentColor = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        // Header block of side drawer matching Sophisticated Dark
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                            Text(
                                text = "STUDENT CAREER HUB",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BunkBuddy",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = " AI",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        
                        // Menu destinations list
                        NavDestination.entries.forEach { dest ->
                            val isSelected = currentDestination == dest
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = dest.title,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    viewModel.navigateTo(dest.route)
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    unselectedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                    
                    // Elegant subtle bottom-left floating hamburger trigger button for accessibility and screen space optimization
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.65f))
                                .shadow(6.dp, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Side Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavigationRail(
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(Color(0xFF050505))
            .padding(vertical = 32.dp, horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // High-fidelity header branding
            Text(
                text = "AI INTERVIEW SIMULATOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color(0xFF64748B) // Slate-500
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "BunkBuddy",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF14B8A6) // Teal-500
                )
                Text(
                    text = " AI",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light),
                    color = Color(0xFFF1F5F9)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Destinations List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavDestination.entries.forEach { dest ->
                    val isSelected = currentDestination == dest
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Color(0xFF14B8A6).copy(alpha = 0.1f) else Color.Transparent
                            )
                            .clickable { onNavigate(dest) }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = dest.icon,
                            contentDescription = dest.title,
                            tint = if (isSelected) Color(0xFF14B8A6) else Color(0xFF94A3B8),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = dest.title,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF14B8A6) else Color(0xFFF1F5F9)
                            )
                        )
                    }
                }
            }
        }
        
        // Lower visual user details block
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF334155), Color(0xFF0F172A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JD",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF94A3B8)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Candidate Hub",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFF1F5F9)
                )
                Text(
                    text = "LIVE ANALYTICS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = Color(0xFF14B8A6)
                )
            }
        }
    }
}
