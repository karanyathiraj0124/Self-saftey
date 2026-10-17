package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.EmergencyAlert
import com.example.model.PartnerProfile
import com.example.ui.theme.*

@Composable
fun EmergencySosDialog(
    alert: EmergencyAlert,
    currentUser: PartnerProfile,
    emergencyNumber: String,
    onResolveSos: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var resolutionNote by remember { mutableStateOf("") }
    var isSirenActive by remember { mutableStateOf(true) }

    // Pulsing emergency strobe
    val infiniteTransition = rememberInfiniteTransition(label = "sos_strobe")
    val strobeScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strobe_scale"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A050A))
                .testTag("emergency_sos_fullscreen_dialog"),
            color = Color(0xFF140206)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Flashing beacon ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(strobeScale)
                        .clip(CircleShape)
                        .background(EmergencyRed.copy(alpha = 0.25f))
                        .border(3.dp, EmergencyRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = EmergencyRed,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "EMERGENCY SOS ACTIVE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = EmergencyRed,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "Triggered by ${alert.senderName} · Live broadcast active",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Alert details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(EmergencyRed.copy(alpha = 0.6f))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Current Location", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(alert.address, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BatteryStd, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Battery: ${alert.batteryPercent}%", fontSize = 13.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${alert.contactsNotifiedCount} contacts alerted", fontSize = 13.sp, color = TextSecondary)
                            }
                        }

                        if (alert.customMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DividerColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Message:", fontSize = 11.sp, color = TextMuted)
                            Text("\"${alert.customMessage}\"", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Direct Call Local Emergency Services
                Button(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$emergencyNumber")
                        }
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("call_emergency_services_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Emergency Services ($emergencyNumber)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Send SMS dispatch to trusted contacts
                OutlinedButton(
                    onClick = {
                        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:")
                            putExtra(
                                "sms_body",
                                "EMERGENCY: I need help! My location: https://maps.google.com/?q=${alert.latitude},${alert.longitude} (Self Safety SOS)"
                            )
                        }
                        context.startActivity(smsIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_emergency_sms_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                    )
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast SMS with Google Maps Link", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = DividerColor)
                Spacer(modifier = Modifier.height(16.dp))

                // Resolution section
                Text(
                    text = "RESOLVE EMERGENCY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SafetyGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = resolutionNote,
                    onValueChange = { resolutionNote = it },
                    placeholder = { Text("Optional note (e.g., Safe with security team)", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sos_resolution_note_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SafetyGreen,
                        unfocusedBorderColor = NavyCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onResolveSos(resolutionNote) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_safe_sos_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NavyDarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I Am Safe — Cancel Alert",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyDarkBackground
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
