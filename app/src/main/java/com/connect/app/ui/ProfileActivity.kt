package com.connect.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.connect.app.R
import com.connect.app.data.Contact
import com.connect.app.data.ContactStorage
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var contactStorage: ContactStorage
    
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextSurname: TextInputEditText
    private lateinit var editTextAge: TextInputEditText
    private lateinit var editTextGmail: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextLinkedIn: TextInputEditText
    private lateinit var editTextGithub: TextInputEditText
    private lateinit var editTextFacebook: TextInputEditText
    private lateinit var editTextTwitter: TextInputEditText
    private lateinit var editTextInstagram: TextInputEditText
    private lateinit var editTextWebsite: TextInputEditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.profile_title)
        
        // Initialize views
        initViews()
        
        // Initialize storage
        contactStorage = ContactStorage(this)
        
        // Load existing profile
        loadProfile()
        
        // Setup save button
        buttonSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun initViews() {
        editTextName = findViewById(R.id.editTextName)
        editTextSurname = findViewById(R.id.editTextSurname)
        editTextAge = findViewById(R.id.editTextAge)
        editTextGmail = findViewById(R.id.editTextGmail)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextLinkedIn = findViewById(R.id.editTextLinkedIn)
        editTextGithub = findViewById(R.id.editTextGithub)
        editTextFacebook = findViewById(R.id.editTextFacebook)
        editTextTwitter = findViewById(R.id.editTextTwitter)
        editTextInstagram = findViewById(R.id.editTextInstagram)
        editTextWebsite = findViewById(R.id.editTextWebsite)
        buttonSave = findViewById(R.id.buttonSave)
    }

    private fun loadProfile() {
        val profile = contactStorage.getMyProfile()
        if (profile != null) {
            editTextName.setText(profile.name)
            editTextSurname.setText(profile.surname)
            if (profile.age > 0) {
                editTextAge.setText(profile.age.toString())
            }
            editTextGmail.setText(profile.gmail)
            editTextPhone.setText(profile.phone)
            editTextLinkedIn.setText(profile.linkedIn)
            editTextGithub.setText(profile.github)
            editTextFacebook.setText(profile.facebook)
            editTextTwitter.setText(profile.twitter)
            editTextInstagram.setText(profile.instagram)
            editTextWebsite.setText(profile.website)
        }
    }

    private fun saveProfile() {
        val name = editTextName.text.toString().trim()
        val surname = editTextSurname.text.toString().trim()
        
        // Validate required fields
        if (name.isEmpty()) {
            editTextName.error = "Name is required"
            editTextName.requestFocus()
            return
        }
        
        if (surname.isEmpty()) {
            editTextSurname.error = "Surname is required"
            editTextSurname.requestFocus()
            return
        }
        
        val age = editTextAge.text.toString().toIntOrNull() ?: 0
        
        // Validate age if provided
        if (age < 0 || age > 150) {
            editTextAge.error = "Please enter a valid age"
            editTextAge.requestFocus()
            return
        }
        
        // Create profile
        val existingProfile = contactStorage.getMyProfile()
        val profileId = existingProfile?.id?.ifEmpty { UUID.randomUUID().toString() } 
            ?: UUID.randomUUID().toString()
        
        val profile = Contact(
            id = profileId,
            name = name,
            surname = surname,
            age = age,
            gmail = editTextGmail.text.toString().trim(),
            phone = editTextPhone.text.toString().trim(),
            linkedIn = editTextLinkedIn.text.toString().trim(),
            github = editTextGithub.text.toString().trim(),
            facebook = editTextFacebook.text.toString().trim(),
            twitter = editTextTwitter.text.toString().trim(),
            instagram = editTextInstagram.text.toString().trim(),
            website = editTextWebsite.text.toString().trim(),
            timestamp = System.currentTimeMillis()
        )
        
        // Save profile
        contactStorage.saveMyProfile(profile)
        
        // Show success message
        Toast.makeText(
            this,
            getString(R.string.profile_saved),
            Toast.LENGTH_SHORT
        ).show()
        
        // Close activity
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
