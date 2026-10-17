package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGuardRepository
import com.example.model.PlaceCategory
import com.example.model.PrivacyPreferences
import com.example.model.SavedPlace
import com.example.model.TrustedContact
import com.example.ui.theme.*

@Composable
fun PrivacySettingsScreen(
    repository: CoupleGuardRepository,
    onOpenPairingDialog: () -> Unit,
    onOpenSupabaseGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coupleState by repository.coupleState.collectAsState()
    val savedPlaces by repository.savedPlaces.collectAsState()
    val trustedContacts by repository.trustedContacts.collectAsState()
    val privacyPrefs by repository.privacyPreferences.collectAsState()

    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showExportConfirmDialog by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // Section 1: Privacy Guarantee Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_guarantee_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SafetyGreen.copy(alpha = 0.5f))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Self Safety Privacy Pledge",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "Zero snooping: No call, audio, or microphone recording.",
                        "Zero message reading: Never reads SMS, chats, or keystrokes.",
                        "Equal partner rights: Either user can disconnect instantly.",
                        "Transparent sharing: Visible status whenever GPS is active."
                    ).forEach { pledge ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pledge, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Section 2: Partner Pairing & Consent Management
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pairing_settings_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Partner Pairing", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Text(
                                text = "Paired with ${coupleState.partnerUser.name}",
                                fontSize = 12.sp,
                                color = CyanGlow
                            )
                        }
                        Button(
                            onClick = onOpenPairingDialog,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated, contentColor = CyanGlow),
                            modifier = Modifier.testTag("manage_pairing_button")
                        ) {
                            Text("Manage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Section 3: Privacy & Telemetry Toggles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("telemetry_toggles_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Voluntary Data Controls", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Share Battery Level", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text("Lets partner see your phone charge percentage", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = privacyPrefs.shareBatteryLevel,
                            onCheckedChange = {
                                repository.updatePrivacyPreferences(privacyPrefs.copy(shareBatteryLevel = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = CyanSubtle)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = DividerColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Precise GPS Coordinates", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text("Sub-15m accuracy instead of neighborhood area", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = privacyPrefs.sharePreciseLocation,
                            onCheckedChange = {
                                repository.updatePrivacyPreferences(privacyPrefs.copy(sharePreciseLocation = it))
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = CyanSubtle)
                        )
                    }
                }
            }
        }

        // Section 4: Saved Destinations / Geofences
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saved_destinations_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Saved Destinations", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Text("Optional arrival & departure alerts", fontSize = 11.sp, color = TextSecondary)
                        }
                        OutlinedButton(
                            onClick = { showAddPlaceDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_destination_button")
                        ) {
                            Text("+ Add Place", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    savedPlaces.forEach { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(place.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                                Text("${place.category.label} · ${place.radiusMeters}m radius", fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = place.isEnabled,
                                onCheckedChange = { repository.toggleSavedPlace(place.id) },
                                colors = SwitchDefaults.colors(checkedThumbColor = SafetyGreen, checkedTrackColor = SafetyGreenSubtle)
                            )
                            IconButton(onClick = { repository.deleteSavedPlace(place.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Trusted Emergency Contacts
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trusted_contacts_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Trusted Contacts", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Text("Alerted during SOS or missed check-ins", fontSize = 11.sp, color = TextSecondary)
                        }
                        OutlinedButton(
                            onClick = { showAddContactDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_contact_button")
                        ) {
                            Text("+ Add Contact", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    trustedContacts.forEach { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NavyDarkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(contact.name.take(1), fontWeight = FontWeight.Bold, color = CyanGlow, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                                Text("${contact.relationship} · ${contact.phone}", fontSize = 11.sp, color = TextSecondary)
                            }
                            if (contact.isEmergencyContact) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmergencyRedSubtle
                                ) {
                                    Text(
                                        text = "SOS",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmergencyRed
                                    )
                                }
                            }
                            IconButton(onClick = { repository.removeTrustedContact(contact.id) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Section 6: Data Rights, Export, and Backend Guide
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("developer_and_data_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(NavyCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Data Governance & Backend Architecture", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onOpenSupabaseGuide,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("view_supabase_guide_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Supabase SQL Schema & Windows Guide", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val json = repository.exportSafetyDataJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Self Safety Export", json)
                                clipboard.setPrimaryClip(clip)
                                showExportConfirmDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_data_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Data", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showWipeConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("wipe_data_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(EmergencyRed.copy(alpha = 0.5f))
                            )
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wipe Data", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Add Place Dialog
    if (showAddPlaceDialog) {
        AddPlaceDialog(
            onAddPlace = { name, cat, addr, radius, arr, dep ->
                repository.addSavedPlace(name, cat, addr, radius, arr, dep)
            },
            onDismissRequest = { showAddPlaceDialog = false }
        )
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        var contactName by remember { mutableStateOf("") }
        var contactPhone by remember { mutableStateOf("") }
        var contactRelation by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            containerColor = NavyDarkCard,
            title = { Text("Add Trusted Contact", color = TextPrimary) },
            text = {
                Column {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Contact Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = contactRelation,
                        onValueChange = { contactRelation = it },
                        label = { Text("Relationship (e.g. Sister, Mom, Friend)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                            repository.addTrustedContact(contactName, contactPhone, contactRelation.ifBlank { "Family" })
                            showAddContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = NavyDarkBackground)
                ) {
                    Text("Save Contact", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Export Confirmation
    if (showExportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExportConfirmDialog = false },
            containerColor = NavyDarkCard,
            title = { Text("Data Export Complete", color = TextPrimary) },
            text = { Text("Your full safety records, pairing state, and preferences have been copied to the clipboard in JSON format.", color = TextSecondary) },
            confirmButton = {
                Button(onClick = { showExportConfirmDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Wipe Confirmation
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            containerColor = NavyDarkCard,
            title = { Text("Wipe All Local Data?", color = EmergencyRed) },
            text = { Text("This will disconnect pairing, delete all check-in logs, and reset privacy preferences.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        repository.disconnectPairing()
                        repository.clearTimelineEvents()
                        showWipeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Wipe Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
