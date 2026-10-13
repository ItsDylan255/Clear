package com.example.clear.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ReminderSettings
import com.example.clear.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Gemeinsame Logik fuer Erinnerungen mit Uhrzeit und Wochentagen (ohne Zitat-Pool). */
abstract class SimpleReminderViewModel(
    app: Application,
    private val settingsId: Int,
    private val defaultHour: Int
) : AndroidViewModel(app) {
    private val context: Application get() = getApplication()
    private val dao = ClearDatabase.getInstance(app).reminderDao()

    private fun defaults() = ReminderSettings(id = settingsId, hour = defaultHour)

    val settings: StateFlow<ReminderSettings> = dao.observeSettingsById(settingsId)
        .map { it ?: defaults() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaults())

    fun setEnabled(enabled: Boolean) = update { it.copy(enabled = enabled) }
    fun setTime(hour: Int, minute: Int) = update { it.copy(hour = hour, minute = minute) }

    /** dayIndex: 0 = Montag ... 6 = Sonntag */
    fun toggleDay(dayIndex: Int) = update { it.copy(daysMask = it.daysMask xor (1 shl dayIndex)) }

    private fun update(change: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch {
            val newSettings = change(dao.getSettingsById(settingsId) ?: defaults())
            dao.saveSettings(newSettings)
            ReminderScheduler.reschedule(context, newSettings)
        }
    }
}

class MeditationReminderViewModel(app: Application) :
    SimpleReminderViewModel(app, ReminderScheduler.MEDITATION_ID, 18)

class CheckInReminderViewModel(app: Application) :
    SimpleReminderViewModel(app, ReminderScheduler.CHECKIN_ID, 20)