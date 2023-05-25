package com.alebit.taikoweb.struct


import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("password")
    val password: String,
    @SerializedName("remember")
    val remember: Boolean,
    @SerializedName("username")
    val username: String
)