package com.example

import com.example.data.CoupleGuardRepository
import com.example.data.SupabaseReference
import com.example.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CoupleGuardRepositoryTest {

    private lateinit var repository: CoupleGuardRepository

    @Before
    fun setUp() {
        repository = CoupleGuardRepository()
    }

    @Test
    fun testInitialPairingActive() {
        val state = repository.coupleState.value
        assertEquals(PairingStatus.ACTIVE, state.status)
        assertEquals("Alex Carter", state.currentUser.name)
        assertEquals("Taylor Reed", state.partnerUser.name)
        assertTrue(state.currentUser.isLocationSharingEnabled)
    }

    @Test
    fun testSwitchPersona() {
        repository.switchPersona("user_taylor")
        val state = repository.coupleState.value
        assertEquals("Taylor Reed", state.currentUser.name)
        assertEquals("Alex Carter", state.partnerUser.name)

        repository.switchPersona("user_alex")
        val resetState = repository.coupleState.value
        assertEquals("Alex Carter", resetState.currentUser.name)
    }

    @Test
    fun testToggleLocationSharing() {
        repository.toggleLocationSharing(false, SharingDuration.ALWAYS)
        assertFalse(repository.coupleState.value.currentUser.isLocationSharingEnabled)

        repository.toggleLocationSharing(true, SharingDuration.ONE_HOUR)
        assertTrue(repository.coupleState.value.currentUser.isLocationSharingEnabled)
        assertNotNull(repository.coupleState.value.currentUser.sharingExpiryMillis)
    }

    @Test
    fun testEmergencySosTriggerAndResolve() {
        repository.triggerEmergencySos("Testing SOS broadcast")
        val alert = repository.activeEmergencyAlert.value
        assertNotNull(alert)
        assertEquals("Alex Carter", alert?.senderName)
        assertEquals(SosStatus.ACTIVE, alert?.status)

        // Resolve
        repository.resolveEmergencySos("All safe, false alarm")
        assertNull(repository.activeEmergencyAlert.value)
    }

    @Test
    fun testScheduleAndConfirmCheckIn() {
        val initialCount = repository.checkIns.value.size
        repository.scheduleCheckIn("Walk home", 25, "Route along 3rd St")
        assertEquals(initialCount + 1, repository.checkIns.value.size)

        val newCheckIn = repository.checkIns.value.first()
        assertEquals("Walk home", newCheckIn.title)
        assertEquals(CheckInStatus.PENDING, newCheckIn.status)

        repository.confirmCheckInSafe(newCheckIn.id)
        val updated = repository.checkIns.value.first { it.id == newCheckIn.id }
        assertEquals(CheckInStatus.CONFIRMED_SAFE, updated.status)
        assertNotNull(updated.confirmedAtMillis)
    }

    @Test
    fun testSimulateMissedCheckIn() {
        repository.scheduleCheckIn("Night run", 10, "Park loop")
        val checkIn = repository.checkIns.value.first()

        repository.simulateMissedCheckIn(checkIn.id)
        val updated = repository.checkIns.value.first { it.id == checkIn.id }
        assertEquals(CheckInStatus.MISSED_UNCONFIRMED, updated.status)
    }

    @Test
    fun testDisconnectPairingRevokesConsentImmediately() {
        repository.disconnectPairing()
        val state = repository.coupleState.value
        assertEquals(PairingStatus.DISCONNECTED, state.status)
        // Ensure location sharing is immediately revoked for privacy
        assertFalse(state.currentUser.isLocationSharingEnabled)
        assertFalse(state.partnerUser.isLocationSharingEnabled)
    }

    @Test
    fun testSavedPlacesOperations() {
        val initialCount = repository.savedPlaces.value.size
        repository.addSavedPlace("Library", PlaceCategory.CAMPUS, "100 Main St", 200, true, false)
        assertEquals(initialCount + 1, repository.savedPlaces.value.size)

        val newPlace = repository.savedPlaces.value.last()
        repository.toggleSavedPlace(newPlace.id)
        val toggled = repository.savedPlaces.value.first { it.id == newPlace.id }
        assertFalse(toggled.isEnabled)

        repository.deleteSavedPlace(newPlace.id)
        assertEquals(initialCount, repository.savedPlaces.value.size)
    }

    @Test
    fun testExportSafetyDataJson() {
        val json = repository.exportSafetyDataJson()
        assertTrue(json.contains("Self Safety"))
        assertTrue(json.contains("Alex Carter"))
        assertTrue(json.contains("Taylor Reed"))
    }

    @Test
    fun testFakeCallAndSiren() {
        assertFalse(repository.isFakeCallActive.value)
        repository.triggerFakeCall()
        assertTrue(repository.isFakeCallActive.value)
        repository.dismissFakeCall()
        assertFalse(repository.isFakeCallActive.value)

        assertFalse(repository.isSirenActive.value)
        repository.toggleSiren()
        assertTrue(repository.isSirenActive.value)
        repository.toggleSiren()
        assertFalse(repository.isSirenActive.value)
    }

    @Test
    fun testSupabaseSchemaContainsRlsAndTables() {
        assertTrue(SupabaseReference.SQL_SCHEMA_AND_RLS.contains("create table if not exists public.locations"))
        assertTrue(SupabaseReference.SQL_SCHEMA_AND_RLS.contains("create table if not exists public.emergency_alerts"))
        assertTrue(SupabaseReference.SQL_SCHEMA_AND_RLS.contains("enable row level security"))
        assertTrue(SupabaseReference.WINDOWS_SETUP_GUIDE.contains("npx expo start"))
    }
}
