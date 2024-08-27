package com.project.suitcase.view.ui.activity.profile

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityProfileViewBinding
import com.project.suitcase.view.viewmodel.UserProfileUiState
import com.project.suitcase.view.viewmodel.UserProfileViewModel
import com.project.suitcase.view.viewmodel.UserProfileViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileViewActivity : AppCompatActivity() {

    private val viewModel: UserProfileViewModel by viewModel()

    private var binding: ActivityProfileViewBinding ?= null

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileViewBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        binding?.btnBack?.setOnClickListener {
            finish()
        }
        viewModel.uiState.observe(this) { state ->
            when(state) {
                UserProfileUiState.Loading -> {
                    binding?.layoutContent?.visibility = View.INVISIBLE
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }

        viewModel.userProfileEvent.observe(this) { event ->
            when(event) {
                is UserProfileViewModelEvent.Error -> {
                    Log.e("Setting Fragment", event.error)
                }
                is UserProfileViewModelEvent.Success -> {
                    binding?.layoutContent?.visibility = View.VISIBLE
                    binding?.progressBar?.visibility = View.INVISIBLE

                    binding?.edtUserEmail?.setText(event.userDetail.email)
                    binding?.edtUserName?.setText(event.userDetail.name)
                    binding?.edtUserPhoneNumber?.setText(event.userDetail.phoneNumber)
                    if (!event.userDetail.userImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(event.userDetail.userImage)
                            .into(binding?.ivUserImage!!)
                    } else {
                        binding?.ivUserImage?.setImageResource(R.drawable.user_profile_image)
                    }
                }
            }
        }




    }
}