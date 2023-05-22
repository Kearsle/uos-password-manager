package com.dkb.universityofsalfordpasswordmanagingapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasswordCreateViewModel: ViewModel() {
    val passwordCreateLiveModel = MutableLiveData<PasswordCreateModel>()

    init {
        passwordCreateLiveModel.value = PasswordCreateModel("","","")
    }
}