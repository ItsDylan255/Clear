package com.example.clear.data

import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.flow.Flow

class PersonalIdeaRepository(private val dao: PersonalIdeaDao) {
    fun observe(categoryId: String): Flow<List<PersonalIdea>> = dao.observeByCategory(categoryId)
    suspend fun get(id: Long): PersonalIdea? = dao.getById(id)
    suspend fun add(idea: PersonalIdea) = dao.insert(idea)
    suspend fun update(idea: PersonalIdea) = dao.update(idea)
    suspend fun delete(idea: PersonalIdea) = dao.delete(idea)
}

class CheckInRepository(private val dao: CheckInDao) {
    /** Speichert einen Check-in. Ohne eine einzige Antwort wird nichts gespeichert. */
    suspend fun save(answers: Map<CheckInCategory, Int>) {
        if (answers.isEmpty()) return
        dao.insert(
            CheckInEntry(
                timestamp = System.currentTimeMillis(),
                energy = answers[CheckInCategory.Energy],
                clarity = answers[CheckInCategory.Clarity],
                recovery = answers[CheckInCategory.Recovery],
                joy = answers[CheckInCategory.Joy],
                mood = answers[CheckInCategory.Mood],
                thoughts = answers[CheckInCategory.Thoughts],
                connection = answers[CheckInCategory.Connection],
                structure = answers[CheckInCategory.Structure]
            )
        )
    }
}
