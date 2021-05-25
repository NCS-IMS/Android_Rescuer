package com.ncs.ims_rescuer.HTTPManager.DTOManager

import com.google.gson.annotations.SerializedName

data class CurrentLocationDTO(
    @SerializedName("documents")
    var documents : List<CurrentLocationData>
)

data class CurrentLocationData(
    @SerializedName("address")
    var address : AddressData
)

data class AddressData(
    @SerializedName("address_name")
    var address_name : String
)
