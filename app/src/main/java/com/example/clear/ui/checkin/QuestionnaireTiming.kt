package com.example.clear.ui.checkin

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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

/** Fruehestes Datum fuer den naechsten Fragebogen, zum Beispiel "5. November 2026". */
internal fun nextAvailableLabel(latestCreatedAt: Long): String =
    dateOf(latestCreatedAt).plusDays(QUESTIONNAIRE_INTERVAL_DAYS)
        .format(DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMANY))

/** Datum eines Ergebnisses, zum Beispiel "22. Okt. 2026". */
internal fun dateLabel(createdAt: Long): String =
    dateOf(createdAt).format(DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.GERMANY))
