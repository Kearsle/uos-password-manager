package com.dkb.universityofsalfordpasswordmanagingapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordViewModel : ViewModel() {
    val passwordsLiveModel = MutableLiveData<PasswordModel>()
    val passwordEditLiveModel = MutableLiveData<PasswordEditModel>()

    init {
        passwordsLiveModel.value = PasswordModel()
        passwordEditLiveModel.value = PasswordEditModel("","","","")
    }
}