package lk.voltgo.voltgo.ui.viewmodel.auth

/**
 * ------------------------------------------------------------
 * File: HomeViewModel.kt
 * Author: Panchali Samarasinghe
 * Date: 2025-10-16
 *
 * Description:
 * ViewModel for the VoltGo Home screen.
 * - Handles logout and account deactivation
 * - Exposes reservation stats needed by Home:
 *     • pendingCount (any status == Pending)
 *     • approvedFutureCount (status == Confirmed AND start/reservationDate is in the future)
 * ------------------------------------------------------------
 */

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.voltgo.voltgo.auth.AuthManager
import lk.voltgo.voltgo.data.remote.types.StatusType
import lk.voltgo.voltgo.data.repository.ReservationRepository
import lk.voltgo.voltgo.data.repository.UserRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

data class HomeStatsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pendingCount: Int = 0,
    val approvedFutureCount: Int = 0,
    val lastUpdatedAt: Instant? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(HomeStatsState(isLoading = false))
    val stats: StateFlow<HomeStatsState> = _stats

    /** Logs out the current user by clearing token from AuthManager and DataStore */
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authManager.logout()
            onLoggedOut() // navigate to login screen
        }
    }

    fun deactivateAndLogout(
        onLoggedOut: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.deactivateMe()
                if (result.isSuccess) {
                    authManager.logout()
                    onLoggedOut()
                } else {
                    val message = result.exceptionOrNull()?.message ?: "Failed to deactivate"
                    onError(message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unexpected error during deactivation")
            }
        }
    }

    // --- Stats loading ---

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadHomeStats() {
        _stats.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val nic = authManager.getCurrentNIC()
                if (nic.isNullOrBlank()) {
                    _stats.update { it.copy(isLoading = false, error = "Invalid Owner NIC") }
                    return@launch
                }

                // Optional: sync latest before observing
                runCatching { reservationRepository.syncMyReservations(nic) }

                reservationRepository.observeMyReservations(nic).collectLatest { entities ->
                    val now = Instant.now()

                    val pendingCount = entities.count { it.status == StatusType.Pending }

                    val approvedFutureCount = entities.count { entity ->
                        entity.status == StatusType.Confirmed && isFuture(entity, now)
                    }

                    _stats.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            pendingCount = pendingCount,
                            approvedFutureCount = approvedFutureCount,
                            lastUpdatedAt = Instant.now()
                        )
                    }
                }
            } catch (e: Exception) {
                _stats.update { it.copy(isLoading = false, error = e.message ?: "Failed to load stats") }
            }
        }
    }

    /**
     * Determines if a reservation is in the future.
     * Tries startTime, then reservationDate as fallback.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isFuture(entity: lk.voltgo.voltgo.data.local.entities.ReservationEntity, now: Instant): Boolean {
        // Prefer startTime if present, else reservationDate
        val candidate = entity.reservationDate ?: entity.reservationDate ?: return false

        // Try Instant.parse first (ISO), otherwise interpret as local date-time in system zone.
        return parseToInstant(candidate)?.isAfter(now) ?: false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseToInstant(value: String): Instant? {
        return try {
            // If string is ISO-8601 with zone/offset (e.g., 2025-10-26T10:45:18.279Z)
            Instant.parse(value)
        } catch (_: Throwable) {
            try {
                // If it's a local date-time (no zone), treat as system-zone then convert
                val ldt = LocalDateTime.parse(value)
                val zoned = ZonedDateTime.of(ldt, ZoneId.systemDefault())
                zoned.toInstant()
            } catch (_: Throwable) {
                try {
                    // Some backends use UTC without 'Z'; assume UTC
                    val ldt = LocalDateTime.parse(value)
                    ldt.toInstant(ZoneOffset.UTC)
                } catch (_: Throwable) {
                    null
                }
            }
        }
    }
}