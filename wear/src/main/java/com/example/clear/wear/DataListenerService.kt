package com.example.clear.wear

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

/** Bekommt neue Staende vom Handy, auch wenn die Uhr-App geschlossen ist, und aktualisiert die Kachel. */
class DataListenerService : WearableListenerService() {

    override fun onDataChanged(events: DataEventBuffer) {
        events.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == PhoneLink.PATH_STATUS) {
                StatusStore.save(applicationContext, WatchStatus.from(DataMapItem.fromDataItem(event.dataItem).dataMap))
            }
        }
    }
}
