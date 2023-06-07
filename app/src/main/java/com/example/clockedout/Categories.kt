package com.example.clockedout

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clockedout.databinding.ActivityCategoriesBinding


class Categories : AppCompatActivity() {
    //private val binding = ActivityCategoriesBinding.inflate(layoutInflater)
    private var arrCat = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //----------------------------------------------------------------------------------------//
        //Binding
        val binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Populating spinner
        populateSpinner(binding)

        //----------------------------------------------------------------------------------------//
        //Create Button
        binding.btnCreate.setOnClickListener()
        {
            try {
                var category : String = binding.etCatName.text.toString()
                if (category != "")
                {
                    capture(category)
                    populateSpinner(binding)
                }
                else
                {
                    binding.etCatName.error = "Please enter a Category"
                }
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
                binding.etCatName.error = "Please enter a Category"
            }
        }
        //----------------------------------------------------------------------------------------//
        //Delete Button
        binding.btnDelete.setOnClickListener()
        {
            try {
                val selectedCategory: String = binding.spCategories.selectedItem.toString()
                delete(selectedCategory);
                populateSpinner(binding)
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }
        }
    }
    //----------------------------------------------------------------------------------------//
    //Capture Method
    private fun capture(Category: String)
    {
        //SharedCatCounter is used as an ID for sharedPreference keys.
        val sharedCategoryCounter = getSharedPreferences("SharedCatCounter",Context.MODE_PRIVATE)
        var counterEditor = sharedCategoryCounter.edit()
        var counter : Int = sharedCategoryCounter.getInt("Counter",0).toInt()
        counter += 1
        counterEditor.putInt("Counter", counter)
        counterEditor.commit()
        //Adding the category entered into the sharedpreference.
        val sharedPreference =  getSharedPreferences("SharedCategories",Context.MODE_PRIVATE)
        var catEditor = sharedPreference.edit()
        catEditor.putString(counter.toString(), Category)
        catEditor.commit()
        arrCat.clear()
        Toast.makeText(this,"Category Captured", Toast.LENGTH_SHORT).show()
    }
    //----------------------------------------------------------------------------------------//
    //Delete Method
    private fun delete(itemToDelete: String)
    {
        val sharedPreference =  getSharedPreferences("SharedCategories",Context.MODE_PRIVATE)
        var catEditor = sharedPreference.edit()
        var counter: Int = 0
        //Clearing the SharedCategories
        catEditor.clear()
        catEditor.commit()
        //Repopulating SharedCategories using the array of categories
        for (category in arrCat)
        {
            if(!category.equals(itemToDelete))
            {
                counter += 1
                catEditor.putString(counter.toString(),category)
                catEditor.commit()
            }
        }
        arrCat.clear()
        val sharedCategoryCounter = getSharedPreferences("SharedCatCounter",Context.MODE_PRIVATE)
        var counterEditor = sharedCategoryCounter.edit()
        counterEditor.putInt("Counter", counter)
        counterEditor.commit()
        //Need to alter the shared preferences
        repopulateArrays(itemToDelete)
        Toast.makeText(this,"Category Deleted", Toast.LENGTH_SHORT).show()
    }
    //----------------------------------------------------------------------------------------//
    //Handles the repopulation after category is deleted.
    private fun repopulateArrays(categoryToDelete: String) {
        //SharedPreferences
        val sharedTimeSheetItem = getSharedPreferences("SharedTimesheetItem", Context.MODE_PRIVATE)
        val sharedTimesheetCategory = getSharedPreferences("SharedTimesheetCategory", Context.MODE_PRIVATE)
        val sharedTimesheetDate = getSharedPreferences("SharedTimesheetDate", Context.MODE_PRIVATE)
        val sharedTimesheetStartTime = getSharedPreferences("SharedTimesheetStartTime", Context.MODE_PRIVATE)
        val sharedTimesheetEndTime = getSharedPreferences("SharedTimesheetEndTime", Context.MODE_PRIVATE)
        val sharedTimesheetImage = getSharedPreferences("SharedTimesheetImage", Context.MODE_PRIVATE)
        //Accompanying editors.
        val timesheetItemEditor = sharedTimeSheetItem.edit()
        val timesheetCategoryEditor = sharedTimesheetCategory.edit()
        val timesheetDateEditor = sharedTimesheetDate.edit()
        val timesheetStartTimeEditor = sharedTimesheetStartTime.edit()
        val timesheetEndTimeEditor = sharedTimesheetEndTime.edit()
        val timesheetImageEditor = sharedTimesheetImage.edit()
        //Iterates through timesheet items and removes items accordingly.
        val allTimeSheetEntries: Map<String, *> = sharedTimeSheetItem.all
        for ((key, value) in allTimeSheetEntries) {
            if (sharedTimesheetCategory.getString(key, "") == categoryToDelete) {
                timesheetItemEditor.remove(key)
                timesheetCategoryEditor.remove(key)
                timesheetDateEditor.remove(key)
                timesheetStartTimeEditor.remove(key)
                timesheetEndTimeEditor.remove(key)
                timesheetImageEditor.remove(key)
            }
        }
        //Applying all the changes made.
        timesheetItemEditor.apply()
        timesheetCategoryEditor.apply()
        timesheetDateEditor.apply()
        timesheetStartTimeEditor.apply()
        timesheetEndTimeEditor.apply()
        timesheetImageEditor.apply()
    }
    //----------------------------------------------------------------------------------------//
    //Populates the spinner with Categories
    private  fun populateSpinner(binding: ActivityCategoriesBinding)
    {
        //Populating the categories array
        val sharedPreferences = getSharedPreferences("SharedCategories", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((key, value) in allEntries) {
            arrCat.add(value.toString())
        }
        //Populating the spinner using categories array
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCat)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategories.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}

