package feature.catchup

import domain.model.DailyAssignment
import domain.model.enums.CatchUpStrategy

/** Single source of truth for the catch-up / grace mechanic screen. */
data class CatchUpUiState(
    val availableStrategies: List<CatchUpStrategy> = emptyList(),
    val selectedStrategy: CatchUpStrategy? = null,
    val schedulePreview: List<DailyAssignment> = emptyList(),
    val isLoading: Boolean = false,
    val showCatchUp: Boolean = true,
    val error: String? = null,
)
