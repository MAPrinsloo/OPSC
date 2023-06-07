package com.example.clockedout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.clockedout.databinding.ActivityHubBinding
import com.example.clockedout.databinding.ActivityProgressBinding

class Progress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        //Binding
        val binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this, "To be implemented.", Toast.LENGTH_SHORT).show()
    }
}