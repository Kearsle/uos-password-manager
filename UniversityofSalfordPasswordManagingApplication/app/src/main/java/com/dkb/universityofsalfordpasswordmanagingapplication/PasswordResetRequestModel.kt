package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordResetRequestModel (
    val password: String,
    val oldPassword: String
        )