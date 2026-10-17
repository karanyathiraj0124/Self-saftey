package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FakeCallDialog(
    callerName: String = "Mom (Home)",
    onDismissRequest: () -> Unit
) {
    var isCallAnswered by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isCallAnswered) {
        if (isCallAnswered) {
            while (true) {
                delay(1000)
                callSeconds++
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ring_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "call_ring"
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("fake_call_screen"),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top caller info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text(
                        text = if (isCallAnswered) "Active Call" else "Incoming Call...",
                        fontSize = 14.sp,
                        color = if (isCallAnswered) SafetyGreen else CyanGlow,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = callerName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isCallAnswered) String.format("%02d:%02d", callSeconds / 60, callSeconds % 60) else "+1 (555) 019-2831",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Caller Avatar with ringing pulse
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(if (!isCallAnswered) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(NavyCardElevated)
                            .border(2.dp, if (isCallAnswered) SafetyGreen else CyanAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Middle safety prompt hint
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Self Safety Discreet Exit · Use this call to leave an uneasy situation",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Bottom Call Controls
                if (!isCallAnswered) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 36.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(EmergencyRed)
                                    .testTag("decline_fake_call_button")
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Decline", fontSize = 12.sp, color = TextSecondary)
                        }

                        // Answer
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { isCallAnswered = true },
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(SafetyGreen)
                                    .testTag("answer_fake_call_button")
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Answer", tint = Color.White, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Answer", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                } else {
                    // Active call controls (Mute, Keypad, End Call)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = {}, modifier = Modifier.size(48.dp).clip(CircleShape).background(NavyCardElevated)) {
                                Icon(Icons.Default.MicOff, contentDescription = "Mute", tint = Color.White)
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(48.dp).clip(CircleShape).background(NavyCardElevated)) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(48.dp).clip(CircleShape).background(NavyCardElevated)) {
                                Icon(Icons.Default.Dialpad, contentDescription = "Keypad", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(EmergencyRed)
                                .testTag("end_fake_call_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("End Call", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
