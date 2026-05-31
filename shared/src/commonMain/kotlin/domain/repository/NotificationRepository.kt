package domain.repository

import domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Manages notification preferences and local notification scheduling.
 * All notifications in v1 are local — no server push infrastructure.
 * Hard cap: one notification per calendar day (RULES.md).
 */
interface NotificationRepository {
    /** Emits the user's current notification preferences. */
    fun observePreferences(uid: String): Flow<NotificationPreferences?>

    /** Saves updated notification preferences and reschedules local notifications accordingly. */
    suspend fun savePreferences(preferences: NotificationPreferences): Result<Unit>
}
