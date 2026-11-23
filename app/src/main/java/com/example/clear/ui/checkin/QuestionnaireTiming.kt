package com.example.clear.ui.checkin

import com.example.clear.AppLanguage
import com.example.clear.tr
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Zwischen zwei Frageboegen (PHQ-9, WHO-5, WSAS) liegen mindestens zwei Wochen. */
const val QUESTIONNAIRE_INTERVAL_DAYS = 14L

private fun dateOf(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

/** true, wenn es noch kein Ergebnis gibt oder das letzte mindestens 14 Tage her ist. */
internal fun isQuestionnaireAvailable(latestCreatedAt: Long?): Boolean {
    if (latestCreatedAt == null) return true
    val days = ChronoUnit.DAYS.between(dateOf(latestCreatedAt), LocalDate.now(ZoneId.systemDefault()))
    return days >= QUESTIONNAIRE_INTERVAL_DAYS
}

/** Datum eines Ergebnisses, zum Beispiel "22. Okt. 2026". */
internal fun dateLabel(createdAt: Long): String =
    dateOf(createdAt).format(DateTimeFormatter.ofPattern(tr("d. MMM yyyy", "MMM d, yyyy"), AppLanguage.locale))
