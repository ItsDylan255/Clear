package com.example.clear.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Kopiert ausgewaehlte Bilder und Musik in den privaten App-Ordner.
 * Vorteil: Die Dateien bleiben verfuegbar, auch wenn du das Original verschiebst oder loeschst,
 * und kein anderes Programm kommt an sie heran.
 */
object FileStore {
    const val IMAGES = "anchor_images"
    const val MUSIC = "anchor_music"

    fun file(context: Context, folder: String, fileName: String): File =
        File(File(context.filesDir, folder), fileName)

    /** Gibt den neuen Dateinamen zurueck, oder null wenn das Kopieren nicht geklappt hat. */
    suspend fun import(context: Context, uri: Uri, folder: String): String? =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, folder).apply { mkdirs() }
            val mime = context.contentResolver.getType(uri)
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "bin"
            val target = File(dir, "${UUID.randomUUID()}.$extension")
            try {
                val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
                input.use { source -> target.outputStream().use { out -> source.copyTo(out) } }
                target.name
            } catch (e: Exception) {
                target.delete()
                null
            }
        }

    fun delete(context: Context, folder: String, fileName: String) {
        file(context, folder, fileName).delete()
    }

    fun displayName(context: Context, uri: Uri): String? = try {
        context.contentResolver
            .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
    } catch (e: Exception) {
        null
    }
}
