package com.dkb.universityofsalfordpasswordmanagingapplication

import retrofit2.Call
import retrofit2.http.*

interface API {

    // USERS

    @POST("user")
    fun getUser(
        @Header("Authorization") authHeader: String
    ): Call<UserGetResponseModel>

    @POST("createUser")
    fun createUser(
        @Body registerInfo: RegisterRequestModel
    ): Call<RegisterResponseModel>

    @POST("userLogin")
    fun userLogin(
        @Body loginInfo: LoginRequestModel
    ): Call<LoginResponseModel>

    @HTTP(method = "DELETE", path = "userLogout", hasBody = true)
    fun userLogout(
        @Header("Authorization") authHeader: String,
        @Body logoutInfo: LogoutRequestModel
    ): Call<LogoutResponseModel>

    @HTTP(method = "DELETE", path = "userLogoutAll")
    fun userLogoutAll(
        @Header("Authorization") authHeader: String
    ): Call<LogoutAllResponseModel>

    @HTTP(method = "DELETE", path = "userDelete")
    fun userDelete(
        @Header("Authorization") authHeader: String
    ): Call<UserDeleteResponseModel>

    @PUT("userPasswordReset")
    fun userPasswordReset(
        @Header("Authorization") authHeader: String,
        @Body passwordInfo: PasswordResetRequestModel
    ): Call<PasswordResetResponseModel>

    // PASSWORDS

    @GET("passwords")
    fun passwords(
        @Header("Authorization") authHeader: String
    ): Call<List<PasswordsResponseModelItem>>

    @PUT("passwords/edit")
    fun passwordsEdit(
        @Header("Authorization") authHeader: String,
        @Body passwordInfo: PasswordEditRequestModel
    ): Call<PasswordEditResponseModel>

    @POST("passwords/create")
    fun passwordsCreate(
        @Header("authorization") authHeader: String,
        @Body passwordInfo: PasswordCreateRequestModel
    ): Call<PasswordCreateResponseModel>

    @HTTP(method = "DELETE", path = "passwords/delete", hasBody = true)
    fun passwordsDelete(
        @Header("Authorization") authHeader: String,
        @Body passwordInfo: PasswordDeleteRequestModel
    ): Call<PasswordDeleteResponseModel>

    @HTTP(method = "DELETE", path = "passwords/deleteAll")
    fun passwordsDeleteAll(
        @Header("Authorization") authHeader: String
    ): Call<PasswordDeleteResponseModel>

    // RandomWords
    @GET("generateThreeRandomWords")
    fun generateThreeRandomWords(
        @Header("Authorization") authHeader: String
    ): Call<GenerateThreeRandomWordsResponseModelItem>

    // token
    @POST("token")
    fun token(
        @Body tokenInfo: tokenRequestModel
    ): Call<tokenResponseModel>
}