package com.ncs.ims_rescuer.HTTPManager.DTOManager

data class UserInfoDTO(
        var message : String,
        var response: UserInfoDetail
)

data class UserInfoDetail(
        var id: String,
        var profile_image: String,
        var gender: String,
        var email: String,
        var mobile: String,
        var name: String,
        var birthday: String,
        var birthyear: String
)
