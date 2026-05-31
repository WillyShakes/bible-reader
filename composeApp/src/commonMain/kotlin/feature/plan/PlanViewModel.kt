package feature.plan

import androidx.lifecycle.ViewModel
import domain.usecase.GetActivePlanUseCase
import domain.usecase.RecalculateScheduleUseCase
import domain.usecase.SaveDayCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the reading plan home and progress screens. */
class PlanViewModel(
    private val getActivePlanUseCase: GetActivePlanUseCase,
    private val saveDayCompleteUseCase: SaveDayCompleteUseCase,
    private val recalculateScheduleUseCase: RecalculateScheduleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    fun onIntent(intent: PlanIntent) {
        when (intent) {
            is PlanIntent.MarkTodayComplete -> TODO("Feature b")
            is PlanIntent.SelectPlan -> TODO("Feature b")
            is PlanIntent.OpenProgress -> TODO("Feature b")
            is PlanIntent.OpenCatchUp -> TODO("Feature c")
        }
    }
}
