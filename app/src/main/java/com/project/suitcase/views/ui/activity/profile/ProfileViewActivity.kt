package com.project.suitcase.views.ui.activity.profile

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityProfileViewBinding
import com.project.suitcase.views.viewmodel.UserProfileUiState
import com.project.suitcase.views.viewmodel.UserProfileViewModel
import com.project.suitcase.views.viewmodel.UserProfileViewModelEvent
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

        binding?.btnItemEdit2?.setOnClickListener{
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        viewModel.uiState.observe(this) { state ->
            when(state) {
                UserProfileUiState.Loading -> {
                    binding?.layoutContent?.visibility = View.INVISIBLE
                    binding?.progressBar?.visibility = View.VISIBLE
                }

                is UserProfileUiState.Success -> {
                    binding?.layoutContent?.visibility = View.VISIBLE
                    binding?.progressBar?.visibility = View.INVISIBLE

                    binding?.edtUserEmail?.setText(state.userDetail.email)
                    binding?.edtUserName?.setText(state.userDetail.name)
                    binding?.edtUserPhoneNumber?.setText(state.userDetail.phoneNumber)
                    if (!state.userDetail.userImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(state.userDetail.userImage)
                            .into(binding?.ivUserImage!!)
                    } else {
                        binding?.ivUserImage?.setImageResource(R.drawable.user_profile_image)
                    }
                }

                UserProfileUiState.UpdateSuccess -> {

                }
            }
        }

        viewModel.userProfileEvent.observe(this) { event ->
            when(event) {
                is UserProfileViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
            }
        }




    }
}