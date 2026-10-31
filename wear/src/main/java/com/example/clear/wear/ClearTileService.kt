package com.example.clear.wear

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * Kachel neben dem Zifferblatt: wie viele Ideen laufen (mit der ersten) und ob du heute schon
 * eingecheckt hast. Antippen oeffnet die laufenden Ideen auf der Uhr.
 */
class ClearTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val status = StatusStore.load(this)
        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            // Alle 30 Minuten neu zeichnen, damit "heute" nach Mitternacht stimmt
            .setFreshnessIntervalMillis(30 * 60 * 1000L)
            .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout(status)))
            .build()
        return Futures.immediateFuture(tile)
    }

    @Deprecated("Nur fuer aeltere Wear-OS-Versionen; die Kachel hat keine Bilder.")
    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ListenableFuture<ResourceBuilders.Resources> =
        Futures.immediateFuture(ResourceBuilders.Resources.Builder().setVersion(RESOURCES_VERSION).build())

    private fun layout(status: WatchStatus?): LayoutElementBuilders.LayoutElement {
        val column = LayoutElementBuilders.Column.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(text("Clear", 14f, PRIMARY, bold = true))
            .addContent(spacer(8f))

        if (status == null) {
            column.addContent(text(tr(
                "Öffne Clear auf dem Handy",
                "Open Clear on your phone"
            ), 14f, SECONDARY, maxLines = 2))
        } else {
            val ideas = status.ideas
            column.addContent(
                text(
                    when (ideas.size) {
                        0 -> tr("Keine Idee läuft", "No idea running")
                        1 -> tr("1 Idee läuft", "1 idea running")
                        else -> tr("${ideas.size} Ideen laufen", "${ideas.size} ideas running")
                    },
                    16f, TEXT, bold = true
                )
            )
            ideas.firstOrNull()?.let { column.addContent(text(it.title, 14f, SECONDARY, maxLines = 1)) }
            column.addContent(spacer(10f))
            column.addContent(
                text(
                    if (status.checkedInToday()) tr("Heute eingecheckt", "Checked in today")
                    else tr("Heute noch kein Check-in", "No check-in yet today"),
                    14f, if (status.checkedInToday()) PRIMARY else SECONDARY
                )
            )
        }

        val openApp = ModifiersBuilders.Clickable.Builder()
            .setId("open")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(packageName)
                            .setClassName(MainActivity::class.java.name)
                            .build()
                    )
                    .build()
            )
            .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(expand())
            .setHeight(expand())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(openApp)
                    .setPadding(ModifiersBuilders.Padding.Builder().setStart(dp(24f)).setEnd(dp(24f)).build())
                    .build()
            )
            .addContent(column.build())
            .build()
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false, maxLines: Int = 1) =
        LayoutElementBuilders.Text.Builder()
            .setText(value)
            .setMaxLines(maxLines)
            .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE_END)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(sp(size))
                    .setColor(argb(color))
                    .setWeight(
                        if (bold) LayoutElementBuilders.FONT_WEIGHT_BOLD else LayoutElementBuilders.FONT_WEIGHT_NORMAL
                    )
                    .build()
            )
            .build()

    private fun spacer(height: Float) = LayoutElementBuilders.Spacer.Builder().setHeight(dp(height)).build()

    private companion object {
        const val RESOURCES_VERSION = "1"
        const val PRIMARY = 0xFF8FB5DD.toInt()
        const val TEXT = 0xFFE6EBF0.toInt()
        const val SECONDARY = 0xFFAAB4BF.toInt()
    }
}
