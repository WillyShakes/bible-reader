package domain.model

/**
 * The reading assignment for a single day in a plan.
 * Deterministic from plan assets — never stored per-user.
 *
 * @param dayIndex 1-based position in the plan. See SPEC.md §7 Glossary: DayIndex.
 */
data class DailyAssignment(
    val dayIndex: Int,
    val passages: List<Passage>,
)
