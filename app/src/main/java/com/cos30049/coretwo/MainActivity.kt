package com.cos30049.coretwo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {



    private lateinit var items: ArrayList<Item>
    private lateinit var itemImageView: ImageView
    private lateinit var itemNameTextView: TextView
    private lateinit var itemRatingBar: RatingBar
    private lateinit var itemPriceTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var borrowButton: Button
    private lateinit var dueDateTextView: TextView

    private var currentItem = 0


    private val borrowActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedItem = result.data?.getParcelableExtra<Item>("updatedItem")
            Log.d("ItemData", "Name: ${updatedItem?.name}, Rating: ${updatedItem?.rating}, Price Per Day: ${updatedItem?.pricePerDay}, Due Back Date: ${updatedItem?.dueBackDate}")

            updatedItem?.let { newItem ->
                val index = items.indexOfFirst { it.name == newItem.name }
                if (index != -1) {
                    // Update the item in the list
                    items[index] = newItem

                    // Refresh the displayed item
                    displayItem(index)

//                    // Save the updated item to the shared preferences
//                    saveItemData(newItem)

                    // Show a success message
                    Toast.makeText(this, "Borrowed successfully!", Toast.LENGTH_SHORT).show()
                }
            }

            Log.d("ActivityResult", "Result data: ${result.data}")

        } else {
//            // Show an error message if the number of days is 0
//            Toast.makeText(this, "Failed to borrow.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (loadItemData().isEmpty()) {
            initDefaultItems()
        }

        items = loadItemData() as ArrayList<Item>
        Log.d("ItemData", "Price Pay: ${items[0].pricePerDay}, Due Back Date: ${items[0].dueBackDate}")



        itemImageView = findViewById(R.id.itemImageView)
        itemNameTextView = findViewById(R.id.itemNameTextView)
        itemRatingBar = findViewById(R.id.itemRatingBar)
        itemPriceTextView = findViewById(R.id.itemPriceTextView)
        nextButton = findViewById(R.id.nextButton)
        borrowButton = findViewById(R.id.borrowButton)
        dueDateTextView = findViewById(R.id.dueDateTextView)

        displayItem(currentItem)


        nextButton.setOnClickListener {
            // Load next item from the data source
            currentItem = (currentItem + 1) % items.size
            displayItem(currentItem)
        }

        borrowButton.setOnClickListener {
            val intent = Intent(this, BorrowActivity::class.java)
            intent.putExtra("item", items[currentItem])
            borrowActivityResultLauncher.launch(intent)
        }
    }


    private fun displayItem(index: Int) {
        val item = items[index]

        val imageViewId = when (item.name) {
            "BMW" -> R.drawable.bmw
            "Audi" -> R.drawable.audi
            "Rolls-Royce" -> R.drawable.rolls_royce
            else -> R.drawable.default_image
        }
        itemImageView.setImageResource(imageViewId)


        itemNameTextView.text = item.name
        itemRatingBar.rating = item.rating
        itemPriceTextView.text = getString(R.string.item_price, item.pricePerDay)


        // Check if dueBackDate is not null
        if (item.dueBackDate != "null") {
            // Display dueBackDate
            dueDateTextView.text = getString(R.string.due_back_date, item.dueBackDate)
            dueDateTextView.visibility = View.VISIBLE
        } else {
            // Hide the dueDateTextView if dueBackDate is null
            dueDateTextView.visibility = View.GONE
        }
    }

    private fun saveItemData(item: Item) {
        val sharedPreferences = getSharedPreferences("ItemData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Check if the item already exists in the shared preferences
        val existingItems = loadItemData().toMutableList()
        val existingItemIndex = existingItems.indexOfFirst { it.name == item.name }

        if (existingItemIndex != -1) {
            // Update the existing item
            val existingItem = existingItems[existingItemIndex]
            existingItem.dueBackDate = item.dueBackDate
            existingItems[existingItemIndex] = existingItem

            // Save the updated item to the shared preferences
            editor.putString("name$existingItemIndex", existingItem.name)
            editor.putFloat("rating$existingItemIndex", existingItem.rating)
            editor.putInt("pricePerDay$existingItemIndex", existingItem.pricePerDay)
            editor.putString("dueBackDate$existingItemIndex", existingItem.dueBackDate.toString())
        } else {
            // Add a new item to the shared preferences
            editor.putString("name${existingItems.size}", item.name)
            editor.putFloat("rating${existingItems.size}", item.rating)
            editor.putInt("pricePerDay${existingItems.size}", item.pricePerDay)
            editor.putString("dueBackDate${existingItems.size}", item.dueBackDate.toString())
        }

        editor.apply()
    }


    private fun loadItemData(): List<Item> {
        val sharedPreferences = getSharedPreferences("ItemData", Context.MODE_PRIVATE)

        val itemList = mutableListOf<Item>()
        for (i in 0..2) {
            val name = sharedPreferences.getString("name$i", "")
            val rating = sharedPreferences.getFloat("rating$i", 0f)
            val pricePerDay = sharedPreferences.getInt("pricePerDay$i", 0)
            val dueBackDate = sharedPreferences.getString("dueBackDate$i", "")

            if (name!!.isNotEmpty() && rating != 0f && pricePerDay != 0) {
                val item = Item(name, rating, pricePerDay, dueBackDate)
                itemList.add(item)
            }

        }

        return itemList
    }

    private fun initDefaultItems() {
        val sharedPreferences = getSharedPreferences("ItemData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val defaultItems = arrayListOf(
            Item("BMW", 4f,  20),
            Item("Rolls-Royce", 3.5f, 25),
            Item("Audi", 4.7f,10)
        )

        for (i in 0 until defaultItems.size) {
            val item = defaultItems[i]
            editor.putString("name$i", item.name)
            editor.putFloat("rating$i", item.rating)
            editor.putInt("pricePerDay$i", item.pricePerDay)
            editor.putString("dueBackDate$i", item.dueBackDate.toString())
        }

        editor.apply()
    }


}