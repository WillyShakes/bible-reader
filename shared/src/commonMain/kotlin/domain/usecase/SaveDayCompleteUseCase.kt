package domain.usecase

import domain.repository.UserPlanRepository

/**
 * Marks a plan day as COMPLETE.
 * COMPLETE state can never be reverted — this is enforced at the repository layer.
 */
class SaveDayCompleteUseCase(private val userPlanRepository: UserPlanRepository) {
    suspend operator fun invoke(userPlanId: String, dayIndex: Int): Result<Unit> =
        userPlanRepository.saveDayComplete(userPlanId, dayIndex)
}
