package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGuardRepository
import com.example.model.CheckInStatus
import com.example.model.SafetyCheckIn
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CheckInScreen(
    repository: CoupleGuardRepository,
    modifier: Modifier = Modifier
) {
    val checkIns by repository.checkIns.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var selectedDurationMinutes by remember { mutableIntStateOf(30) }

    val activeCheckIns = checkIns.filter { it.status == CheckInStatus.PENDING }
    val pastCheckIns = checkIns.filter { it.status != CheckInStatus.PENDING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // Section 1: Active Check-ins
        item {
            Text(
                text = "ACTIVE SAFETY CHECK-INS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanGlow,
                letterSpacing = 1.sp
            )
        }

        if (activeCheckIns.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDarkCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All Scheduled Check-ins Complete", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                        Text("Schedule a new check-in below before heading out.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(activeCheckIns) { checkIn ->
                val remainingMinutes = ((checkIn.scheduledTimeMillis - System.currentTimeMillis()) / 60_000L).coerceAtLeast(0)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_checkin_card_${checkIn.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.6f))
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(WarningAmber)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DUE IN ~$remainingMinutes MINS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarningAmber
                                )
                            }
                            Text(
                                text = "By ${checkIn.creatorName}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(checkIn.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                        if (checkIn.note.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Note: ${checkIn.note}", fontSize = 12.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { repository.confirmCheckInSafe(checkIn.id) },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("confirm_safe_button_${checkIn.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NavyDarkBackground, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("I Am Safe", fontWeight = FontWeight.Bold, color = NavyDarkBackground, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { repository.simulateMissedCheckIn(checkIn.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("test_missed_checkin_button_${checkIn.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.4f))
                                )
                            ) {
                                Text("Test Missed", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Schedule a New Check-In
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_checkin_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Schedule Safety Check-In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Your partner is notified when you confirm safety or if a deadline passes.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset buttons
                    Text("Quick Presets", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Walk Home", 20, "Walking home from station"),
                            Triple("Rideshare", 15, "Uber/Lyft trip"),
                            Triple("Night Run", 45, "Solo evening run")
                        ).forEach { preset ->
                            OutlinedButton(
                                onClick = {
                                    titleInput = preset.first
                                    selectedDurationMinutes = preset.second
                                    noteInput = preset.third
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text(preset.first, fontSize = 11.sp, color = CyanGlow, maxLines = 1)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Trip or Activity Description") },
                        placeholder = { Text("e.g. Walking from library to campus") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkin_title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = NavyCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Optional Safety Route Note") },
                        placeholder = { Text("e.g. Taking 4th Street bus line") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkin_note_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = NavyCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Timer Duration: $selectedDurationMinutes minutes", fontSize = 12.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                    Slider(
                        value = selectedDurationMinutes.toFloat(),
                        onValueChange = { selectedDurationMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = NavyDarkSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (titleInput.isNotBlank()) {
                                repository.scheduleCheckIn(titleInput, selectedDurationMinutes, noteInput)
                                titleInput = ""
                                noteInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_schedule_checkin_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        enabled = titleInput.isNotBlank()
                    ) {
                        Icon(Icons.Default.AddAlarm, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Safety Timer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: Past Check-Ins Log
        if (pastCheckIns.isNotEmpty()) {
            item {
                Text(
                    text = "CHECK-IN HISTORY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            items(pastCheckIns) { checkIn ->
                val timeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
                val isSafe = checkIn.status == CheckInStatus.CONFIRMED_SAFE

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = NavyDarkCard,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSafe) SafetyGreen.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.5f)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (isSafe) SafetyGreen else WarningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(checkIn.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                            Text(
                                text = if (isSafe) "Confirmed Safe at ${timeFormat.format(Date(checkIn.confirmedAtMillis ?: checkIn.scheduledTimeMillis))}"
                                else "Missed deadline — unconfirmed status",
                                fontSize = 12.sp,
                                color = if (isSafe) SafetyGreen else WarningAmber
                            )
                        }
                    }
                }
            }
        }
    }
}
