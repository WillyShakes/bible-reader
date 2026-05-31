package domain.model.enums

/**
 * User-selected strategy for recovering from being behind on a reading plan.
 * Passed into recalculateSchedule() as a pure input — no side effects.
 */
sealed interface CatchUpStrategy {
    /** Redistribute missed chapters across a fixed future window. Use Int.MAX_VALUE for full plan. */
    data class Compress(val windowDays: Int) : CatchUpStrategy

    /** Mark missed days as SKIPPED; resume from today's original passage; end date unchanged. */
    data object SkipAndContinue : CatchUpStrategy

    /** Extend the plan end date so today is back on track; no extra daily load. */
    data object Recalculate : CatchUpStrategy
}
