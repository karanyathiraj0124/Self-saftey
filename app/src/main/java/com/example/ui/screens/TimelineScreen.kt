package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGuardRepository
import com.example.model.EventSeverity
import com.example.model.TimelineEventType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimelineScreen(
    repository: CoupleGuardRepository,
    modifier: Modifier = Modifier
) {
    val timelineEvents by repository.timelineEvents.collectAsState()
    var selectedFilter by remember { mutableStateOf<TimelineEventType?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredEvents = remember(timelineEvents, selectedFilter) {
        if (selectedFilter == null) timelineEvents
        else timelineEvents.filter { it.type == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .testTag("timeline_screen_root")
    ) {
        // Top Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All Events", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlueSubtle,
                        selectedLabelColor = CyanGlow,
                        containerColor = NavyDarkCard,
                        labelColor = TextSecondary
                    )
                )
            }
            items(TimelineEventType.values()) { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { selectedFilter = type },
                    label = { Text(type.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlueSubtle,
                        selectedLabelColor = CyanGlow,
                        containerColor = NavyDarkCard,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // Timeline events list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp)
        ) {
            // Retention guarantee header
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyDarkCard,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Logs strictly consented safety events. Zero phone tapping, keystroke logging, or private SMS tracking.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            if (filteredEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No events match the selected filter.", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredEvents) { event ->
                    val timeFormat = SimpleDateFormat("HH:mm · MMM d", Locale.getDefault())
                    val eventColor = when (event.severity) {
                        EventSeverity.CRITICAL -> EmergencyRed
                        EventSeverity.WARNING -> WarningAmber
                        EventSeverity.SUCCESS -> SafetyGreen
                        EventSeverity.INFO -> CyanAccent
                    }
                    val icon = when (event.type) {
                        TimelineEventType.SOS_ALERT -> Icons.Default.Warning
                        TimelineEventType.CHECK_IN -> Icons.Default.CheckCircle
                        TimelineEventType.LOCATION_SHARING -> Icons.Default.ShareLocation
                        TimelineEventType.GEOFENCE -> Icons.Default.PinDrop
                        TimelineEventType.PAIRING -> Icons.Default.People
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("timeline_event_card_${event.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(eventColor.copy(alpha = 0.15f))
                                    .border(1.5.dp, eventColor.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = eventColor, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = event.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = timeFormat.format(Date(event.timestampMillis)),
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = event.description,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "By ${event.actorName}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CyanGlow
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_timeline_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                        )
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear Event Timeline", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = NavyDarkCard,
            title = { Text("Clear Timeline?", color = TextPrimary) },
            text = { Text("This will erase the local record of past check-ins and location transitions.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        repository.clearTimelineEvents()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
