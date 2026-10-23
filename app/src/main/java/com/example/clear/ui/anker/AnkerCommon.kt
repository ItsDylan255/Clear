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
import kotlinx.coroutines.flow.MutableStateFlow

/** Die vier Inhaltstypen von Mein Anker. */
enum class AnkerType(val id: String, val title: String) {
    Quotes("quotes", "Zitate und Gedanken"),
    Images("images", "Meine Bilder"),
    Music("music", "Meine Musik"),
    Letters("letters", "Briefe an mich selbst");

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
    if (EmotionTag.parse(tags).size > 1) " Er gilt auch für andere Emotionen und wird überall gelöscht."
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
            Text(if (minutes > 0) "$minutes Min." else "Timer")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            timerOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(if (option == 0) "Ohne Timer" else "$option Min.") },
                    onClick = {
                        MusicChoice.timerMinutes.value = option
                        open = false
                    }
                )
            }
        }
    }
}
