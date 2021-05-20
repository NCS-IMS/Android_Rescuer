package com.ncs.ims_rescuer.HTTPManager.DTOManager

import com.google.gson.annotations.SerializedName

data class UserInfoDTO(
        @SerializedName("message")
        var message : String,
        @SerializedName("response")
        var response: UserInfoDetail
)

data class UserInfoDetail(
        @SerializedName("id")
        var id: String,
        @SerializedName("profile_image")
        var profile_image: String,
        @SerializedName("gender")
        var gender: String,
        @SerializedName("email")
        var email: String,
        @SerializedName("mobile")
        var mobile: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("birthday")
        var birthday: String,
        @SerializedName("birthyear")
        var birthyear: String
)
