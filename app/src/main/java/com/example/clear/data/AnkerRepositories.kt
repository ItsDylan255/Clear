package com.example.clear.data

import kotlinx.coroutines.flow.Flow

class QuoteRepository(private val dao: QuoteDao) {
    fun observeAll(): Flow<List<Quote>> = dao.observeAll()
    suspend fun get(id: Long): Quote? = dao.getById(id)
    suspend fun add(quote: Quote) = dao.insert(quote)
    suspend fun update(quote: Quote) = dao.update(quote)
    suspend fun delete(quote: Quote) = dao.delete(quote)
}

class LetterRepository(private val dao: LetterDao) {
    fun observeAll(): Flow<List<Letter>> = dao.observeAll()
    fun observe(id: Long): Flow<Letter?> = dao.observeById(id)
    suspend fun get(id: Long): Letter? = dao.getById(id)
    suspend fun add(letter: Letter) = dao.insert(letter)
    suspend fun update(letter: Letter) = dao.update(letter)
    suspend fun delete(letter: Letter) = dao.delete(letter)
}

class EmergencyCardRepository(private val dao: EmergencyCardDao) {
    fun observe(): Flow<EmergencyCard?> = dao.observe()
    suspend fun get(): EmergencyCard? = dao.get()
    suspend fun save(card: EmergencyCard) = dao.save(card)
}
