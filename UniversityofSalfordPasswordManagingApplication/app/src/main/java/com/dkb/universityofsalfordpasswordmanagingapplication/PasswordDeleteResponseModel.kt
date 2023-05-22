package com.dkb.universityofsalfordpasswordmanagingapplication

data class PasswordDeleteResponseModel (
    val acknowledged: String,
    val deletedCount: String,
    val error: String
)