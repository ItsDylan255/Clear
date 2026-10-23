package com.example.clear.navigation

/**
 * Routen innerhalb des Bereichs Mein Anker. ID -1 bedeutet: neuer Eintrag.
 * {emotion} ist die id einer Emotion ("anxiety" ...) oder "unknown" fuer Allgemein.
 */
object AnkerRoutes {
    const val START = "anker_start"
    const val EMERGENCY = "anker_notfallkarte"
    const val EVENING = "anker_abend"
    const val EVENING_EDIT = "anker_abend_edit/{entryId}"
    const val EVENING_REMINDER = "anker_abend_erinnerung"
    const val IDEAS = "anker_ideen"
    const val PICK = "anker_wahl/{type}"
    const val QUOTES = "anker_quotes/{emotion}"
    const val QUOTE_EDITOR = "anker_quote_editor/{quoteId}/{emotion}"
    const val LETTERS = "anker_letters/{emotion}"
    const val LETTER_READER = "anker_letter/{letterId}"
    const val LETTER_EDITOR = "anker_letter_editor/{letterId}/{emotion}"
    const val IMAGES = "anker_images/{emotion}"
    const val IMAGE_VIEWER = "anker_image/{imageId}"
    const val MUSIC = "anker_music/{emotion}"

    fun eveningEdit(id: Long) = "anker_abend_edit/$id"
    fun pick(type: String) = "anker_wahl/$type"
    fun quotes(emotion: String) = "anker_quotes/$emotion"
    fun letters(emotion: String) = "anker_letters/$emotion"
    fun images(emotion: String) = "anker_images/$emotion"
    fun music(emotion: String) = "anker_music/$emotion"
    fun quoteEditor(id: Long, emotion: String) = "anker_quote_editor/$id/$emotion"
    fun letterReader(id: Long) = "anker_letter/$id"
    fun letterEditor(id: Long, emotion: String) = "anker_letter_editor/$id/$emotion"
    fun imageViewer(id: Long) = "anker_image/$id"
}
