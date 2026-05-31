package domain.usecase

import domain.model.UserPlan
import domain.repository.UserPlanRepository
import kotlinx.coroutines.flow.Flow

/** Returns the user's active reading plan as a reactive stream, or null if none is active. */
class GetActivePlanUseCase(private val userPlanRepository: UserPlanRepository) {
    operator fun invoke(): Flow<UserPlan?> = userPlanRepository.observeActivePlan()
}
