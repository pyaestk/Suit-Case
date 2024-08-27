package com.project.suitcase.view.ui.activity.trip

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.project.suitcase.R
import com.project.suitcase.databinding.ActivityAddTripBinding
import com.project.suitcase.view.viewmodel.AddTripUiState
import com.project.suitcase.view.viewmodel.AddTripViewModel
import com.project.suitcase.view.viewmodel.AddTripViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AddTripActivity : AppCompatActivity() {

    private var binding: ActivityAddTripBinding? = null

    private val addTripViewModel: AddTripViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        binding?.edtTripDate?.setOnClickListener {
            // Set constraints for the date picker
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Trip Date")
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Format the selected date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selection)
                binding?.edtTripDate?.setText(formattedDate)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        binding?.btnSaveTrip?.setOnClickListener {
            addTripViewModel.addTrip(
                tripName = binding?.edtTripName?.text.toString(),
                date = binding?.edtTripDate?.text.toString()
            )
        }
        binding?.btnBack?.setOnClickListener {
            finish()
        }


        tripViewModelSetup()

    }

    private fun tripViewModelSetup() {
        addTripViewModel.uiState.observe(this) {
            when(it) {
                AddTripUiState.Loading -> {

                }
            }
        }
        addTripViewModel.addTripUiEvent.observe(this) {
            when(it){
                is AddTripViewModelEvent.Error -> {
                    Toast.makeText(
                        this, it.error, Toast.LENGTH_SHORT
                    ).show()
                }
                is AddTripViewModelEvent.Success -> {
                    Toast.makeText(
                        this, "Trip Added Successfully", Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }
}