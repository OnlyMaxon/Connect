package com.connect.app.nfc

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Parcelable
import com.connect.app.data.Contact
import com.google.gson.Gson
import java.nio.charset.Charset

class NFCManager(private val activity: Activity) : NfcAdapter.CreateNdefMessageCallback {
    
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private val gson = Gson()
    private var contactToShare: Contact? = null
    
    fun isNfcAvailable(): Boolean {
        return nfcAdapter != null
    }
    
    fun isNfcEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }
    
    fun setContactToShare(contact: Contact) {
        contactToShare = contact
    }
    
    fun enableNfcForegroundDispatch() {
        if (nfcAdapter == null) return
        
        // Enable Android Beam (for older devices)
        nfcAdapter.setNdefPushMessageCallback(this, activity)
    }
    
    fun disableNfcForegroundDispatch() {
        if (nfcAdapter == null) return
        
        // Disable Android Beam
        nfcAdapter.setNdefPushMessageCallback(null, activity)
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
