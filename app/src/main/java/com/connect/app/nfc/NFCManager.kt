package com.connect.app.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import com.connect.app.data.Contact
import com.google.gson.Gson
import java.nio.charset.Charset

/**
 * Manages NFC operations for contact sharing.
 * Handles both sending (Android Beam) and receiving (NDEF discovery) of contact data.
 */
class NFCManager(private val activity: Activity) : NfcAdapter.CreateNdefMessageCallback {
    
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val gson = Gson()
    private var contactToShare: Contact? = null
    
    /**
     * Checks if NFC hardware is available on the device.
     */
    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null
    }
    
    /**
     * Checks if NFC is enabled in device settings.
     */
    fun isNfcEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }
    
    /**
     * Sets the contact to be shared via NFC.
     */
    fun setContactToShare(contact: Contact) {
        contactToShare = contact
    }
    
    /**
     * Enables NFC foreground dispatch to handle NFC events.
     * Should be called in onResume().
     * Note: Android Beam was deprecated in API 29 and removed in API 30+.
     * This method is kept for API compatibility but no longer enables Android Beam.
     */
    fun enableNfcForegroundDispatch() {
        if (nfcAdapter == null) return
        
        // Android Beam is no longer available in modern Android versions (API 30+)
        // NFC tag reading will still work through standard NFC intent filters
    }
    
    /**
     * Disables NFC foreground dispatch.
     * Should be called in onPause().
     */
    fun disableNfcForegroundDispatch() {
        if (nfcAdapter == null) return
        
        // Android Beam is no longer available in modern Android versions (API 30+)
    }
    
    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val contact = contactToShare ?: return createEmptyMessage()
        
        val json = gson.toJson(contact)
        val mimeType = "application/vnd.com.connect.app"
        
        val mimeRecord = NdefRecord.createMime(mimeType, json.toByteArray(Charset.forName("UTF-8")))
        val appRecord = NdefRecord.createApplicationRecord(activity.packageName)
        
        return NdefMessage(arrayOf(mimeRecord, appRecord))
    }
    
    private fun createEmptyMessage(): NdefMessage {
        val emptyRecord = NdefRecord(
            NdefRecord.TNF_EMPTY,
            ByteArray(0),
            ByteArray(0),
            ByteArray(0)
        )
        return NdefMessage(arrayOf(emptyRecord))
    }
    
    /**
     * Extracts contact data from an NFC intent.
     * Returns null if the intent doesn't contain valid contact data.
     */
    fun extractContactFromIntent(intent: Intent): Contact? {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            
            if (rawMessages != null && rawMessages.isNotEmpty()) {
                val messages = arrayOfNulls<NdefMessage>(rawMessages.size)
                for (i in rawMessages.indices) {
                    messages[i] = rawMessages[i] as NdefMessage
                }
                
                // Process the first message
                messages[0]?.let { message ->
                    val records = message.records
                    if (records.isNotEmpty()) {
                        val payload = records[0].payload
                        val json = String(payload, Charset.forName("UTF-8"))
                        
                        return try {
                            gson.fromJson(json, Contact::class.java)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }
        }
        return null
    }
}
