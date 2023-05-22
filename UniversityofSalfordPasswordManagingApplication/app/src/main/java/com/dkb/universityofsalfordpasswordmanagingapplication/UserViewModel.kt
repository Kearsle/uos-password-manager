package com.dkb.universityofsalfordpasswordmanagingapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    val userLiveModel = MutableLiveData<UserModel>()

    init {
        userLiveModel.value = UserModel("","","")
    }
}