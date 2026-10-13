package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdea
import com.example.clear.data.PersonalIdeaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Liefert die Ideen einer Kategorie. Die Kategorie kommt aus der Navigation ("id"). */
class CategoryViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val categoryId: String = savedStateHandle.get<String>("id") ?: ""
    private val repository =
        PersonalIdeaRepository(ClearDatabase.getInstance(application).personalIdeaDao())

    /** null = wird noch geladen. */
    val ideas: StateFlow<List<PersonalIdea>?> = repository.observe(categoryId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(idea: PersonalIdea) {
        viewModelScope.launch { repository.delete(idea) }
    }
}
