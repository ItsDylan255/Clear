package com.example.clear

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.example.clear.watch.WatchSync
import java.util.Locale

/** Sprache der App. System = wie das Handy (Deutsch auf deutschen Handys, sonst Englisch). */
enum class LanguageChoice(val tag: String) {
    System(""),
    German("de"),
    English("en")
}

/**
 * Die eingestellte Sprache. Ab Android 13 speichert Android selbst die Sprache der App (sie laesst
 * sich dann auch in den Systemeinstellungen aendern), davor merkt sich Clear sie in den Einstellungen.
 */
object AppLanguage {
    private const val FILE = "clear_prefs"
    private const val KEY = "language"

    /** Ergebnis der Wahl. Wird beim Start und nach jeder Aenderung neu bestimmt. */
    @Volatile
    var isEnglish: Boolean = Locale.getDefault().language != "de"
        private set

    /** Fuer Datums- und Zahlenformate passend zur Sprache. */
    val locale: Locale get() = if (isEnglish) Locale.ENGLISH else Locale.GERMANY

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun choice(context: Context): LanguageChoice {
        val tag = if (Build.VERSION.SDK_INT >= 33) {
            context.getSystemService(LocaleManager::class.java).applicationLocales.toLanguageTags()
        } else {
            prefs(context).getString(KEY, "").orEmpty()
        }
        return LanguageChoice.entries.firstOrNull { it.tag.isNotEmpty() && tag.startsWith(it.tag) }
            ?: LanguageChoice.System
    }

    /** Beim Start und nach Aenderungen der Systemsprache aufrufen. */
    fun init(context: Context) {
        isEnglish = when (choice(context)) {
            LanguageChoice.English -> true
            LanguageChoice.German -> false
            LanguageChoice.System -> Resources.getSystem().configuration.locales[0].language != "de"
        }
    }

    /** Aendert die Sprache und baut den Bildschirm in der neuen Sprache neu auf. */
    fun set(activity: Activity, choice: LanguageChoice) {
        val wasEnglish = isEnglish
        if (Build.VERSION.SDK_INT >= 33) {
            activity.getSystemService(LocaleManager::class.java).applicationLocales =
                if (choice == LanguageChoice.System) LocaleList.getEmptyLocaleList()
                else LocaleList.forLanguageTags(choice.tag)
        } else {
            prefs(activity).edit().putString(KEY, choice.tag).apply()
        }
        init(activity)
        // Ab Android 13 baut das System den Bildschirm selbst neu auf, wenn sich die Sprache aendert
        if (Build.VERSION.SDK_INT < 33 && wasEnglish != isEnglish) activity.recreate()
        WatchSync.update(activity)
    }

    /** Zurueck zur Sprache des Handys (beim Loeschen aller Daten). */
    fun reset(context: Context) {
        if (Build.VERSION.SDK_INT >= 33) {
            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.getEmptyLocaleList()
        } else {
            prefs(context).edit().remove(KEY).apply()
        }
        init(context)
    }

    /** Vor Android 13: Systemtexte wie Datumsauswahl in der gewaehlten Sprache anzeigen. */
    fun wrap(base: Context): Context {
        if (Build.VERSION.SDK_INT >= 33) return base
        val tag = prefs(base).getString(KEY, "").orEmpty()
        if (tag.isEmpty()) return base
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale.forLanguageTag(tag))
        return base.createConfigurationContext(config)
    }
}

/** Deutscher oder englischer Text, je nach eingestellter Sprache. */
fun tr(de: String, en: String): String = if (AppLanguage.isEnglish) en else de
