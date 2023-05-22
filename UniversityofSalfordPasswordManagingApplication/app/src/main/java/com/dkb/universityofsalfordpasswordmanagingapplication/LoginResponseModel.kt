package com.dkb.universityofsalfordpasswordmanagingapplication

data class LoginResponseModel (
    val userID: String,
    val accessToken: String,
    val refreshToken: String,
    val error: String
)
