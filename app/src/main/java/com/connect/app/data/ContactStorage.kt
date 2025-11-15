package com.connect.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ContactStorage(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveMyProfile(contact: Contact) {
        val json = gson.toJson(contact)
        sharedPreferences.edit().putString(KEY_MY_PROFILE, json).apply()
    }
    
    fun getMyProfile(): Contact? {
        val json = sharedPreferences.getString(KEY_MY_PROFILE, null) ?: return null
        return try {
            gson.fromJson(json, Contact::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveContact(contact: Contact) {
        val contacts = getAllContacts().toMutableList()
        // Remove existing contact with same ID if exists
        contacts.removeAll { it.id == contact.id }
        contacts.add(contact)
        saveAllContacts(contacts)
    }
    
    fun getAllContacts(): List<Contact> {
        val json = sharedPreferences.getString(KEY_CONTACTS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Contact>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun deleteContact(contactId: String) {
        val contacts = getAllContacts().toMutableList()
        contacts.removeAll { it.id == contactId }
        saveAllContacts(contacts)
    }
    
    private fun saveAllContacts(contacts: List<Contact>) {
        val json = gson.toJson(contacts)
        sharedPreferences.edit().putString(KEY_CONTACTS, json).apply()
    }
    
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
    
    companion object {
        private const val PREFS_NAME = "connect_prefs"
        private const val KEY_MY_PROFILE = "my_profile"
        private const val KEY_CONTACTS = "contacts"
    }
}
