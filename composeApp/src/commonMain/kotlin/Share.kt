package app.rema.bible

import androidx.compose.ui.graphics.ImageBitmap

/** Shares plain text via the platform native share sheet. */
expect fun shareText(text: String)

/** Shares a verse image card bitmap via the platform native share sheet. */
expect fun shareImageCard(bitmap: ImageBitmap)
