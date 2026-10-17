package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PartnerProfile
import com.example.model.SavedPlace
import com.example.ui.theme.*

@Composable
fun InteractiveMapCanvas(
    currentUser: PartnerProfile,
    partnerUser: PartnerProfile,
    savedPlaces: List<SavedPlace>,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var showSafeZones by remember { mutableStateOf(true) }

    // Pulsing radar animation for partner pin
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_radius"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(NavyDarkBackground)
            .testTag("interactive_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f + offsetX
            val centerY = canvasH / 2f + offsetY

            // 1. Draw Map Base (Water body on right side - SF Bay style)
            val waterPath = Path().apply {
                moveTo(canvasW * 0.75f + offsetX * 0.5f, 0f)
                cubicTo(
                    canvasW * 0.70f + offsetX * 0.5f, canvasH * 0.35f,
                    canvasW * 0.85f + offsetX * 0.5f, canvasH * 0.65f,
                    canvasW * 0.72f + offsetX * 0.5f, canvasH
                )
                lineTo(canvasW, canvasH)
                lineTo(canvasW, 0f)
                close()
            }
            drawPath(waterPath, color = Color(0xFF0F2042))

            // 2. Draw Park Area (Green zone)
            drawRoundRect(
                color = Color(0xFF132E27),
                topLeft = Offset(centerX - 160f * zoomLevel, centerY - 240f * zoomLevel),
                size = Size(100f * zoomLevel, 140f * zoomLevel),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // 3. Draw Grid & Street Network
            val gridStep = 60f * zoomLevel
            val startGridX = (centerX % gridStep) - gridStep
            val startGridY = (centerY % gridStep) - gridStep

            // Secondary streets
            var gx = startGridX
            while (gx < canvasW + gridStep) {
                drawLine(
                    color = Color(0xFF18223D),
                    start = Offset(gx, 0f),
                    end = Offset(gx, canvasH),
                    strokeWidth = 2f
                )
                gx += gridStep
            }

            var gy = startGridY
            while (gy < canvasH + gridStep) {
                drawLine(
                    color = Color(0xFF18223D),
                    start = Offset(0f, gy),
                    end = Offset(canvasW, gy),
                    strokeWidth = 2f
                )
                gy += gridStep
            }

            // Primary Arterial Avenues (Main roads)
            drawLine(
                color = Color(0xFF283659),
                start = Offset(0f, centerY),
                end = Offset(canvasW, centerY),
                strokeWidth = 6f
            )
            drawLine(
                color = Color(0xFF283659),
                start = Offset(centerX, 0f),
                end = Offset(centerX, canvasH),
                strokeWidth = 6f
            )
            // Diagonal boulevard
            drawLine(
                color = Color(0xFF334470),
                start = Offset(0f, centerY + 180f * zoomLevel),
                end = Offset(canvasW, centerY - 180f * zoomLevel),
                strokeWidth = 8f
            )

            // 4. Draw Safe Zones / Geofences (if enabled)
            if (showSafeZones) {
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                savedPlaces.forEachIndexed { index, place ->
                    if (place.isEnabled) {
                        val placeX = centerX + (index * 110f - 110f) * zoomLevel
                        val placeY = centerY + (if (index % 2 == 0) -80f else 90f) * zoomLevel
                        val zoneRadius = (place.radiusMeters * 0.45f) * zoomLevel

                        // Transparent fill
                        drawCircle(
                            color = CyanAccent.copy(alpha = 0.12f),
                            radius = zoneRadius,
                            center = Offset(placeX, placeY)
                        )
                        // Dashed boundary
                        drawCircle(
                            color = CyanGlow.copy(alpha = 0.6f),
                            radius = zoneRadius,
                            center = Offset(placeX, placeY),
                            style = Stroke(width = 2.5f, pathEffect = dashedEffect)
                        )
                    }
                }
            }

            // Positions on canvas
            val userPos = Offset(centerX - 80f * zoomLevel, centerY + 60f * zoomLevel)
            val partnerPos = Offset(centerX + 90f * zoomLevel, centerY - 70f * zoomLevel)

            // 5. If Partner Sharing is ON, draw connection line and partner marker
            if (partnerUser.isLocationSharingEnabled && partnerUser.location != null) {
                // Connecting trajectory line
                val trajectoryEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                drawLine(
                    color = ElectricBlue.copy(alpha = 0.6f),
                    start = userPos,
                    end = partnerPos,
                    strokeWidth = 3f,
                    pathEffect = trajectoryEffect
                )

                // Partner pulsing radar rings
                drawCircle(
                    color = ElectricBlue.copy(alpha = radarAlpha),
                    radius = radarRadius * zoomLevel,
                    center = partnerPos
                )
                // Partner accuracy ring
                drawCircle(
                    color = CyanAccent.copy(alpha = 0.2f),
                    radius = 28f * zoomLevel,
                    center = partnerPos
                )
                // Partner inner pin
                drawCircle(
                    color = Color.White,
                    radius = 12f * zoomLevel,
                    center = partnerPos
                )
                drawCircle(
                    color = ElectricBlue,
                    radius = 9f * zoomLevel,
                    center = partnerPos
                )
            }

            // 6. Draw User Marker ("You")
            drawCircle(
                color = CyanAccent.copy(alpha = 0.3f),
                radius = 24f * zoomLevel,
                center = userPos
            )
            drawCircle(
                color = Color.White,
                radius = 11f * zoomLevel,
                center = userPos
            )
            drawCircle(
                color = CyanAccent,
                radius = 8f * zoomLevel,
                center = userPos
            )
        }

        // Overlay 1: Live Status Header Pill
        Surface(
            modifier = Modifier
                .padding(14.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(20.dp),
            color = NavyDarkCard.copy(alpha = 0.92f),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (partnerUser.isLocationSharingEnabled) SafetyGreen else WarningAmber)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (partnerUser.isLocationSharingEnabled) "2.1 km apart · Consented" else "Partner Sharing Paused",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        // Overlay 2: Privacy Banner when partner sharing is disabled
        if (!partnerUser.isLocationSharingEnabled) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                shape = RoundedCornerShape(16.dp),
                color = NavyDarkCard.copy(alpha = 0.95f),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(WarningAmber.copy(alpha = 0.5f))
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Shield",
                        tint = WarningAmber,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mutual Privacy Active",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${partnerUser.name} has not authorized live coordinates. Self Safety strictly honors voluntary consent.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Overlay 3: Interactive Floating Map Controls
        Column(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Toggle Safe Zones
            SmallFloatingActionButton(
                onClick = { showSafeZones = !showSafeZones },
                containerColor = if (showSafeZones) CyanSubtle else NavyDarkCard,
                contentColor = if (showSafeZones) CyanGlow else TextSecondary,
                modifier = Modifier.testTag("map_toggle_zones_button")
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Toggle Safe Zones", modifier = Modifier.size(18.dp))
            }

            // Recenter
            SmallFloatingActionButton(
                onClick = {
                    offsetX = 0f
                    offsetY = 0f
                    zoomLevel = 1f
                },
                containerColor = NavyDarkCard,
                contentColor = TextPrimary,
                modifier = Modifier.testTag("map_recenter_button")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter Map", modifier = Modifier.size(18.dp))
            }

            // Zoom In
            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.5f) },
                containerColor = NavyDarkCard,
                contentColor = TextPrimary,
                modifier = Modifier.testTag("map_zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
            }

            // Zoom Out
            SmallFloatingActionButton(
                onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.6f) },
                containerColor = NavyDarkCard,
                contentColor = TextPrimary,
                modifier = Modifier.testTag("map_zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
            }
        }
    }
}
