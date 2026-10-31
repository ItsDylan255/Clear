package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.model.EmotionTag
import com.example.clear.model.MusicMode
import com.example.clear.tr
import kotlinx.coroutines.flow.MutableStateFlow

/** Die vier Inhaltstypen von Mein Anker. */
enum class AnkerType(val id: String, private val titleDe: String, private val titleEn: String) {
    Quotes("quotes", "Zitate und Gedanken", "Quotes and thoughts"),
    Images("images", "Meine Bilder", "My images"),
    Music("music", "Meine Musik", "My music"),
    Letters("letters", "Briefe an mich selbst", "Letters to myself");

    val title: String get() = tr(titleDe, titleEn)

    companion object {
        fun fromId(id: String?): AnkerType? = entries.firstOrNull { it.id == id }
    }
}

/** Merkt sich die Musikwahl, solange die App laeuft. Gilt fuer Uebersicht und Einzelansicht. */
object MusicChoice {
    val mode = MutableStateFlow(MusicMode.Allow)
    val timerMinutes = MutableStateFlow(0)
}

private val timerOptions = listOf(0, 5, 10, 15, 20, 30)   // 0 = ohne Timer

/** Hinweis fuer die Loeschbestaetigung, wenn ein Eintrag zu mehreren Emotionen gehoert. */
fun deleteNote(tags: String): String =
    if (EmotionTag.parse(tags).size > 1) tr(
        " Er gilt auch für andere Emotionen und wird überall gelöscht.",
        " It also belongs to other emotions and will be deleted everywhere."
    )
    else ""

/** Kleiner Knopf mit Auswahlmenue fuer den Timer. Wirkt beim naechsten Start eines Titels. */
@Composable
fun TimerMenuButton() {
    val minutes by MusicChoice.timerMinutes.collectAsState()
    var open by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { open = true }) {
            Icon(Icons.Outlined.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (minutes > 0) tr("$minutes Min.", "$minutes min") else "Timer")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            timerOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(if (option == 0) tr("Ohne Timer", "No timer") else tr("$option Min.", "$option min")) },
                    onClick = {
                        MusicChoice.timerMinutes.value = option
                        open = false
                    }
                )
            }
        }
    }
}
