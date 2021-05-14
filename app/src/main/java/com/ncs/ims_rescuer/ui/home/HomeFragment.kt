package com.ncs.ims_rescuer.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ncs.ims_rescuer.MainActivity
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentHomeBinding
import com.ncs.ims_rescuer.ui.login.LoginActivity
import com.nhn.android.naverlogin.OAuthLogin

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    lateinit var oAuthLogin : OAuthLogin
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

        return homeBinding.root
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