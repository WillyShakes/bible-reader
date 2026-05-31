package domain.repository

import domain.model.DailyAssignment
import domain.model.UserPlan
import domain.model.enums.CatchUpStrategy
import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/** Parameters for creating or recalculating a reading plan schedule. */
data class RecalcParams(
    val userPlanId: String,
    val planType: PlanType,
    val readingOrder: ReadingOrder,
    val completedDayIndices: List<Int>,
    val skippedDayIndices: List<Int>,
    val today: LocalDate,
    val catchUpStrategy: CatchUpStrategy,
)

/**
 * Manages the user's reading plan state.
 * Local SQLDelight is the source of truth; Firestore is the sync target.
 */
interface UserPlanRepository {
    /** Emits the current ACTIVE plan, or null if none exists. */
    fun observeActivePlan(): Flow<UserPlan?>

    /** Returns all plans (ACTIVE, PAUSED, COMPLETED) for the user. */
    suspend fun getAllPlans(): Result<List<UserPlan>>

    /** Saves a day as COMPLETE. COMPLETE state can never be reverted. */
    suspend fun saveDayComplete(userPlanId: String, dayIndex: Int): Result<Unit>

    /**
     * Recalculates the daily assignment schedule based on the chosen catch-up strategy.
     * Pure computation — does not mutate historical DailyProgress records.
     */
    suspend fun recalculateSchedule(params: RecalcParams): Result<List<DailyAssignment>>

    /** Persists a new or updated UserPlan. */
    suspend fun savePlan(plan: UserPlan): Result<Unit>
}
