package feature.plan

import domain.model.enums.PlanType
import domain.model.enums.ReadingOrder
import kotlinx.datetime.LocalDate

/** All user actions on the reading plan screens. */
sealed interface PlanIntent {
    data object MarkTodayComplete : PlanIntent
    data class SelectPlan(val planType: PlanType, val readingOrder: ReadingOrder, val startDate: LocalDate) : PlanIntent
    data object OpenProgress : PlanIntent
    data object OpenCatchUp : PlanIntent
}
