package com.shubhanya.fingenienxt.data.local.entity

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// User Profile data class
data class UserProfile(
    @DocumentId
    val uid: String = "", // Firebase Auth UID
    val phoneNumber: String = "",
    val name: String = "",
    val dob: String = "", // Date of Birth, store as String for simplicity or use a Date type
    @ServerTimestamp
    val createdAt: Date? = null,
    val lastLogin: Date? = null
) {
    constructor() : this("", "", "", "", null, null)
}