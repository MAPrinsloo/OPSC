package com.example.clockedout

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class Categories : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        val spCategories = findViewById<Spinner>(R.id.spCategories)
        //
        val arrCategories = arrayOf<String>("Categories", "SampleCat1", "SampleCat2")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategories.adapter = adapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}