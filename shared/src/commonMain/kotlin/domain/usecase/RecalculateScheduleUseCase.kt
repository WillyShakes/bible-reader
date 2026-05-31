package domain.usecase

import domain.model.DailyAssignment
import domain.repository.RecalcParams
import domain.repository.UserPlanRepository

/**
 * Recalculates the reading schedule based on a catch-up strategy.
 *
 * This is a pure, stateless operation: inputs in, new DailyAssignment[] out.
 * It never mutates historical DailyProgress records.
 *
 * Compress distribution rule: even division across the window; remainder chapters
 * go to the final day of the window. Example: 15 chapters over 7 days = [2,2,2,2,2,2,3].
 *
 * 5-chapter daily cap: callers must filter out Compress options that exceed this cap
 * before presenting them in the UI (AC-C-5).
 */
class RecalculateScheduleUseCase(private val userPlanRepository: UserPlanRepository) {
    suspend operator fun invoke(params: RecalcParams): Result<List<DailyAssignment>> =
        userPlanRepository.recalculateSchedule(params)
}
