package com.project.suitcase.views.ui.activity.profile

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityProfileEditBinding
import com.project.suitcase.views.viewmodel.UserProfileUiState
import com.project.suitcase.views.viewmodel.UserProfileViewModel
import com.project.suitcase.views.viewmodel.UserProfileViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileEditActivity : AppCompatActivity() {
    private val viewModel: UserProfileViewModel by viewModel()

    private var _binding: ActivityProfileEditBinding? = null
    private val binding get()= _binding!!

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdpateProfile.setOnClickListener {
            val name = binding.edtUserName.text.toString()
            val phoneNumber = binding.edtUserPhoneNumber.text.toString()

            viewModel.updateUserInfo(
                name = name,
                phoneNumber = phoneNumber,
                userImage = "",
            )
        }

        viewModel.uiState.observe(this){ state ->
            when(state){
                UserProfileUiState.Loading -> {
                    binding.layoutContent.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UserProfileUiState.Success -> {
                    binding.layoutContent.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.INVISIBLE

                    binding.edtUserName.setText(state.userDetail.name)
                    binding.edtUserPhoneNumber.setText(state.userDetail.phoneNumber)
                    if (!state.userDetail.userImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(state.userDetail.userImage)
                            .into(binding.ivUserImage)
                    } else {
                        binding.ivUserImage.setImageResource(R.drawable.user_profile_image)
                    }
                }

                UserProfileUiState.UpdateSuccess -> {
                    finish()
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