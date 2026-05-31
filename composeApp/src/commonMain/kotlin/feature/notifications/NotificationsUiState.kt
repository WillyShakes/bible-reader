package feature.notifications

import kotlinx.datetime.LocalTime

/** Single source of truth for the notification preferences screen. */
data class NotificationsUiState(
    val dailyReminderEnabled: Boolean = false,
    val dailyReminderTime: LocalTime = LocalTime(7, 0),
    val reEngagementEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
