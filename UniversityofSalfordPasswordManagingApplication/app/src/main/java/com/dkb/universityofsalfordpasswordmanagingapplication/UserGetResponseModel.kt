package com.dkb.universityofsalfordpasswordmanagingapplication

data class UserGetResponseModel (
    val error: String,
    val _id: String,
    val username: String,
    val email: String
)