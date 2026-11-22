package com.example.clear.data

import com.example.clear.model.CheckInCategory
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import kotlinx.coroutines.flow.Flow

class PersonalIdeaRepository(private val dao: PersonalIdeaDao) {
    fun observe(categoryId: String): Flow<List<PersonalIdea>> = dao.observeByCategory(categoryId)
    suspend fun get(id: Long): PersonalIdea? = dao.getById(id)
    suspend fun add(idea: PersonalIdea) = dao.insert(idea)
    suspend fun update(idea: PersonalIdea) = dao.update(idea)
    suspend fun delete(idea: PersonalIdea) = dao.delete(idea)
}

class CheckInRepository(private val dao: CheckInDao) {
    /**
     * Speichert einen Check-in mit Datum und Uhrzeit (jetzt). Ohne eine einzige Antwort in
     * Teil 1 oder Teil 2 wird nichts gespeichert.
     */
    suspend fun save(answers: Map<CheckInCategory, Int>, facts: CheckInFactAnswers = CheckInFactAnswers()) {
        if (answers.isEmpty() && facts.isEmpty) return
        dao.insert(
            CheckInEntry(
                timestamp = System.currentTimeMillis(),
                mood = answers[CheckInCategory.Mood],
                joy = answers[CheckInCategory.Joy],
                energy = answers[CheckInCategory.Energy],
                clarity = answers[CheckInCategory.Clarity],
                thoughts = answers[CheckInCategory.Thoughts],
                connection = answers[CheckInCategory.Connection],
                calm = answers[CheckInCategory.Calm],
                satisfaction = answers[CheckInCategory.Satisfaction],
                sleep = facts.levels[CheckInFact.Sleep],
                alcohol = facts.levels[CheckInFact.Alcohol],
                movement = facts.levels[CheckInFact.Movement],
                routine = facts.levels[CheckInFact.Routine],
                contact = facts.levels[CheckInFact.Contact],
                rest = facts.levels[CheckInFact.Rest],
                dayTypes = DayType.entries.filter { it in facts.dayTypes }.joinToString(",") { it.id },
                extraChipIds = facts.extraChipIds.sorted().joinToString(",")
            )
        )
    }
}
