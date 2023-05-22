package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordEditResponseModel (
    val acknowledged: Boolean,
    val modifiedCount: Int,
    val upsertedId: String,
    val upsertedCount: Int,
    val matchedCount: Int,
    val error: String
)