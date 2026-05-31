package feature.plan

import domain.model.DailyAssignment
import domain.model.UserPlan

/** Single source of truth for the reading plan home screen. */
data class PlanUiState(
    val activePlan: UserPlan? = null,
    val todayAssignment: DailyAssignment? = null,
    val completedDayCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)
