package feature.catchup

import domain.model.enums.CatchUpStrategy

/** All user actions on the catch-up screen. */
sealed interface CatchUpIntent {
    data class SelectStrategy(val strategy: CatchUpStrategy) : CatchUpIntent
    data object Confirm : CatchUpIntent
    data object Dismiss : CatchUpIntent
}
