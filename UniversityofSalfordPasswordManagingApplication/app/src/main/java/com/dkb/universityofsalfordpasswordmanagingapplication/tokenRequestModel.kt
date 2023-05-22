package com.dkb.universityofsalfordpasswordmanagingapplication

data class tokenRequestModel (
    val refreshToken: String,
    val userID: String
    )