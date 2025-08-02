package com.rarestardev.taskora.utilities

import android.annotation.SuppressLint
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

@SuppressLint("GetInstance")
object AES {
    private const val SECRET_KEY = "Symbian0936@Rare"
    private val cipher = Cipher.getInstance("AES")
    private val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(),"AES")


    fun encrypt(input: String) : String{
        cipher.init(Cipher.ENCRYPT_MODE,keySpec)
        val encrypted = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }


    fun decrypt(input: String) : String{
        cipher.init(Cipher.DECRYPT_MODE,keySpec)
        val decoded = Base64.decode(input, Base64.DEFAULT)
        return String(cipher.doFinal(decoded))
    }
}