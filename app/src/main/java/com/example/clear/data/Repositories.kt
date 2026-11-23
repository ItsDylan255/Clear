package com.example.clear.data

import com.example.clear.model.CheckInCategory
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class PersonalIdeaRepository(private val dao: PersonalIdeaDao) {
    fun observeAll(): Flow<List<PersonalIdea>> = dao.observeAll()
    suspend fun get(id: Long): PersonalIdea? = dao.getById(id)
    suspend fun add(idea: PersonalIdea) = dao.insert(idea)
    suspend fun update(idea: PersonalIdea) = dao.update(idea)
    suspend fun delete(idea: PersonalIdea) = dao.delete(idea)
}

/** Eigene Themen fuer Ideen: anlegen, umbenennen, loeschen. */
class IdeaTopicRepository(private val dao: IdeaTopicDao) {
    fun observeAll(): Flow<List<IdeaTopic>> = dao.observeAll()

    /** Legt ein Thema an und gibt seine id zurueck, oder null bei leerem Namen. */
    suspend fun add(name: String): Long? {
        val clean = name.trim()
        if (clean.isEmpty()) return null
        return dao.insert(IdeaTopic(name = clean, createdAt = System.currentTimeMillis()))
    }

    suspend fun rename(topic: IdeaTopic, name: String) {
        val clean = name.trim()
        if (clean.isNotEmpty()) dao.update(topic.copy(name = clean))
    }

    /** Loescht ein Thema. Seine Ideen wandern nach `moveTo` (null = "Ohne Thema"). */
    suspend fun delete(topic: IdeaTopic, moveTo: Long?) {
        dao.moveIdeas(topic.id, moveTo)
        dao.delete(topic)
    }
}

class CheckInRepository(private val dao: CheckInDao) {
    /** Der heutige Check-in (lokales Datum), oder null. Gibt es mehrere, der neueste. */
    suspend fun getToday(): CheckInEntry? {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return dao.getLatestBetween(start, end)
    }

    /**
     * Speichert in den heutigen Check-in. Pro Tag gibt es einen Eintrag: Gibt es heute schon
     * einen, wird er ergaenzt, sonst entsteht ein neuer (Datum und Uhrzeit: jetzt).
     * `answers` ersetzt Teil 1, `facts` ersetzt Teil 2. null = diesen Teil nicht anfassen.
     * Ist danach alles leer, wird nichts gespeichert (bzw. der leere Eintrag entfernt).
     */
    suspend fun saveToday(answers: Map<CheckInCategory, Int>?, facts: CheckInFactAnswers?) {
        val existing = getToday()
        var entry = existing ?: CheckInEntry(timestamp = System.currentTimeMillis())
        if (answers != null) {
            entry = entry.copy(
                mood = answers[CheckInCategory.Mood],
                joy = answers[CheckInCategory.Joy],
                energy = answers[CheckInCategory.Energy],
                clarity = answers[CheckInCategory.Clarity],
                thoughts = answers[CheckInCategory.Thoughts],
                connection = answers[CheckInCategory.Connection],
                calm = answers[CheckInCategory.Calm],
                satisfaction = answers[CheckInCategory.Satisfaction]
            )
        }
        if (facts != null) {
            entry = entry.copy(
                sleep = facts.levels[CheckInFact.Sleep],
                alcohol = facts.levels[CheckInFact.Alcohol],
                movement = facts.levels[CheckInFact.Movement],
                routine = facts.levels[CheckInFact.Routine],
                contact = facts.levels[CheckInFact.Contact],
                rest = facts.levels[CheckInFact.Rest],
                dayTypes = DayType.entries.filter { it in facts.dayTypes }.joinToString(",") { it.id },
                extraChipIds = facts.extraChipIds.sorted().joinToString(",")
            )
        }
        val empty = entry.ratings.isEmpty() && !entry.hasFacts
        when {
            existing == null && empty -> Unit
            existing == null -> dao.insert(entry)
            empty -> dao.delete(existing)
            else -> dao.update(entry)
        }
    }
}

/** Antworten aus Teil 2 eines gespeicherten Check-ins, zum Vorausfuellen. */
fun CheckInEntry.factAnswers(): CheckInFactAnswers = CheckInFactAnswers(
    levels = CheckInFact.entries.mapNotNull { f -> fact(f)?.let { f to it } }.toMap(),
    dayTypes = dayTypeSet,
    extraChipIds = extraIdSet
)

/** Eigene Extras-Chips: anlegen, umbenennen, (de)aktivieren. Geloescht wird nie, alte Eintraege behalten ihre Chips. */
class ExtraChipRepository(private val dao: ExtraChipDao) {
    fun observeAll(): Flow<List<ExtraChip>> = dao.observeAll()

    /** Legt einen Chip an und gibt seine id zurueck, oder null bei leerem Namen. */
    suspend fun add(name: String): Long? {
        val clean = name.trim()
        if (clean.isEmpty()) return null
        return dao.insert(ExtraChip(name = clean, createdAt = System.currentTimeMillis()))
    }

    suspend fun rename(chip: ExtraChip, name: String) {
        val clean = name.trim()
        if (clean.isNotEmpty()) dao.update(chip.copy(name = clean))
    }

    suspend fun setActive(chip: ExtraChip, active: Boolean) = dao.update(chip.copy(active = active))
}
