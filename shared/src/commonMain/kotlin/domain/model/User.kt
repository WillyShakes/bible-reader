package domain.model

import domain.model.enums.AuthProvider
import domain.model.enums.Language
import kotlinx.datetime.Instant

/** Authenticated or guest user. Guest users have provider = GUEST and null email/displayName. */
data class User(
    val uid: String,
    val provider: AuthProvider,
    val email: String?,
    val displayName: String?,
    val createdAt: Instant,
    val lastSeenAt: Instant,
    val preferredLanguage: Language,
)
