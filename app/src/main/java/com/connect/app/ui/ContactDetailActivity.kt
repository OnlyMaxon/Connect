package com.connect.app.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.connect.app.R
import com.connect.app.data.Contact
import com.connect.app.data.ContactStorage

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var contactStorage: ContactStorage
    private var contact: Contact? = null
    
    private lateinit var textDetailName: TextView
    private lateinit var textDetailAge: TextView
    private lateinit var textDetailEmail: TextView
    private lateinit var textDetailPhone: TextView
    private lateinit var textDetailLinkedIn: TextView
    private lateinit var textDetailGithub: TextView
    private lateinit var textDetailFacebook: TextView
    private lateinit var textDetailTwitter: TextView
    private lateinit var textDetailInstagram: TextView
    private lateinit var textDetailWebsite: TextView
    
    private lateinit var layoutEmail: LinearLayout
    private lateinit var layoutPhone: LinearLayout
    private lateinit var layoutLinkedIn: LinearLayout
    private lateinit var layoutGithub: LinearLayout
    private lateinit var layoutFacebook: LinearLayout
    private lateinit var layoutTwitter: LinearLayout
    private lateinit var layoutInstagram: LinearLayout
    private lateinit var layoutWebsite: LinearLayout
    
    private lateinit var buttonDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        
        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.contact_details)
        
        // Initialize storage
        contactStorage = ContactStorage(this)
        
        // Get contact from intent
        contact = intent.getSerializableExtra("CONTACT") as? Contact
        
        if (contact == null) {
            finish()
            return
        }
        
        // Initialize views
        initViews()
        
        // Display contact information
        displayContact(contact!!)
        
        // Setup delete button
        buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun initViews() {
        textDetailName = findViewById(R.id.textDetailName)
        textDetailAge = findViewById(R.id.textDetailAge)
        textDetailEmail = findViewById(R.id.textDetailEmail)
        textDetailPhone = findViewById(R.id.textDetailPhone)
        textDetailLinkedIn = findViewById(R.id.textDetailLinkedIn)
        textDetailGithub = findViewById(R.id.textDetailGithub)
        textDetailFacebook = findViewById(R.id.textDetailFacebook)
        textDetailTwitter = findViewById(R.id.textDetailTwitter)
        textDetailInstagram = findViewById(R.id.textDetailInstagram)
        textDetailWebsite = findViewById(R.id.textDetailWebsite)
        
        layoutEmail = findViewById(R.id.layoutEmail)
        layoutPhone = findViewById(R.id.layoutPhone)
        layoutLinkedIn = findViewById(R.id.layoutLinkedIn)
        layoutGithub = findViewById(R.id.layoutGithub)
        layoutFacebook = findViewById(R.id.layoutFacebook)
        layoutTwitter = findViewById(R.id.layoutTwitter)
        layoutInstagram = findViewById(R.id.layoutInstagram)
        layoutWebsite = findViewById(R.id.layoutWebsite)
        
        buttonDelete = findViewById(R.id.buttonDelete)
    }

    private fun displayContact(contact: Contact) {
        // Display name and age
        textDetailName.text = contact.getFullName()
        textDetailAge.text = if (contact.age > 0) {
            "Age: ${contact.age}"
        } else {
            "Age: Not specified"
        }
        
        // Display email
        if (contact.gmail.isNotEmpty()) {
            textDetailEmail.text = contact.gmail
            layoutEmail.visibility = View.VISIBLE
        } else {
            layoutEmail.visibility = View.GONE
        }
        
        // Display phone
        if (contact.phone.isNotEmpty()) {
            textDetailPhone.text = contact.phone
            layoutPhone.visibility = View.VISIBLE
        } else {
            layoutPhone.visibility = View.GONE
        }
        
        // Display LinkedIn
        if (contact.linkedIn.isNotEmpty()) {
            textDetailLinkedIn.text = contact.linkedIn
            layoutLinkedIn.visibility = View.VISIBLE
        } else {
            layoutLinkedIn.visibility = View.GONE
        }
        
        // Display GitHub
        if (contact.github.isNotEmpty()) {
            textDetailGithub.text = contact.github
            layoutGithub.visibility = View.VISIBLE
        } else {
            layoutGithub.visibility = View.GONE
        }
        
        // Display Facebook
        if (contact.facebook.isNotEmpty()) {
            textDetailFacebook.text = contact.facebook
            layoutFacebook.visibility = View.VISIBLE
        } else {
            layoutFacebook.visibility = View.GONE
        }
        
        // Display Twitter
        if (contact.twitter.isNotEmpty()) {
            textDetailTwitter.text = contact.twitter
            layoutTwitter.visibility = View.VISIBLE
        } else {
            layoutTwitter.visibility = View.GONE
        }
        
        // Display Instagram
        if (contact.instagram.isNotEmpty()) {
            textDetailInstagram.text = contact.instagram
            layoutInstagram.visibility = View.VISIBLE
        } else {
            layoutInstagram.visibility = View.GONE
        }
        
        // Display Website
        if (contact.website.isNotEmpty()) {
            textDetailWebsite.text = contact.website
            layoutWebsite.visibility = View.VISIBLE
        } else {
            layoutWebsite.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_contact))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteContact()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteContact() {
        contact?.let {
            contactStorage.deleteContact(it.id)
            Toast.makeText(
                this,
                "Contact deleted",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
