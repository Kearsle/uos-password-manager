package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordEditRequestModel (
    val _id: String,
    val programName: String,
    val programUsername: String,
    val programPassword: String
)