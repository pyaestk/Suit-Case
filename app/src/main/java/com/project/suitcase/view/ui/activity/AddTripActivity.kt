package com.project.suitcase.view.ui.activity

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.suitcase.databinding.ActivityAddTripBinding
import com.project.suitcase.view.viewmodel.TripUiState
import com.project.suitcase.view.viewmodel.TripViewModel
import com.project.suitcase.view.viewmodel.TripViewModelEvent
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AddTripActivity : AppCompatActivity() {

    private var binding: ActivityAddTripBinding? = null
    private val calendar = Calendar.getInstance()

    private val viewModel: TripViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.edtTripDate?.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, { DatePicker, year: Int, monthOfYear:Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    binding?.edtTripDate?.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )
            datePickerDialog.show()
        }

        binding?.btnSaveTrip?.setOnClickListener {
            viewModel.addTrip(
                tripName = binding?.edtTripName?.text.toString(),
                date = binding?.edtTripDate?.text.toString()
            )
        }
        binding?.btnBack?.setOnClickListener {
            finish()
        }


        tirpViewModelSetup()

    }

    private fun tirpViewModelSetup() {
        viewModel.addTripUiEvent.observe(this) { event ->
            when(event){
                is TripViewModelEvent.Error -> {
                    Toast.makeText(
                        this, event.error, Toast.LENGTH_SHORT
                    ).show()
                }
                is TripViewModelEvent.Success -> {
                    Toast.makeText(
                        this, "Success", Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        viewModel.uiState.observe(this) { event ->
            when(event) {
                TripUiState.Loading -> {
                    Toast.makeText(
                        this, "Loading", Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }
}