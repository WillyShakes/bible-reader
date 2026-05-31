package feature.notifications

import kotlinx.datetime.LocalTime

/** All user actions on the notification preferences screen. */
sealed interface NotificationsIntent {
    data class SetDailyReminderEnabled(val enabled: Boolean) : NotificationsIntent
    data class SetDailyReminderTime(val time: LocalTime) : NotificationsIntent
    data class SetReEngagementEnabled(val enabled: Boolean) : NotificationsIntent
}
