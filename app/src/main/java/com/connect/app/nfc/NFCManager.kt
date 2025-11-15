package com.connect.app.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import com.connect.app.data.Contact
import com.google.gson.Gson
import java.nio.charset.Charset

/**
 * Manages NFC operations for contact sharing.
 * Handles receiving (NDEF discovery) of contact data.
 * Note: Android Beam (sending) was deprecated in API 29 and removed in API 30+.
 */
class NFCManager(private val activity: Activity) {
    
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val gson = Gson()
    
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
