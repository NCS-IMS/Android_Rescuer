package com.ncs.ims_rescuer.HTTPManager.DTOManager

import com.google.gson.annotations.SerializedName

data class FireStationDTO(
    @SerializedName("message")
    var message : String,
    @SerializedName("result")
    var result : List<FireStationData>
)

data class FireStationData(
    @SerializedName("address_name")
    var address_name : String,
    @SerializedName("distance")
    var distance : String,
    @SerializedName("id")
    var id : String,
    @SerializedName("phone")
    var phone : String,
    @SerializedName("place_name")
    var place_name : String,
    @SerializedName("road_address_name")
    var road_address_name : String,
    @SerializedName("x")
    var x : String,
    @SerializedName("y")
    var y : String,
)
