package com.example.clear.ui.anker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clear.data.AnchorImage
import com.example.clear.data.FileStore
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.components.TextInputDialog

@Composable
fun ImagesScreen(
    onBack: () -> Unit,
    onOpenImage: (Long) -> Unit,
    viewModel: ImagesViewModel = viewModel()
) {
    val images by viewModel.images.collectAsState()
    val message by viewModel.message.collectAsState()
    val list = images
    var toDelete by remember { mutableStateOf<AnchorImage?>(null) }
    var captionFor by remember { mutableStateOf<AnchorImage?>(null) }

    // Photo Picker: braucht keine Berechtigung, die App sieht nur das gewaehlte Bild
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.addImage(uri)
    }

    ClearDetailScreen(title = "Meine Bilder", onBack = onBack) {
        Text(
            "Bilder von sicheren Orten, Menschen und Momenten.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        message?.let { text ->
            ClearCard(containerColor = ClearWarningSoft) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
            }
        }
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> EmptyHintCard()
            else -> list.forEach { image ->
                ImageCard(
                    image = image,
                    onOpen = { onOpenImage(image.id) },
                    onEditCaption = { captionFor = image },
                    onDelete = { toDelete = image }
                )
            }
        }
        AddButton("Bild hinzufügen") {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        Text(
            "Das Bild wird in Clear gespeichert und bleibt nur auf deinem Handy.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    captionFor?.let { image ->
        TextInputDialog(
            title = "Beschriftung",
            label = "Beschriftung (optional)",
            initial = image.caption.orEmpty(),
            onConfirm = { viewModel.updateCaption(image.id, it); captionFor = null },
            onDismiss = { captionFor = null }
        )
    }
    toDelete?.let { image ->
        ConfirmDeleteDialog(
            title = "Bild löschen?",
            message = "Das Bild wird aus Clear entfernt. Dein Original bleibt unberührt.",
            onConfirm = { viewModel.delete(image); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

@Composable
private fun ImageCard(
    image: AnchorImage,
    onOpen: () -> Unit,
    onEditCaption: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val file = remember(image.fileName) { FileStore.file(context, FileStore.IMAGES, image.fileName) }

    ClearCard(onClick = onOpen) {
        if (file.exists()) {
            AsyncImage(
                model = file,
                contentDescription = image.caption ?: "Gespeichertes Bild",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(MaterialTheme.shapes.small)
            )
        } else {
            Text(
                "Diese Bilddatei wurde nicht gefunden. Du kannst den Eintrag löschen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                image.caption ?: "Ohne Beschriftung",
                style = MaterialTheme.typography.bodyLarge,
                color = if (image.caption == null) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            OptionsMenu("Optionen für dieses Bild", onEdit = onEditCaption, onDelete = onDelete)
        }
    }
}

/** Grosse Ansicht eines Bildes. */
@Composable
fun ImageViewerScreen(onBack: () -> Unit, viewModel: ImageViewerViewModel = viewModel()) {
    val image by viewModel.image.collectAsState()
    val current = image
    val context = LocalContext.current

    ClearDetailScreen(title = current?.caption ?: "Bild", onBack = onBack) {
        if (current != null) {
            val file = FileStore.file(context, FileStore.IMAGES, current.fileName)
            if (file.exists()) {
                AsyncImage(
                    model = file,
                    contentDescription = current.caption ?: "Gespeichertes Bild",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
                )
            } else {
                Text(
                    "Diese Bilddatei wurde nicht gefunden.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
