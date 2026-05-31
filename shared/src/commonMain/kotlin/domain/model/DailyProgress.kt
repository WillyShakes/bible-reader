package domain.model

import domain.model.enums.DayState
import kotlinx.datetime.Instant

/**
 * Records a user's state for one day in their plan.
 * Conflict rule: COMPLETE always overwrites SKIPPED or NOT_YET — never revert a completed day.
 *
 * @param deviceId Used for conflict resolution during multi-device sync.
 */
data class DailyProgress(
    val progressId: String,
    val uid: String,
    val userPlanId: String,
    val dayIndex: Int,
    val state: DayState,
    val completedAt: Instant?,
    val deviceId: String,
    val updatedAt: Instant,
)
