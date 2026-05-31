package domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

/**
 * User's notification settings. Part of the sync payload.
 * Hard cap: one notification per calendar day regardless of these settings (RULES.md).
 */
data class NotificationPreferences(
    val uid: String,
    val dailyReminderEnabled: Boolean,
    val dailyReminderTime: LocalTime,
    val reEngagementEnabled: Boolean,
    val updatedAt: Instant,
)
