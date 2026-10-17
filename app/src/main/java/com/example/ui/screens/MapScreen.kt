package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGuardRepository
import com.example.model.MotionActivity
import com.example.model.SharingDuration
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.theme.*

@Composable
fun MapScreen(
    repository: CoupleGuardRepository,
    modifier: Modifier = Modifier
) {
    val coupleState by repository.coupleState.collectAsState()
    val savedPlaces by repository.savedPlaces.collectAsState()

    val currentUser = coupleState.currentUser
    val partnerUser = coupleState.partnerUser

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .testTag("map_screen_root")
    ) {
        // Map Canvas
        InteractiveMapCanvas(
            currentUser = currentUser,
            partnerUser = partnerUser,
            savedPlaces = savedPlaces,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // Bottom Telemetry & Controls Panel
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = NavyDarkCard,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Partner Live Status or Paused Notice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = partnerUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (partnerUser.isLocationSharingEnabled) "Authorized live stream" else "Voluntary sharing disabled by partner",
                            fontSize = 12.sp,
                            color = if (partnerUser.isLocationSharingEnabled) CyanGlow else WarningAmber
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavyDarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (partnerUser.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                                contentDescription = null,
                                tint = SafetyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${partnerUser.batteryPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }

                if (partnerUser.isLocationSharingEnabled && partnerUser.location != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = partnerUser.location.address,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${partnerUser.location.speedKmh} km/h", fontSize = 12.sp, color = TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.GpsFixed, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accuracy: ±${partnerUser.location.accuracyMeters.toInt()}m", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(modifier = Modifier.height(10.dp))

                // Your Own Sharing Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Share Your Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (currentUser.isLocationSharingEnabled) {
                                currentUser.sharingExpiryMillis?.let {
                                    val mins = ((it - System.currentTimeMillis()) / 60_000L).coerceAtLeast(0)
                                    "Auto-expires in ${mins}m"
                                } ?: "Always active"
                            } else "Private to you",
                            fontSize = 11.sp,
                            color = if (currentUser.isLocationSharingEnabled) CyanGlow else TextMuted
                        )
                    }

                    Switch(
                        checked = currentUser.isLocationSharingEnabled,
                        onCheckedChange = { enabled ->
                            repository.toggleLocationSharing(enabled, currentUser.sharingDuration)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanGlow,
                            checkedTrackColor = CyanSubtle
                        ),
                        modifier = Modifier.testTag("map_share_switch")
                    )
                }

                if (currentUser.isLocationSharingEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SharingDuration.values().forEach { duration ->
                            FilterChip(
                                selected = currentUser.sharingDuration == duration,
                                onClick = {
                                    repository.toggleLocationSharing(true, duration)
                                },
                                label = { Text(duration.label, fontSize = 10.sp) },
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

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
