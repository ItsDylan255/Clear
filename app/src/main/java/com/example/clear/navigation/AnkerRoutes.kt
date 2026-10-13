package com.example.clear.navigation

/** Routen innerhalb des Bereichs Mein Anker. ID -1 bedeutet: neuer Eintrag. */
object AnkerRoutes {
    const val START = "anker_start"
    const val QUOTES = "anker_quotes"
    const val QUOTE_EDITOR = "anker_quote_editor/{quoteId}"
    const val LETTERS = "anker_letters"
    const val LETTER_READER = "anker_letter/{letterId}"
    const val LETTER_EDITOR = "anker_letter_editor/{letterId}"
    const val IMAGES = "anker_images"
    const val IMAGE_VIEWER = "anker_image/{imageId}"
    const val MUSIC = "anker_music"

    fun quoteEditor(id: Long = -1L) = "anker_quote_editor/$id"
    fun letterReader(id: Long) = "anker_letter/$id"
    fun letterEditor(id: Long = -1L) = "anker_letter_editor/$id"
    fun imageViewer(id: Long) = "anker_image/$id"
}
