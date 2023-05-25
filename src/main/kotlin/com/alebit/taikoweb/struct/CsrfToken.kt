package com.alebit.taikoweb.struct


import com.google.gson.annotations.SerializedName

data class CsrfToken(
    @SerializedName("status")
    val status: String,
    @SerializedName("token")
    val token: String
)