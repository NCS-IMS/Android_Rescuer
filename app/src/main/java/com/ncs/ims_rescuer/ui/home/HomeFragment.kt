package com.ncs.ims_rescuer.ui.home

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
import com.ncs.ims_rescuer.R
import com.ncs.ims_rescuer.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeBinding: FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        homeBinding.homeViewModel = homeViewModel
        homeBinding.lifecycleOwner = this

        homeViewModel.userName.observe(viewLifecycleOwner, {
            homeBinding.userName.text = it
        })

        homeViewModel.userImg.observe(viewLifecycleOwner, {
            Glide.with(this).load(it).into(homeBinding.userImg)
        })

        return homeBinding.root
    }
}