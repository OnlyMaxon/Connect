package com.connect.app.ui

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.connect.app.R
import com.connect.app.data.Contact
import com.connect.app.data.ContactStorage
import com.connect.app.nfc.NFCManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var nfcManager: NFCManager
    private lateinit var contactStorage: ContactStorage
    private lateinit var contactsAdapter: ContactsAdapter
    
    private lateinit var textNfcStatus: TextView
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var textEmptyState: TextView
    private lateinit var fabEditProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        textNfcStatus = findViewById(R.id.textNfcStatus)
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts)
        textEmptyState = findViewById(R.id.textEmptyState)
        fabEditProfile = findViewById(R.id.fabEditProfile)
        
        // Initialize managers
        nfcManager = NFCManager(this)
        contactStorage = ContactStorage(this)
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Check NFC status
        checkNfcStatus()
        
        // Load contacts
        loadContacts()
        
        // Setup FAB click listener
        fabEditProfile.setOnClickListener {
            openProfileActivity()
        }
        
        // Handle NFC intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        
        // Reload contacts in case they were updated
        loadContacts()
        
        // Enable NFC foreground dispatch
        val myProfile = contactStorage.getMyProfile()
        if (myProfile != null) {
            nfcManager.setContactToShare(myProfile)
            nfcManager.enableNfcForegroundDispatch()
        }
    }

    override fun onPause() {
        super.onPause()
        nfcManager.disableNfcForegroundDispatch()
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(mutableListOf()) { contact ->
            openContactDetail(contact)
        }
        recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactsAdapter
        }
    }

    private fun checkNfcStatus() {
        when {
            !nfcManager.isNfcAvailable() -> {
                textNfcStatus.text = getString(R.string.nfc_not_available)
                textNfcStatus.setTextColor(getColor(android.R.color.holo_red_dark))
            }
            !nfcManager.isNfcEnabled() -> {
                textNfcStatus.text = getString(R.string.nfc_disabled)
                textNfcStatus.setTextColor(getColor(android.R.color.holo_orange_dark))
                textNfcStatus.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
            }
            else -> {
                val myProfile = contactStorage.getMyProfile()
                if (myProfile == null) {
                    textNfcStatus.text = getString(R.string.setup_profile_first)
                    textNfcStatus.setTextColor(getColor(android.R.color.holo_orange_dark))
                } else {
                    textNfcStatus.text = getString(R.string.nfc_ready)
                    textNfcStatus.setTextColor(getColor(R.color.primary))
                }
            }
        }
    }

    private fun loadContacts() {
        val contacts = contactStorage.getAllContacts()
        if (contacts.isEmpty()) {
            recyclerViewContacts.visibility = View.GONE
            textEmptyState.visibility = View.VISIBLE
        } else {
            recyclerViewContacts.visibility = View.VISIBLE
            textEmptyState.visibility = View.GONE
            contactsAdapter.updateContacts(contacts.sortedByDescending { it.timestamp })
        }
    }

    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val receivedContact = nfcManager.extractContactFromIntent(intent)
            
            if (receivedContact != null) {
                // Generate a unique ID for the received contact
                val contactWithId = receivedContact.copy(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis()
                )
                
                // Save the contact
                contactStorage.saveContact(contactWithId)
                
                // Update UI
                loadContacts()
                
                // Show success message
                Toast.makeText(
                    this,
                    getString(R.string.contact_received),
                    Toast.LENGTH_LONG
                ).show()
                
                // Optionally open the contact detail
                openContactDetail(contactWithId)
            }
        }
    }

    private fun openProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun openContactDetail(contact: Contact) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra("CONTACT", contact)
        }
        startActivity(intent)
    }
}
