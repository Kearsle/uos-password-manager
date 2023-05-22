package com.dkb.universityofsalfordpasswordmanagingapplication

data class RegisterRequestModel (
    val username: String,
    val password: String,
    val email: String,
)