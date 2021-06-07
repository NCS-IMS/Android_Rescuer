package com.ncs.imsUser.SaveDataManager

import android.content.Context
import android.util.Log
import com.pddstudio.preferences.encrypted.EncryptedPreferences

class UserInfoData(context: Context) {
    var userData : EncryptedPreferences = EncryptedPreferences.Builder(context)
        .withEncryptionPassword("user")
        .build()
    var editor = userData.edit()

    fun setUserID(userID : String):Boolean{
        editor.putString("USER_ID",userID)
        return editor.commit()
    }
    fun setName(name : String):Boolean{
        editor.putString("NAME",name)
        return editor.commit()
    }
    fun setPhone(phone : String):Boolean{
        editor.putString("PHONE",phone)
        return editor.commit()
    }
    fun setEmail(email : String):Boolean{
        editor.putString("EMAIL",email)
        return editor.commit()
    }

    fun setGender(gender : String):Boolean{
        editor.putString("GENDER",gender)
        return editor.commit()
    }
    fun setBirthday(birthday : String):Boolean{
        editor.putString("BIRTHDAY",birthday)
        return editor.commit()
    }
    fun setBirthYear(birthyear : String):Boolean{
        editor.putString("BIRTHYEAR",birthyear)
        return editor.commit()
    }
    fun setProfileImg(imgUrl: String):Boolean{
        editor.putString("IMGURL",imgUrl)
        return editor.commit()
    }
    fun setFireStationId(firestationId: String): Boolean{
        editor.putString("FIREID",firestationId)
        return editor.commit()
    }
    fun setFireStationName(firestationName: String): Boolean{
        editor.putString("FIRENAME",firestationName)
        return editor.commit()
    }
    fun getUserData():HashMap<String, String>{
        val userInfo = HashMap<String, String>()
        userInfo["USER_ID"] = userData.getString("USER_ID", "")
        userInfo["NAME"] = userData.getString("NAME", "")
        userInfo["PHONE"] = userData.getString("PHONE", "")
        userInfo["BIRTHYEAR"] = userData.getString("BIRTHYEAR", "")
        userInfo["BIRTHDAY"] = userData.getString("BIRTHDAY", "")
        userInfo["GENDER"] = userData.getString("GENDER", "")
        userInfo["IMGURL"] = userData.getString("IMGURL", "")
        userInfo["EMAIL"] = userData.getString("EMAIL", "")
        userInfo["FIREID"] = userData.getString("FIREID", "")
        userInfo["FIRENAME"] = userData.getString("FIRENAME", "")
        return userInfo
    }

    fun checkAllDataSave():Boolean{
        var userInfo = getUserData()
        for(value in userInfo.values){
            if(value.isEmpty())
                return false
        }
        return true
    }
}