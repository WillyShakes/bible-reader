package domain.model.enums

/** State of a single day in a reading plan. COMPLETE always wins in sync conflicts. */
enum class DayState { COMPLETE, SKIPPED, NOT_YET }
