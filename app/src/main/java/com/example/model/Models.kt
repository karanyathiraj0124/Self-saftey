package com.example.model

enum class SharingDuration(val label: String, val minutes: Long?) {
    ONE_HOUR("1 Hour", 60),
    FOUR_HOURS("4 Hours", 240),
    EIGHT_HOURS("8 Hours", 480),
    ALWAYS("Until Disabled", null)
}

enum class MotionActivity(val label: String) {
    STATIONARY("Stationary"),
    WALKING("Walking"),
    DRIVING("In Transit")
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val accuracyMeters: Float,
    val speedKmh: Float,
    val motionActivity: MotionActivity,
    val lastUpdatedMillis: Long
)

data class PartnerProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val avatarInitials: String,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val isOnline: Boolean,
    val lastActiveMillis: Long,
    val isLocationSharingEnabled: Boolean,
    val sharingDuration: SharingDuration,
    val sharingExpiryMillis: Long?,
    val location: LocationData?
)

enum class PairingStatus {
    ACTIVE,
    PENDING_INVITE,
    DISCONNECTED
}

data class CoupleState(
    val coupleId: String,
    val inviteCode: String,
    val status: PairingStatus,
    val pairedAtMillis: Long,
    val currentUser: PartnerProfile,
    val partnerUser: PartnerProfile
)

enum class SosStatus {
    ACTIVE,
    RESOLVED
}

data class EmergencyAlert(
    val id: String,
    val senderId: String,
    val senderName: String,
    val triggeredAtMillis: Long,
    val resolvedAtMillis: Long?,
    val status: SosStatus,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val batteryPercent: Int,
    val customMessage: String,
    val contactsNotifiedCount: Int
)

enum class CheckInStatus {
    PENDING,
    CONFIRMED_SAFE,
    MISSED_UNCONFIRMED
}

data class SafetyCheckIn(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val title: String,
    val scheduledTimeMillis: Long,
    val durationMinutes: Int,
    val note: String,
    val status: CheckInStatus,
    val confirmedAtMillis: Long?,
    val autoAlertPartnerOnMissed: Boolean
)

enum class PlaceCategory(val label: String) {
    HOME("Home"),
    WORK("Work / Office"),
    CAMPUS("Campus / School"),
    GYM("Gym"),
    OTHER("Custom Safe Zone")
}

data class SavedPlace(
    val id: String,
    val name: String,
    val category: PlaceCategory,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val notifyOnArrival: Boolean,
    val notifyOnDeparture: Boolean,
    val isEnabled: Boolean
)

enum class TimelineEventType(val label: String) {
    SOS_ALERT("Emergency SOS"),
    CHECK_IN("Safety Check-in"),
    LOCATION_SHARING("Location Sharing"),
    GEOFENCE("Saved Destination"),
    PAIRING("Pairing & Consent")
}

enum class EventSeverity {
    INFO,
    SUCCESS,
    WARNING,
    CRITICAL
}

data class TimelineEvent(
    val id: String,
    val timestampMillis: Long,
    val type: TimelineEventType,
    val title: String,
    val description: String,
    val severity: EventSeverity,
    val actorName: String
)

data class TrustedContact(
    val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val isEmergencyContact: Boolean
)

data class PrivacyPreferences(
    val shareBatteryLevel: Boolean = true,
    val sharePreciseLocation: Boolean = true,
    val retentionPeriodDays: Int = 30,
    val requireBiometricForSosDismissal: Boolean = false,
    val emergencyServiceNumber: String = "911"
)

enum class NavigationTab(val title: String) {
    DASHBOARD("Safety"),
    MAP("Live Map"),
    CHECKINS("Check-Ins"),
    TIMELINE("Timeline"),
    PRIVACY("Privacy")
}
