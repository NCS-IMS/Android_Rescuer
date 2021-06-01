package com.ncs.ims_rescuer.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ncs.imsUser.SaveDataManager.UserInfoData
import com.ncs.ims_rescuer.GISManager.GetMylocation
import com.ncs.ims_rescuer.MainActivity
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.SaveDataManager.ApplicationSetting
import com.ncs.ims_rescuer.databinding.FragmentHomeBinding
import com.ncs.ims_rescuer.ui.login.LoginActivity
import com.nhn.android.naverlogin.OAuthLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    lateinit var oAuthLogin : OAuthLogin

    private val appSetting by lazy {
        ApplicationSetting(requireContext())
    }
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        homeBinding.homeViewModel = homeViewModel
        homeBinding.lifecycleOwner = this

        oAuthLogin = OAuthLogin.getInstance()

        homeViewModel.userName.observe(viewLifecycleOwner, {
            homeBinding.userName.text = it
        })
        homeViewModel.userImg.observe(viewLifecycleOwner, {
            Glide.with(this).load(it).into(homeBinding.userImg)
        })
        homeBinding.logoutBtn.setOnClickListener(this)
        //gps.getLocation(requireContext())["longitude"]!!, gps.getLocation(requireContext())["longitude"]!!

        enrollUser()
        return homeBinding.root
    }

    fun enrollUser(){
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.Main).launch{
                homeViewModel.setUserInfo(appSetting.getSetting()["fcm"]!!, appSetting.getSetting()["uuid"]!!)
                    .observe(viewLifecycleOwner, {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        appSetting.setFirstCheck(false)
                    })
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            homeBinding.logoutBtn.id ->{
                oAuthLogin.logout(requireContext())
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}