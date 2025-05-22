package com.project.suitcase.views.ui.activity.profile

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var imageUri: Uri? = null
    private var userImage: String? = null

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        registerActivityForResult()

        userImage = intent.getStringExtra("userImage")

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdpateProfile.setOnClickListener {
            val name = binding.edtUserName.text.toString()
            val phoneNumber = binding.edtUserPhoneNumber.text.toString()

            if (name.isEmpty()){
                binding.textInputLayoutUserName.error = "Username cannot be blank"
            } else {
                viewModel.updateUserInfo(
                    name = name,
                    phoneNumber = phoneNumber,
                    userImage = imageUri,
                )
            }
        }

        binding.fabProfileImage.setOnClickListener {
            chooseImage()
        }

        observerViewModel()
    }

    private fun observerViewModel() {
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
                }

                UserProfileUiState.UpdateSuccess -> {
                    finish()
                }
            }
        }

        if (userImage.isNullOrEmpty()) {
            binding.ivUserImage.setImageResource(R.drawable.user_profile_image)
        } else {
            Glide.with(this)
                .load(userImage)
                .into(binding.ivUserImage)
            binding.ivUserImage.setBackgroundResource(R.color.white)
        }

        viewModel.userProfileEvent.observe(this) { event ->
            when(event) {
                is UserProfileViewModelEvent.Error -> {
                    Toast.makeText(this, event.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerActivityForResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imageData = result.data
                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Glide.with(this)
                            .load(it)
                            .into(binding.ivUserImage)
                        binding.ivUserImage.setBackgroundResource(R.color.white)
                    }
                }
            }
        )
    }

    private fun chooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }
}