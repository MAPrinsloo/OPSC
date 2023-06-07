package com.example.clockedout

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clockedout.databinding.ActivityGoalsBinding

class Goals : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //----------------------------------------------------------------------------------------//
        //Binding
        val binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var minHours: Int = 0
        var maxHours: Int = 0

        //----------------------------------------------------------------------------------------//
        //Set Goals Button
        binding.btnSetGoals.setOnClickListener()
        {
            try {
                minHours = binding.etMinHours.text.toString().toInt()
                maxHours = binding.etMaxHours.text.toString().toInt()
                //Data Validation
                if (minHours > 0 && maxHours > 0)
                {
                    captureGoals(minHours, maxHours)
                }
                if (minHours <= 0)
                {
                    binding.etMinHours.error = "Must be greater than 0"
                }
                if (maxHours <= 0)
                {
                    binding.etMaxHours.error = "Must be greater than 0"
                }
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }

        }
    }
    //--------------------------------------------------------------------------------------------//
    //Shared preferences for persisting data --v
    //https://youtu.be/fJEFZ6EOM9o
    //Used to Capture the goals
    private fun captureGoals(minHours: Int, maxHours: Int)
    {
        val sharedPreference =  getSharedPreferences("SharedGoals",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        editor.putInt("MinHours", minHours)
        editor.putInt("MaxHours", maxHours)
        editor.commit()
        Toast.makeText(this,"Goals Set", Toast.LENGTH_SHORT).show()
    }
}