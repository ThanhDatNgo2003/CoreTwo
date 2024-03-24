package com.cos30049.coretwo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class BorrowActivity : AppCompatActivity() {
    private lateinit var numberOfDaysTextView: TextView
    private lateinit var numberOfDaysSlider: Slider
    private lateinit var calculatedPriceTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow)

        numberOfDaysTextView = findViewById(R.id.numberOfDaysTextView)
        numberOfDaysSlider = findViewById(R.id.numberOfDaysSlider)
        calculatedPriceTextView = findViewById(R.id.calculatedPriceTextView)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        val item = intent.getParcelableExtra<Item>("item")

        // Set step size to 1 and disable label formatter
        numberOfDaysSlider.stepSize = 1f
        numberOfDaysSlider.setLabelFormatter(null)

        numberOfDaysSlider.addOnChangeListener { _, value, _ ->
            val intValue = value.toInt()
            val totalPrice = item?.pricePerDay?.times(intValue)
            calculatedPriceTextView.text = getString(R.string.total_price_format, totalPrice)
        }

        saveButton.setOnClickListener {
            // Logic to save booking details and return to MainActivity
            val borrowedDays = numberOfDaysSlider.value.toInt()
            if (borrowedDays == 0) {
                // Show an error message if the number of days is 0
                Toast.makeText(this, "Failed to borrow. Please select a valid number of days.", Toast.LENGTH_SHORT).show()
            } else {
                // Calculate due back date by adding borrowed days to current date
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_MONTH, borrowedDays)
                val dueBackDate = calendar.time

                // Format due back date to yyyy-MM-dd format
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDueBackDate = dateFormat.format(dueBackDate)

                // Log item data and due back date
                Log.d("BorrowActivity", "Borrowed Item: $item")
                Log.d("BorrowActivity", "Due Back Date: $formattedDueBackDate")

                item?.dueBackDate = formattedDueBackDate

                // Pass the updated item back to MainActivity
                val intent = Intent()
                intent.putExtra("updatedItem", item)
                setResult(Activity.RESULT_OK, intent)
                finish()}
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
