package com.ncs.ims_rescuer.HTTPManager.RepositoryManager

import android.app.Application
import android.app.DownloadManager
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.HTTPManager.DTOManager.PublicDTO
import com.ncs.ims_rescuer.HTTPManager.RetrofitAPI
import com.ncs.ims_rescuer.HTTPManager.RetrofitInterface
import com.ncs.ims_rescuer.HTTPManager.Tools
import com.ncs.ims_rescuer.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream

class MainRepository{

    private val retrofit : Retrofit = RetrofitAPI.getInstance(Tools().EMERGENCY_URL)
    private val service = retrofit.create(RetrofitInterface::class.java)


    fun updateImage(bitmap: Bitmap, id: String):LiveData<String>{
        val message =  MutableLiveData<String>()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        var requestBody = RequestBody.create(MediaType.parse("image/jpg"), byteArrayOutputStream.toByteArray())
        var uploadImg = MultipartBody.Part.createFormData("profile_image", "${id}", requestBody)

        val kakaoId = RequestBody.create(MediaType.parse("text/plain"), id)
        var filename : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), id)
        var info = hashMapOf(
            "kakaoId" to kakaoId,
            "filename" to filename
        )
        service.updateImage(uploadImg, info).enqueue(object : Callback<PublicDTO>{
            override fun onResponse(call: Call<PublicDTO>, response: Response<PublicDTO>) {
                message.value = "${Tools().EMERGENCY_URL}/user/${id}.jpg"
            }

            override fun onFailure(call: Call<PublicDTO>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
        return message
    }
}