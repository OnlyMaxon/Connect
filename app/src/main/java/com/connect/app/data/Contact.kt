package com.connect.app.data

import java.io.Serializable

/**
 * Data class representing a contact with personal and social information.
 * Used for both user's own profile and received contacts via NFC.
 */
data class Contact(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val age: Int = 0,
    val gmail: String = "",
    val linkedIn: String = "",
    val github: String = "",
    val facebook: String = "",
    val twitter: String = "",
    val instagram: String = "",
    val website: String = "",
    val phone: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    
    /**
     * Returns the full name combining first and last name.
     */
    fun getFullName(): String = "$name $surname".trim()
    
    /**
     * Checks if the contact has any social media links.
     */
    fun hasSocialLinks(): Boolean = 
        linkedIn.isNotEmpty() || github.isNotEmpty() || facebook.isNotEmpty() || 
        twitter.isNotEmpty() || instagram.isNotEmpty() || website.isNotEmpty()
    
    /**
     * Checks if the contact has valid required information.
     */
    fun isValid(): Boolean = name.isNotEmpty() && surname.isNotEmpty()
    
    /**
     * Returns a display string for contact info (email or phone if available).
     */
    fun getPrimaryContactInfo(): String = when {
        gmail.isNotEmpty() -> gmail
        phone.isNotEmpty() -> phone
        else -> "No contact info"
    }
}
