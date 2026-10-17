package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGuardRepository
import com.example.model.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private val repository = CoupleGuardRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CoupleGuardApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoupleGuardApp(repository: CoupleGuardRepository) {
    var selectedTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    val coupleState by repository.coupleState.collectAsState()
    val activePersonaId by repository.activePersonaId.collectAsState()
    val activeAlert by repository.activeEmergencyAlert.collectAsState()
    val notificationBanner by repository.notificationBanner.collectAsState()
    val privacyPrefs by repository.privacyPreferences.collectAsState()
    val isFakeCallActive by repository.isFakeCallActive.collectAsState()
    val isSirenActive by repository.isSirenActive.collectAsState()

    var showPairingDialog by remember { mutableStateOf(false) }
    var showSupabaseGuide by remember { mutableStateOf(false) }
    var showSosModalManually by remember { mutableStateOf(false) }

    // Handle back button: return to dashboard first before exiting
    BackHandler(enabled = selectedTab != NavigationTab.DASHBOARD) {
        selectedTab = NavigationTab.DASHBOARD
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Self Safety",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Personal Solo Guardian",
                                fontSize = 10.sp,
                                color = CyanGlow
                            )
                        }
                    }
                },
                actions = {
                    // Persona Switcher Chip (Alex <-> Taylor) for mutual simulation
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                val nextPersona = if (activePersonaId == "user_alex") "user_taylor" else "user_alex"
                                repository.switchPersona(nextPersona)
                            }
                            .testTag("persona_switcher_chip"),
                        color = NavyDarkCard,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(CyanAccent.copy(alpha = 0.5f))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SyncAlt,
                                contentDescription = "Switch Persona",
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = coupleState.currentUser.name.substringBefore(" "),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Pairing status indicator button
                    IconButton(
                        onClick = { showPairingDialog = true },
                        modifier = Modifier.testTag("topbar_pairing_button")
                    ) {
                        Icon(
                            imageVector = if (coupleState.status == PairingStatus.ACTIVE) Icons.Default.Link else Icons.Default.LinkOff,
                            contentDescription = "Pairing settings",
                            tint = if (coupleState.status == PairingStatus.ACTIVE) SafetyGreen else WarningAmber
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDarkSurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyDarkSurface,
                contentColor = TextPrimary,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("coupleguard_bottom_nav")
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val icon = when (tab) {
                        NavigationTab.DASHBOARD -> if (isSelected) Icons.Default.Shield else Icons.Default.Shield
                        NavigationTab.MAP -> Icons.Default.Map
                        NavigationTab.CHECKINS -> Icons.Default.CheckCircle
                        NavigationTab.TIMELINE -> Icons.Default.History
                        NavigationTab.PRIVACY -> Icons.Default.Lock
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = { Icon(icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDarkBackground,
                            indicatorColor = CyanGlow,
                            selectedTextColor = CyanGlow,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen content
            when (selectedTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(
                    repository = repository,
                    onNavigateToMap = { selectedTab = NavigationTab.MAP },
                    onNavigateToCheckIns = { selectedTab = NavigationTab.CHECKINS },
                    onNavigateToTimeline = { selectedTab = NavigationTab.TIMELINE },
                    onTriggerSos = { showSosModalManually = true }
                )
                NavigationTab.MAP -> MapScreen(
                    repository = repository
                )
                NavigationTab.CHECKINS -> CheckInScreen(
                    repository = repository
                )
                NavigationTab.TIMELINE -> TimelineScreen(
                    repository = repository
                )
                NavigationTab.PRIVACY -> PrivacySettingsScreen(
                    repository = repository,
                    onOpenPairingDialog = { showPairingDialog = true },
                    onOpenSupabaseGuide = { showSupabaseGuide = true }
                )
            }

            // In-App Notification / Transient Alert Banner
            AnimatedVisibility(
                visible = notificationBanner != null,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyDarkCard,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(ElectricBlue)
                    ),
                    modifier = Modifier.clickable { repository.dismissBanner() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = notificationBanner ?: "",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { repository.dismissBanner() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Emergency SOS Dialog (either active or previewed)
    if (activeAlert != null || showSosModalManually) {
        val currentAlert = activeAlert ?: EmergencyAlert(
            id = "sos_manual",
            senderId = coupleState.currentUser.id,
            senderName = coupleState.currentUser.name,
            triggeredAtMillis = System.currentTimeMillis(),
            resolvedAtMillis = null,
            status = SosStatus.ACTIVE,
            latitude = coupleState.currentUser.location?.latitude ?: 37.7749,
            longitude = coupleState.currentUser.location?.longitude ?: -122.4194,
            address = coupleState.currentUser.location?.address ?: "Current GPS coordinates",
            batteryPercent = coupleState.currentUser.batteryPercent,
            customMessage = "Emergency SOS initiated! Partner and trusted contacts alerted.",
            contactsNotifiedCount = 2
        )

        EmergencySosDialog(
            alert = currentAlert,
            currentUser = coupleState.currentUser,
            emergencyNumber = privacyPrefs.emergencyServiceNumber,
            onResolveSos = { note ->
                repository.resolveEmergencySos(note)
                showSosModalManually = false
            },
            onDismissRequest = {
                showSosModalManually = false
            }
        )
    }

    // Pairing Management Dialog
    if (showPairingDialog) {
        PairingDialog(
            coupleState = coupleState,
            onPairWithCode = { code -> repository.pairWithCode(code) },
            onGenerateNewCode = { repository.generateNewInviteCode() },
            onDisconnectPairing = { repository.disconnectPairing() },
            onReconnectDemo = { repository.reconnectDemoPairing() },
            onDismissRequest = { showPairingDialog = false }
        )
    }

    // Supabase & Architecture Guide Dialog
    if (showSupabaseGuide) {
        SupabaseGuideDialog(
            onDismissRequest = { showSupabaseGuide = false }
        )
    }

    // Discreet Fake Call Dialog
    if (isFakeCallActive) {
        FakeCallDialog(
            callerName = "Mom (Home)",
            onDismissRequest = { repository.dismissFakeCall() }
        )
    }

    // Deterrent Siren & Strobe Overlay
    if (isSirenActive) {
        SirenOverlay(
            onDismissRequest = { repository.toggleSiren() }
        )
    }
}
