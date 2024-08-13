package com.project.suitcase.view.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.project.suitcase.R
import com.project.suitcase.databinding.FragmentSettingBinding
import com.project.suitcase.view.ui.activity.auth.LoginActivity
import com.project.suitcase.view.ui.activity.profile.ProfileViewActivity
import com.project.suitcase.view.viewmodel.UserProfileUiState
import com.project.suitcase.view.viewmodel.UserProfileViewModel
import com.project.suitcase.view.viewmodel.UserProfileViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingFragment : Fragment() {

    private var binding: FragmentSettingBinding? = null
    private val viewModel: UserProfileViewModel by viewModel()

    private val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.profileCard?.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileViewActivity::class.java))
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when(state) {
                UserProfileUiState.Loading -> {
                    binding?.layoutProfile?.visibility = View.INVISIBLE
                    binding?.progressBarProfile?.visibility = View.VISIBLE
                }
            }
        }

        viewModel.userProfileEvent.observe(viewLifecycleOwner) { event ->
            when(event) {
                is UserProfileViewModelEvent.Error -> {
                    Log.e("Setting Fragment", event.error)
                }
                is UserProfileViewModelEvent.Success -> {
                    binding?.layoutProfile?.visibility = View.VISIBLE
                    binding?.progressBarProfile?.visibility = View.INVISIBLE

                    binding?.tvUserName?.text = event.userDetail.name
                    if (!event.userDetail.userImage.isNullOrEmpty()){
                        Glide.with(this)
                            .load(event.userDetail.userImage)
                            .into(binding?.ivUser!!)
                    } else {
                        binding?.ivUser?.setImageResource(R.drawable.user_profile_image)
                    }
                }
            }
        }

        binding?.btnLogOut?.setOnClickListener {
            fAuth.signOut()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

}