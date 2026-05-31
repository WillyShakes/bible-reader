package domain.model

import domain.model.enums.PlanStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * The per-user instance of a reading plan, including their progress position and status.
 * Part of the sync payload.
 *
 * @param entryPosition Set when user joins mid-plan (AC-B-10). Null for normal plan start.
 */
data class UserPlan(
    val userPlanId: String,
    val uid: String,
    val planId: String,
    val status: PlanStatus,
    val startDate: LocalDate,
    val entryPosition: Int?,
    val currentDayIndex: Int,
    val projectedEndDate: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
)
