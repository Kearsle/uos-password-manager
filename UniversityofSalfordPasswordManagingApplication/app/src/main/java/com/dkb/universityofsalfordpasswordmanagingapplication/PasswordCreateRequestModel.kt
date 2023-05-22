package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordCreateRequestModel (
    val programName: String,
    val programUsername: String,
    val programPassword: String
)