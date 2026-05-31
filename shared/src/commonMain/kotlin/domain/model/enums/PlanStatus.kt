package domain.model.enums

/** Lifecycle status of a UserPlan. Only one ACTIVE plan may exist at a time. */
enum class PlanStatus { ACTIVE, PAUSED, COMPLETED, ABANDONED }
