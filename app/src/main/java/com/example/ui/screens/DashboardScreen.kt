package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.EmergencySosButton
import com.example.ui.components.PartnerStatusCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    repository: CoupleGuardRepository,
    onNavigateToMap: () -> Unit,
    onNavigateToCheckIns: () -> Unit,
    onNavigateToTimeline: () -> Unit,
    onTriggerSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coupleState by repository.coupleState.collectAsState()
    val checkIns by repository.checkIns.collectAsState()
    val timelineEvents by repository.timelineEvents.collectAsState()
    val activeAlert by repository.activeEmergencyAlert.collectAsState()

    val currentUser = coupleState.currentUser
    val partnerUser = coupleState.partnerUser

    var showSharingDurationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
    ) {
        // Active Emergency SOS Alert Banner (if someone triggered it)
        if (activeAlert != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTriggerSos() }
                        .testTag("active_sos_warning_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmergencyRed)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE EMERGENCY ALERT",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${activeAlert?.senderName} triggered SOS at ${activeAlert?.address}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        // 1. Guardian Status Card
        item {
            PartnerStatusCard(
                partner = partnerUser,
                onViewMapClick = onNavigateToMap,
                onRequestCheckInClick = onNavigateToCheckIns
            )
        }

        // 2. Personal Self-Safety Immediate Protection Tools
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("self_defense_tools_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SELF SAFETY QUICK TOOLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Fake Call Button
                        Button(
                            onClick = { repository.triggerFakeCall() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_fake_call_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated, contentColor = TextPrimary),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PhoneCallback, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fake Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Discreet Exit", fontSize = 9.sp, color = TextMuted)
                            }
                        }

                        // Deterrent Siren
                        Button(
                            onClick = { repository.toggleSiren() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_siren_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated, contentColor = TextPrimary),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Siren Strobe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Scare Threat", fontSize = 9.sp, color = TextMuted)
                            }
                        }

                        // Solo Walk Timer
                        Button(
                            onClick = onNavigateToCheckIns,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_solo_timer_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated, contentColor = TextPrimary),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Safety Timer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Auto Dead-Man", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // 3. Large Tactile Emergency SOS Trigger
        item {
            EmergencySosButton(
                onTriggerSos = onTriggerSos
            )
        }

        // 3. Your Location Sharing Status Card (Independent control)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("my_location_control_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentUser.isLocationSharingEnabled) Icons.Default.ShareLocation else Icons.Default.LocationOff,
                                contentDescription = null,
                                tint = if (currentUser.isLocationSharingEnabled) CyanAccent else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Your Location Sharing",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (currentUser.isLocationSharingEnabled) {
                                        "Mode: ${currentUser.sharingDuration.label}"
                                    } else "Paused · Not visible to partner",
                                    fontSize = 12.sp,
                                    color = if (currentUser.isLocationSharingEnabled) CyanGlow else WarningAmber
                                )
                            }
                        }

                        Switch(
                            checked = currentUser.isLocationSharingEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showSharingDurationDialog = true
                                } else {
                                    repository.toggleLocationSharing(false, currentUser.sharingDuration)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyanGlow,
                                checkedTrackColor = CyanSubtle,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = NavyDarkSurface
                            ),
                            modifier = Modifier.testTag("my_location_toggle_switch")
                        )
                    }

                    if (currentUser.isLocationSharingEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SharingDuration.values().forEach { duration ->
                                FilterChip(
                                    selected = currentUser.sharingDuration == duration,
                                    onClick = {
                                        repository.toggleLocationSharing(true, duration)
                                    },
                                    label = { Text(duration.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricBlueSubtle,
                                        selectedLabelColor = CyanGlow,
                                        containerColor = NavyDarkSurface,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Upcoming / Active Safety Check-In
        item {
            val pendingCheckIn = checkIns.firstOrNull { it.status == CheckInStatus.PENDING }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_checkin_summary_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Safety Check-In",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        TextButton(onClick = onNavigateToCheckIns) {
                            Text("Manage", fontSize = 12.sp, color = CyanGlow)
                        }
                    }

                    if (pendingCheckIn != null) {
                        val remainingMinutes = ((pendingCheckIn.scheduledTimeMillis - System.currentTimeMillis()) / 60_000L).coerceAtLeast(0)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NavyDarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pendingCheckIn.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "By ${pendingCheckIn.creatorName} · Due in ~$remainingMinutes mins",
                                        fontSize = 12.sp,
                                        color = WarningAmber
                                    )
                                }
                                Button(
                                    onClick = { repository.confirmCheckInSafe(pendingCheckIn.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("quick_confirm_safe_button")
                                ) {
                                    Text("I'm Safe", fontWeight = FontWeight.Bold, color = NavyDarkBackground, fontSize = 12.sp)
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavyDarkSurface)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "No active check-ins pending.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            OutlinedButton(
                                onClick = onNavigateToCheckIns,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("schedule_checkin_button")
                            ) {
                                Text("+ Schedule", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5. Recent Consented Safety Timeline Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recent_timeline_preview_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Safety Events",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        TextButton(onClick = onNavigateToTimeline) {
                            Text("View All", fontSize = 12.sp, color = CyanGlow)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    timelineEvents.take(3).forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val tintColor = when (event.severity) {
                                EventSeverity.CRITICAL -> EmergencyRed
                                EventSeverity.WARNING -> WarningAmber
                                EventSeverity.SUCCESS -> SafetyGreen
                                EventSeverity.INFO -> CyanAccent
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(tintColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = event.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to pick duration when toggling sharing
    if (showSharingDurationDialog) {
        AlertDialog(
            onDismissRequest = { showSharingDurationDialog = false },
            containerColor = NavyDarkCard,
            title = { Text("Select Sharing Duration", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        "How long would you like to voluntarily share your live GPS coordinates with ${partnerUser.name}?",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SharingDuration.values().forEach { duration ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    repository.toggleLocationSharing(true, duration)
                                    showSharingDurationDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(duration.label, color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSharingDurationDialog = false }) {
                    Text("Close", color = TextMuted)
                }
            }
        )
    }
}
