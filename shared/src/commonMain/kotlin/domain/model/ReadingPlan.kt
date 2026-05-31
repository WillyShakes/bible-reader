package domain.model

import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder

/**
 * Static plan asset shared across all users. Not stored per-user.
 * The per-user instance is [UserPlan].
 */
data class ReadingPlan(
    val planId: String,
    val planType: PlanType,
    val readingOrder: ReadingOrder,
    val totalDays: Int,
    val assignments: List<DailyAssignment>,
)
