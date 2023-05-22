package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordsResponseModelItem (
    val _id: String,
    val userID: String,
    val programName: String,
    val programUsername: String,
    val programPassword: String,
    val createdOn: String,
    val updatedOn: String
)