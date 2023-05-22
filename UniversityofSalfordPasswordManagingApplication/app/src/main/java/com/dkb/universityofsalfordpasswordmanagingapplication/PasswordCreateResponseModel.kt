package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordCreateResponseModel (
    val userID: String,
    val programName: String,
    val programUsername: String,
    val programPassword: String,
    val _id: String,
    val createdOn: String,
    val updatedOn: String,
    val errors: String
)