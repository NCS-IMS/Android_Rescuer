package com.ncs.ims_rescuer.HTTPManager.DTOManager

import com.google.gson.annotations.SerializedName

data class NotificationDTO(
        @SerializedName("message")
        var message : String,
        @SerializedName("result")
        var result : NotificationData
)

data class NotificationData(
        @SerializedName("id")
        var id : String,
        @SerializedName("kakaoId")
        var kakaoId : String,
        @SerializedName("state")
        var state : String,
        @SerializedName("isSelf")
        var isSelf : String,
        @SerializedName("latitude")
        var latitude : String,
        @SerializedName("longitude")
        var longitude : String,
        @SerializedName("userAddr")
        var userAddr : String,
        @SerializedName("emAddr")
        var emAddr : String,
        @SerializedName("anamnesis")
        var anamnesis : String,
        @SerializedName("medicine")
        var medicine : String,
        @SerializedName("createDate")
        var createDate : String,
        @SerializedName("updateDate")
        var updateDate : String
)
