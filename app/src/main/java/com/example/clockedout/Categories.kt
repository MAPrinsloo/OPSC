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
        PopulateSpinner(binding)

        //----------------------------------------------------------------------------------------//
        //Create Button
        binding.btnCreate.setOnClickListener()
        {
            try {
                var Category : String = binding.etCatName.text.toString()
                Capture(Category)
                PopulateSpinner(binding)
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }
        }
        //----------------------------------------------------------------------------------------//
        //Delete Button
        binding.btnDelete.setOnClickListener()
        {
            try {
                val selectedCategory: String = binding.spCategories.selectedItem.toString()
                Delete(selectedCategory);
                PopulateSpinner(binding)
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }
        }
    }
    //----------------------------------------------------------------------------------------//
    private fun Capture(Category: String)
    {
        //
        val sharedCategoryCounter = getSharedPreferences("SharedCatCounter",Context.MODE_PRIVATE)
        var counterEditor = sharedCategoryCounter.edit()
        var counter : Int = sharedCategoryCounter.getInt("Counter",0).toInt()
        counter += 1
        counterEditor.putInt("Counter", counter)
        counterEditor.commit()
        //
        val sharedPreference =  getSharedPreferences("SharedCategories",Context.MODE_PRIVATE)
        var catEditor = sharedPreference.edit()
        catEditor.putString(counter.toString(), Category)
        catEditor.commit()
        Toast.makeText(this,"Category Captured", Toast.LENGTH_SHORT).show()
    }

    private fun Delete(itemToDelete: String)
    {
        val sharedPreference =  getSharedPreferences("SharedCategories",Context.MODE_PRIVATE)
        var catEditor = sharedPreference.edit()
        var counter: Int = 0

        catEditor.clear()
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
    }

    private  fun PopulateSpinner(binding: ActivityCategoriesBinding)
    {
        val sharedPreferences = getSharedPreferences("SharedCategories", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((key, value) in allEntries) {
            arrCat.add(value.toString())
        }
        //Populating the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCat)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategories.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}

