package com.example.clear.ui.anker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.EmergencyCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmergencyPersonState(
    val isLoading: Boolean = true,
    val name: String = "",
    val phone: String = "",
    val note: String = ""
)

data class EmergencyPlanState(
    val isLoading: Boolean = true,
    val plan: String = ""
)

/** Speichert nur Name, Nummer und Hinweis. Der Plan bleibt unberuehrt. */
class EmergencyPersonEditViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).emergencyCardDao()
    private var saving = false

    private val _state = MutableStateFlow(EmergencyPersonState())
    val state: StateFlow<EmergencyPersonState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val c = dao.get() ?: EmergencyCard()
            _state.value = EmergencyPersonState(
                isLoading = false, name = c.contactName, phone = c.contactPhone, note = c.contactNote
            )
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v) }
    fun onPhoneChange(v: String) = _state.update { it.copy(phone = v) }
    fun onNoteChange(v: String) = _state.update { it.copy(note = v) }

    fun save(onDone: () -> Unit) {
        if (saving) return
        saving = true
        val s = _state.value
        viewModelScope.launch {
            val current = dao.get() ?: EmergencyCard()
            dao.save(
                current.copy(
                    contactName = s.name.trim(),
                    contactPhone = s.phone.trim(),
                    contactNote = s.note.trim()
                )
            )
            onDone()
        }
    }
}

/** Speichert nur den Plan. Die Notfallperson bleibt unberuehrt. */
class EmergencyPlanEditViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).emergencyCardDao()
    private var saving = false

    private val _state = MutableStateFlow(EmergencyPlanState())
    val state: StateFlow<EmergencyPlanState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val c = dao.get() ?: EmergencyCard()
            _state.value = EmergencyPlanState(isLoading = false, plan = c.safetyPlan)
        }
    }

    fun onPlanChange(v: String) = _state.update { it.copy(plan = v) }

    fun save(onDone: () -> Unit) {
        if (saving) return
        saving = true
        val s = _state.value
        viewModelScope.launch {
            val current = dao.get() ?: EmergencyCard()
            dao.save(current.copy(safetyPlan = s.plan.trim()))
            onDone()
        }
    }
}
