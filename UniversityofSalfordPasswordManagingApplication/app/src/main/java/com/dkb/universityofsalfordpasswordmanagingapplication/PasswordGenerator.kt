package com.dkb.universityofsalfordpasswordmanagingapplication

import android.util.Log

object PasswordGenerator {
    fun random (length: Int, numbers: Boolean, symbols: Boolean, uppercase: Boolean, lowercase: Boolean, ): String {
        var charset = ""
        var generate = false

        if (numbers) {
            charset += "0123456789"
            generate = true
        }
        if (symbols) {
            charset += "?!£$%&*+"
            generate = true
        }
        if (uppercase) {
            charset += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            generate = true
        }
        if (lowercase) {
            charset += "abcdefghiklmnopqrstuvwxyz"
            generate = true
        }

        return if (generate) {
            (1..length)
                .map { charset.random() }
                .joinToString("")
        } else {
            ""
        }
    }
}