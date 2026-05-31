package app.rema.bible

import androidx.compose.ui.graphics.ImageBitmap

actual fun shareText(text: String) {
    // TODO (Feature f — AC-F-5): Intent.ACTION_SEND with TYPE_TEXT_PLAIN
}

actual fun shareImageCard(bitmap: ImageBitmap) {
    // TODO (Feature f — AC-F-6): write bitmap to cache, share via Intent.ACTION_SEND
}
