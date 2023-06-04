package com.example.clockedout

import android.content.Context
import android.content.Intent
import android.graphics.Paint.Cap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clockedout.databinding.ActivityGoalsBinding
import com.example.clockedout.databinding.ActivityMainBinding

class Goals : AppCompatActivity() {
    //Static variables to be called elsewhere
    //https://marketsplash.com/tutorials/kotlin/kotlin-static-variable/#link1
    /*
    companion object {
        var MinHours: Int = 0
        var MaxHours: Int = 0
        public fun InitializeGoals ()
        {
            MinHours = g
        }
    }
     */
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
                if (minHours > 0 && maxHours > 0)
                {
                    CaptureGoals(minHours, maxHours)
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

    private fun CaptureGoals(minHours: Int, maxHours: Int)
    {
        //Shared preferences for persisting data --v
        //https://youtu.be/fJEFZ6EOM9o


        val sharedPreference =  getSharedPreferences("SharedPrefs",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        editor.putInt("MinHours", minHours)
        editor.putInt("MaxHours", maxHours)
        editor.commit()
        Toast.makeText(this,"Goals Set", Toast.LENGTH_SHORT).show()
    }
}