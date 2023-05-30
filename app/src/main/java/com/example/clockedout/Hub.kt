package com.example.clockedout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Hub : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)
        //
        val btnCategories = findViewById<Button>(R.id.btnCategories)
        val btnTimesheet = findViewById<Button>(R.id.btnTimesheet)
        btnTimesheet.setOnClickListener()
        {
            val intent = Intent(this, Timesheet::class.java)
            startActivity(intent)
        }
        btnCategories.setOnClickListener()
        {
            val intent = Intent(this, Categories::class.java)
            startActivity(intent)
        }
    }
}