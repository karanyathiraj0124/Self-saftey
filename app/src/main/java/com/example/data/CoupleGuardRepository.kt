package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CoupleGuardRepository {

    private val now = System.currentTimeMillis()

    // Default Partner A: Alex Carter
    private var userA = PartnerProfile(
        id = "user_alex",
        name = "Alex Carter",
        phone = "+1 (555) 234-5678",
        email = "alex.carter@coupleguard.app",
        avatarInitials = "AC",
        batteryPercent = 86,
        isCharging = false,
        isOnline = true,
        lastActiveMillis = now - 60_000,
        isLocationSharingEnabled = true,
        sharingDuration = SharingDuration.ALWAYS,
        sharingExpiryMillis = null,
        location = LocationData(
            latitude = 37.7749,
            longitude = -122.4194,
            address = "742 Montgomery St, Financial District, SF",
            accuracyMeters = 8f,
            speedKmh = 0f,
            motionActivity = MotionActivity.STATIONARY,
            lastUpdatedMillis = now - 90_000
        )
    )

    // Default Partner B: Taylor Reed
    private var userB = PartnerProfile(
        id = "user_taylor",
        name = "Taylor Reed",
        phone = "+1 (555) 876-5432",
        email = "taylor.reed@coupleguard.app",
        avatarInitials = "TR",
        batteryPercent = 74,
        isCharging = true,
        isOnline = true,
        lastActiveMillis = now - 180_000,
        isLocationSharingEnabled = true,
        sharingDuration = SharingDuration.FOUR_HOURS,
        sharingExpiryMillis = now + (3 * 3600_000),
        location = LocationData(
            latitude = 37.7892,
            longitude = -122.4014,
            address = "Pier 15, The Embarcadero, SF",
            accuracyMeters = 12f,
            speedKmh = 4.2f,
            motionActivity = MotionActivity.WALKING,
            lastUpdatedMillis = now - 120_000
        )
    )

    // Current active viewing persona ("user_alex" or "user_taylor")
    private val _activePersonaId = MutableStateFlow("user_alex")
    val activePersonaId: StateFlow<String> = _activePersonaId.asStateFlow()

    private val _coupleState = MutableStateFlow(
        CoupleState(
            coupleId = "couple_safe_7719",
            inviteCode = "CG-9284",
            status = PairingStatus.ACTIVE,
            pairedAtMillis = now - (14 * 86400_000L),
            currentUser = userA,
            partnerUser = userB
        )
    )
    val coupleState: StateFlow<CoupleState> = _coupleState.asStateFlow()

    private val _activeEmergencyAlert = MutableStateFlow<EmergencyAlert?>(null)
    val activeEmergencyAlert: StateFlow<EmergencyAlert?> = _activeEmergencyAlert.asStateFlow()

    private val _checkIns = MutableStateFlow<List<SafetyCheckIn>>(
        listOf(
            SafetyCheckIn(
                id = "chk_1",
                creatorId = "user_alex",
                creatorName = "Alex Carter",
                title = "Evening walk back from studio",
                scheduledTimeMillis = now + (25 * 60_000L),
                durationMinutes = 30,
                note = "Taking the well-lit path along Embarcadero",
                status = CheckInStatus.PENDING,
                confirmedAtMillis = null,
                autoAlertPartnerOnMissed = true
            ),
            SafetyCheckIn(
                id = "chk_2",
                creatorId = "user_taylor",
                creatorName = "Taylor Reed",
                title = "Commute via subway",
                scheduledTimeMillis = now - (120 * 60_000L),
                durationMinutes = 20,
                note = "Arrived safely at station",
                status = CheckInStatus.CONFIRMED_SAFE,
                confirmedAtMillis = now - (115 * 60_000L),
                autoAlertPartnerOnMissed = true
            )
        )
    )
    val checkIns: StateFlow<List<SafetyCheckIn>> = _checkIns.asStateFlow()

    private val _savedPlaces = MutableStateFlow<List<SavedPlace>>(
        listOf(
            SavedPlace(
                id = "pl_1",
                name = "Shared Apartment",
                category = PlaceCategory.HOME,
                address = "742 Montgomery St, SF",
                latitude = 37.7749,
                longitude = -122.4194,
                radiusMeters = 150,
                notifyOnArrival = true,
                notifyOnDeparture = true,
                isEnabled = true
            ),
            SavedPlace(
                id = "pl_2",
                name = "Design Studio Office",
                category = PlaceCategory.WORK,
                address = "100 California St, SF",
                latitude = 37.7935,
                longitude = -122.3995,
                radiusMeters = 200,
                notifyOnArrival = true,
                notifyOnDeparture = false,
                isEnabled = true
            ),
            SavedPlace(
                id = "pl_3",
                name = "Equinox Gym",
                category = PlaceCategory.GYM,
                address = "757 Market St, SF",
                latitude = 37.7871,
                longitude = -122.4048,
                radiusMeters = 100,
                notifyOnArrival = false,
                notifyOnDeparture = false,
                isEnabled = true
            )
        )
    )
    val savedPlaces: StateFlow<List<SavedPlace>> = _savedPlaces.asStateFlow()

    private val _timelineEvents = MutableStateFlow<List<TimelineEvent>>(
        listOf(
            TimelineEvent(
                id = "evt_1",
                timestampMillis = now - 90_000,
                type = TimelineEventType.LOCATION_SHARING,
                title = "Location Sharing Active",
                description = "Alex started sharing precise location (Mode: Until Disabled)",
                severity = EventSeverity.INFO,
                actorName = "Alex Carter"
            ),
            TimelineEvent(
                id = "evt_2",
                timestampMillis = now - (115 * 60_000L),
                type = TimelineEventType.CHECK_IN,
                title = "Safety Check-in Confirmed",
                description = "Taylor confirmed 'Commute via subway' as Safe",
                severity = EventSeverity.SUCCESS,
                actorName = "Taylor Reed"
            ),
            TimelineEvent(
                id = "evt_3",
                timestampMillis = now - (3 * 3600_000L),
                type = TimelineEventType.GEOFENCE,
                title = "Arrived at Safe Zone",
                description = "Taylor arrived at Design Studio Office",
                severity = EventSeverity.INFO,
                actorName = "Taylor Reed"
            ),
            TimelineEvent(
                id = "evt_4",
                timestampMillis = now - (14 * 86400_000L),
                type = TimelineEventType.PAIRING,
                title = "Pairing Confirmed",
                description = "Mutual consent established between Alex and Taylor with equal controls",
                severity = EventSeverity.SUCCESS,
                actorName = "System"
            )
        )
    )
    val timelineEvents: StateFlow<List<TimelineEvent>> = _timelineEvents.asStateFlow()

    private val _trustedContacts = MutableStateFlow<List<TrustedContact>>(
        listOf(
            TrustedContact("c_1", "Taylor Reed", "+1 (555) 876-5432", "Partner (Primary)", true),
            TrustedContact("c_2", "Sarah Carter", "+1 (555) 345-6789", "Sister", true),
            TrustedContact("c_3", "Marcus Vance", "+1 (555) 456-7890", "Close Friend", false)
        )
    )
    val trustedContacts: StateFlow<List<TrustedContact>> = _trustedContacts.asStateFlow()

    private val _privacyPreferences = MutableStateFlow(PrivacyPreferences())
    val privacyPreferences: StateFlow<PrivacyPreferences> = _privacyPreferences.asStateFlow()

    private val _notificationBanner = MutableStateFlow<String?>(null)
    val notificationBanner: StateFlow<String?> = _notificationBanner.asStateFlow()

    private val _isFakeCallActive = MutableStateFlow(false)
    val isFakeCallActive: StateFlow<Boolean> = _isFakeCallActive.asStateFlow()

    private val _isSirenActive = MutableStateFlow(false)
    val isSirenActive: StateFlow<Boolean> = _isSirenActive.asStateFlow()

    fun triggerFakeCall() {
        _isFakeCallActive.value = true
        _notificationBanner.value = "Incoming discreet safety call simulation triggered"
    }

    fun dismissFakeCall() {
        _isFakeCallActive.value = false
    }

    fun toggleSiren() {
        val next = !_isSirenActive.value
        _isSirenActive.value = next
        _notificationBanner.value = if (next) "Deterrent Siren & Strobe Activated!" else "Deterrent Siren Deactivated"
    }

    fun dismissBanner() {
        _notificationBanner.value = null
    }

    fun switchPersona(targetId: String) {
        if (targetId == _activePersonaId.value) return
        _activePersonaId.value = targetId
        syncCoupleState()
        _notificationBanner.value = "Switched active profile to ${getCurrentUser().name}"
    }

    private fun getCurrentUser(): PartnerProfile {
        return if (_activePersonaId.value == "user_alex") userA else userB
    }

    private fun getPartnerUser(): PartnerProfile {
        return if (_activePersonaId.value == "user_alex") userB else userA
    }

    private fun syncCoupleState() {
        val current = getCurrentUser()
        val partner = getPartnerUser()
        _coupleState.value = _coupleState.value.copy(
            currentUser = current,
            partnerUser = partner
        )
    }

    fun toggleLocationSharing(enable: Boolean, duration: SharingDuration) {
        val currTime = System.currentTimeMillis()
        val expiry = if (enable && duration.minutes != null) {
            currTime + (duration.minutes * 60_000L)
        } else null

        if (_activePersonaId.value == "user_alex") {
            userA = userA.copy(
                isLocationSharingEnabled = enable,
                sharingDuration = duration,
                sharingExpiryMillis = expiry,
                lastActiveMillis = currTime
            )
        } else {
            userB = userB.copy(
                isLocationSharingEnabled = enable,
                sharingDuration = duration,
                sharingExpiryMillis = expiry,
                lastActiveMillis = currTime
            )
        }
        syncCoupleState()

        val actor = getCurrentUser().name
        val desc = if (enable) {
            "$actor enabled location sharing (${duration.label})"
        } else {
            "$actor paused location sharing. Partner can no longer view live coordinates."
        }
        addTimelineEvent(
            type = TimelineEventType.LOCATION_SHARING,
            title = if (enable) "Location Sharing Enabled" else "Location Sharing Paused",
            description = desc,
            severity = if (enable) EventSeverity.INFO else EventSeverity.WARNING,
            actorName = actor
        )
        _notificationBanner.value = if (enable) "Live location sharing activated" else "Location sharing disabled"
    }

    fun triggerEmergencySos(customMessage: String) {
        val currTime = System.currentTimeMillis()
        val current = getCurrentUser()
        val alert = EmergencyAlert(
            id = "sos_${UUID.randomUUID().toString().take(8)}",
            senderId = current.id,
            senderName = current.name,
            triggeredAtMillis = currTime,
            resolvedAtMillis = null,
            status = SosStatus.ACTIVE,
            latitude = current.location?.latitude ?: 37.7749,
            longitude = current.location?.longitude ?: -122.4194,
            address = current.location?.address ?: "Current GPS coordinates",
            batteryPercent = current.batteryPercent,
            customMessage = customMessage.ifBlank { "Emergency! I need help immediately. Here is my current live location." },
            contactsNotifiedCount = _trustedContacts.value.count { it.isEmergencyContact }
        )
        _activeEmergencyAlert.value = alert

        addTimelineEvent(
            type = TimelineEventType.SOS_ALERT,
            title = "EMERGENCY SOS ACTIVATED",
            description = "${current.name} triggered SOS! Battery: ${current.batteryPercent}%, Location: ${alert.address}",
            severity = EventSeverity.CRITICAL,
            actorName = current.name
        )
        _notificationBanner.value = "CRITICAL: SOS Alert broadcast to partner & trusted contacts!"
    }

    fun resolveEmergencySos(note: String) {
        val currAlert = _activeEmergencyAlert.value ?: return
        val currTime = System.currentTimeMillis()
        val current = getCurrentUser()

        _activeEmergencyAlert.value = null

        addTimelineEvent(
            type = TimelineEventType.SOS_ALERT,
            title = "Emergency SOS Resolved",
            description = "${current.name} marked the emergency as safe. Note: ${note.ifBlank { "Situation under control." }}",
            severity = EventSeverity.SUCCESS,
            actorName = current.name
        )
        _notificationBanner.value = "Emergency SOS cancelled. Partner notified that you are safe."
    }

    fun scheduleCheckIn(title: String, durationMinutes: Int, note: String) {
        val currTime = System.currentTimeMillis()
        val current = getCurrentUser()
        val newCheckIn = SafetyCheckIn(
            id = "chk_${UUID.randomUUID().toString().take(8)}",
            creatorId = current.id,
            creatorName = current.name,
            title = title,
            scheduledTimeMillis = currTime + (durationMinutes * 60_000L),
            durationMinutes = durationMinutes,
            note = note,
            status = CheckInStatus.PENDING,
            confirmedAtMillis = null,
            autoAlertPartnerOnMissed = true
        )
        _checkIns.update { listOf(newCheckIn) + it }

        addTimelineEvent(
            type = TimelineEventType.CHECK_IN,
            title = "Check-in Scheduled",
            description = "${current.name} set a check-in for '${title}' in $durationMinutes mins",
            severity = EventSeverity.INFO,
            actorName = current.name
        )
        _notificationBanner.value = "Safety check-in active for next $durationMinutes mins"
    }

    fun confirmCheckInSafe(checkInId: String) {
        val currTime = System.currentTimeMillis()
        var updatedTitle = ""
        _checkIns.update { list ->
            list.map { item ->
                if (item.id == checkInId) {
                    updatedTitle = item.title
                    item.copy(status = CheckInStatus.CONFIRMED_SAFE, confirmedAtMillis = currTime)
                } else item
            }
        }
        val current = getCurrentUser()
        addTimelineEvent(
            type = TimelineEventType.CHECK_IN,
            title = "Check-in Confirmed Safe",
            description = "${current.name} confirmed they are safe for '$updatedTitle'",
            severity = EventSeverity.SUCCESS,
            actorName = current.name
        )
        _notificationBanner.value = "Check-in confirmed safe! Partner notified."
    }

    fun simulateMissedCheckIn(checkInId: String) {
        var updatedTitle = ""
        _checkIns.update { list ->
            list.map { item ->
                if (item.id == checkInId) {
                    updatedTitle = item.title
                    item.copy(status = CheckInStatus.MISSED_UNCONFIRMED)
                } else item
            }
        }
        val current = getCurrentUser()
        addTimelineEvent(
            type = TimelineEventType.CHECK_IN,
            title = "Check-in Missed (Unconfirmed)",
            description = "Check-in '$updatedTitle' was not acknowledged before deadline. Partner alerted with last known location.",
            severity = EventSeverity.WARNING,
            actorName = current.name
        )
        _notificationBanner.value = "Notice: Check-in unconfirmed. Partner notified."
    }

    fun addSavedPlace(
        name: String,
        category: PlaceCategory,
        address: String,
        radiusMeters: Int,
        notifyArrival: Boolean,
        notifyDeparture: Boolean
    ) {
        val newPlace = SavedPlace(
            id = "pl_${UUID.randomUUID().toString().take(8)}",
            name = name,
            category = category,
            address = address,
            latitude = 37.7850 + (Math.random() - 0.5) * 0.02,
            longitude = -122.4100 + (Math.random() - 0.5) * 0.02,
            radiusMeters = radiusMeters,
            notifyOnArrival = notifyArrival,
            notifyOnDeparture = notifyDeparture,
            isEnabled = true
        )
        _savedPlaces.update { it + newPlace }
        addTimelineEvent(
            type = TimelineEventType.GEOFENCE,
            title = "Saved Place Added",
            description = "Added '$name' (${category.label}) with ${radiusMeters}m radius",
            severity = EventSeverity.INFO,
            actorName = getCurrentUser().name
        )
        _notificationBanner.value = "Saved place added"
    }

    fun toggleSavedPlace(placeId: String) {
        _savedPlaces.update { list ->
            list.map {
                if (it.id == placeId) it.copy(isEnabled = !it.isEnabled) else it
            }
        }
    }

    fun deleteSavedPlace(placeId: String) {
        _savedPlaces.update { it.filterNot { item -> item.id == placeId } }
    }

    fun pairWithCode(inputCode: String): Boolean {
        if (inputCode.trim().length >= 4) {
            _coupleState.update {
                it.copy(
                    status = PairingStatus.ACTIVE,
                    pairedAtMillis = System.currentTimeMillis()
                )
            }
            addTimelineEvent(
                type = TimelineEventType.PAIRING,
                title = "Pairing Successful",
                description = "Connected with partner using code ${inputCode.trim().uppercase()}",
                severity = EventSeverity.SUCCESS,
                actorName = "Self Safety"
            )
            _notificationBanner.value = "Partner connected successfully!"
            return true
        }
        return false
    }

    fun generateNewInviteCode(): String {
        val newCode = "CG-${(1000..9999).random()}"
        _coupleState.update { it.copy(inviteCode = newCode) }
        _notificationBanner.value = "Generated new pairing code: $newCode"
        return newCode
    }

    fun disconnectPairing() {
        val currTime = System.currentTimeMillis()
        val current = getCurrentUser()
        _coupleState.update {
            it.copy(status = PairingStatus.DISCONNECTED)
        }
        // Immediately halt location sharing
        userA = userA.copy(isLocationSharingEnabled = false)
        userB = userB.copy(isLocationSharingEnabled = false)
        syncCoupleState()

        addTimelineEvent(
            type = TimelineEventType.PAIRING,
            title = "Pairing Disconnected",
            description = "${current.name} revoked pairing. All mutual location sharing and alerts terminated immediately.",
            severity = EventSeverity.WARNING,
            actorName = current.name
        )
        _notificationBanner.value = "Pairing disconnected. Location sharing revoked."
    }

    fun reconnectDemoPairing() {
        _coupleState.update {
            it.copy(
                status = PairingStatus.ACTIVE,
                pairedAtMillis = System.currentTimeMillis()
            )
        }
        userA = userA.copy(isLocationSharingEnabled = true)
        userB = userB.copy(isLocationSharingEnabled = true)
        syncCoupleState()
        _notificationBanner.value = "Pairing re-established with mutual consent"
    }

    fun addTrustedContact(name: String, phone: String, relationship: String) {
        val newContact = TrustedContact(
            id = "c_${UUID.randomUUID().toString().take(6)}",
            name = name,
            phone = phone,
            relationship = relationship,
            isEmergencyContact = true
        )
        _trustedContacts.update { it + newContact }
        _notificationBanner.value = "Trusted contact '$name' added"
    }

    fun removeTrustedContact(contactId: String) {
        _trustedContacts.update { it.filterNot { c -> c.id == contactId } }
    }

    fun updatePrivacyPreferences(newPrefs: PrivacyPreferences) {
        _privacyPreferences.value = newPrefs
        _notificationBanner.value = "Privacy preferences saved"
    }

    fun clearTimelineEvents() {
        _timelineEvents.value = emptyList()
        _notificationBanner.value = "Timeline history cleared"
    }

    private fun addTimelineEvent(
        type: TimelineEventType,
        title: String,
        description: String,
        severity: EventSeverity,
        actorName: String
    ) {
        val event = TimelineEvent(
            id = "evt_${UUID.randomUUID().toString().take(8)}",
            timestampMillis = System.currentTimeMillis(),
            type = type,
            title = title,
            description = description,
            severity = severity,
            actorName = actorName
        )
        _timelineEvents.update { listOf(event) + it }
    }

    fun exportSafetyDataJson(): String {
        val current = getCurrentUser()
        val partner = getPartnerUser()
        return """
        {
          "application": "Self Safety",
          "exportTimestamp": "${System.currentTimeMillis()}",
          "user": {
            "id": "${current.id}",
            "name": "${current.name}",
            "phone": "${current.phone}"
          },
          "partner": {
            "id": "${partner.id}",
            "name": "${partner.name}",
            "phone": "${partner.phone}"
          },
          "pairingStatus": "${_coupleState.value.status}",
          "timelineEventCount": ${_timelineEvents.value.size},
          "savedPlacesCount": ${_savedPlaces.value.size},
          "trustedContactsCount": ${_trustedContacts.value.size},
          "privacyStatement": "Voluntary mutual consent architecture. Zero unauthorized tracking."
        }
        """.trimIndent()
    }
}
