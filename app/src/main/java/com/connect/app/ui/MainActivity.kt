package com.connect.app.ui

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private lateinit var textNfcSubtitle: TextView
    private lateinit var imageNfcIcon: android.widget.ImageView
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var layoutEmptyState: android.widget.LinearLayout
    private lateinit var fabEditProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        textNfcStatus = findViewById(R.id.textNfcStatus)
        textNfcSubtitle = findViewById(R.id.textNfcSubtitle)
        imageNfcIcon = findViewById(R.id.imageNfcIcon)
        recyclerViewContacts = findViewById(R.id.recyclerViewContacts)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
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
        
        // Recheck NFC status (profile might have been created/updated)
        checkNfcStatus()
        
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
                textNfcStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                imageNfcIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                textNfcSubtitle.text = "This device does not support NFC"
            }
            !nfcManager.isNfcEnabled() -> {
                textNfcStatus.text = getString(R.string.nfc_disabled)
                textNfcStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                imageNfcIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                textNfcSubtitle.text = "Tap here to enable NFC in Settings"
                textNfcStatus.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
                textNfcSubtitle.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
            }
            else -> {
                val myProfile = contactStorage.getMyProfile()
                if (myProfile == null) {
                    textNfcStatus.text = getString(R.string.setup_profile_first)
                    textNfcStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                    imageNfcIcon.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
                    textNfcSubtitle.text = "Create your profile to start sharing"
                } else {
                    textNfcStatus.text = "✓ Ready to Share"
                    textNfcStatus.setTextColor(ContextCompat.getColor(this, R.color.accent))
                    imageNfcIcon.setColorFilter(ContextCompat.getColor(this, R.color.accent))
                    textNfcSubtitle.text = "Tap another phone to share your contact"
                }
            }
        }
    }

    private fun loadContacts() {
        val contacts = contactStorage.getAllContacts()
        if (contacts.isEmpty()) {
            recyclerViewContacts.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerViewContacts.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            contactsAdapter.updateContacts(contacts.sortedByDescending { it.timestamp })
        }
    }

    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            try {
                val receivedContact = nfcManager.extractContactFromIntent(intent)
                
                if (receivedContact != null) {
                    // Validate received contact has minimum required data
                    if (receivedContact.name.isEmpty() && receivedContact.surname.isEmpty()) {
                        Toast.makeText(
                            this,
                            "Received invalid contact data",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    
                    // Check for duplicate contacts (same email or name+surname)
                    val existingContacts = contactStorage.getAllContacts()
                    val isDuplicate = existingContacts.any { contact ->
                        (contact.gmail.isNotEmpty() && contact.gmail == receivedContact.gmail) ||
                        (contact.name == receivedContact.name && contact.surname == receivedContact.surname)
                    }
                    
                    if (isDuplicate) {
                        Toast.makeText(
                            this,
                            "Contact already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    
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
                } else {
                    Toast.makeText(
                        this,
                        "Failed to read contact data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error processing NFC data",
                    Toast.LENGTH_SHORT
                ).show()
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
