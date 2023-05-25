package com.alebit.taikoweb.struct


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("don")
    val don: Don,
    @SerializedName("status")
    val status: String,
    @SerializedName("username")
    val username: String
) {
    data class Don(
        @SerializedName("body_fill")
        val bodyFill: String,
        @SerializedName("face_fill")
        val faceFill: String
    )
}