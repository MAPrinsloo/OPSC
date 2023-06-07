package com.example.clockedout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.clockedout.databinding.ActivityTimesheetBinding
import java.io.ByteArrayOutputStream
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

import android.util.Base64

class Timesheet : AppCompatActivity() {
    private var arrCat = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Binding
        val binding = ActivityTimesheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Populate Spinner
        populateSpinner(binding)
        //Module Manual
        val getResult = registerForActivityResult( ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == Activity.RESULT_OK && it.data != null){
                var bitmap = it.data!!.extras?.get("data") as Bitmap
                binding.ibtnAddImage.setImageBitmap(bitmap)
            }
        }
        //----------------------------------------------------------------------------------------//
        //btnClock click
        binding.btnClock.setOnClickListener()
        {
            //https://www.baeldung.com/kotlin/string-to-date
            try {
                var requiredFieldsFilled = true
                val etDate = binding.etDate.text.toString()
                val etStartTime = binding.etStartTime.text.toString()
                val etEndTime = binding.etEndTime.text.toString()
                val etCategory = binding.spCategories.selectedItem.toString()
                val etDescription = binding.etDescription.text.toString()
                val image = binding.ibtnAddImage.drawable.toBitmap()
                if (etDate.isEmpty())
                {
                    binding.etDate.error = "enter a date dd/MM/yyyy."
                    requiredFieldsFilled = false
                }
                if (etStartTime.isEmpty())
                {
                    binding.etStartTime.error = "please enter a start time eg. 13:00"
                    requiredFieldsFilled = false
                }
                if (etEndTime.isEmpty())
                {
                    binding.etEndTime.error = "Please enter an end time eg. 14:00"
                    requiredFieldsFilled = false
                }
                if (requiredFieldsFilled == true)
                {
                    var date: Date = getDate(binding,etDate) as Date
                    var startTime: Time = getStartTime(binding,etStartTime) as Time
                    var endTime: Time = getEndTime(binding,etEndTime) as Time
                    saveData(date, startTime, endTime,etCategory,etDescription, image)
                }
            }
            catch (e: java.lang.NullPointerException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }
        }
        //----------------------------------------------------------------------------------------//
        //
        binding.ibtnAddImage.setOnClickListener()
        {
            try {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                getResult.launch(intent)
            }
            catch (e: java.lang.IllegalArgumentException)
            {
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
            }
        }
    }
    //----------------------------------------------------------------------------------------//
    //
    private fun saveData(date: Date, startTime: Time, endTime: Time,category: String ,description: String, image: Bitmap) {
        //https://stackoverflow.com/questions/70367909/converting-timestamp-value-to-12-24-hour-time-value-in-kotlin
        try {
            //Timesheet counter
            val sharedTimesheetCounter = getSharedPreferences("SharedTimesheetCounter",Context.MODE_PRIVATE)
            var counterEditor = sharedTimesheetCounter.edit()
            var counter : Int = sharedTimesheetCounter.getInt("Counter",0).toInt()
            counter += 1
            counterEditor.putInt("Counter", counter)
            counterEditor.commit()
            //Category Saved
            val sharedTimesheetCategory =  getSharedPreferences("SharedTimesheetCategory",Context.MODE_PRIVATE)
            var timesheetCategoryEditor = sharedTimesheetCategory.edit()
            timesheetCategoryEditor.putString(counter.toString(), category)
            timesheetCategoryEditor.commit()
            //Date Saved
            val sharedTimesheetDate =  getSharedPreferences("SharedTimesheetDate",Context.MODE_PRIVATE)
            var timesheetDateEditor = sharedTimesheetDate.edit()
            timesheetDateEditor.putString(counter.toString(), date.toString())
            timesheetDateEditor.commit()
            //Start Time Saved
            val sharedTimesheetStartTime =  getSharedPreferences("SharedTimesheetStartTime",Context.MODE_PRIVATE)
            var timesheetStartTimeEditor = sharedTimesheetStartTime.edit()
            timesheetStartTimeEditor.putString(counter.toString(), startTime.toString())
            timesheetStartTimeEditor.commit()
            //End Time Saved
            val sharedTimesheetEndTime =  getSharedPreferences("SharedTimesheetEndTime",Context.MODE_PRIVATE)
            var timesheetEndTimeEditor = sharedTimesheetEndTime.edit()
            timesheetEndTimeEditor.putString(counter.toString(), endTime.toString())
            timesheetEndTimeEditor.commit()
            //https://www.c-sharpcorner.com/article/how-to-store-and-retrieve-the-image-using-sharedpreferences-in-android/
            //https://stackoverflow.com/questions/8586242/how-to-store-images-using-sharedpreference-in-android
            //Picture Saved
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
            val sharedTimesheetImage =  getSharedPreferences("SharedTimesheetImage",Context.MODE_PRIVATE)
            var timesheetImageEditor = sharedTimesheetImage.edit()
            timesheetImageEditor.putString(counter.toString(), encodedImage)
            timesheetImageEditor.commit()
            //Timesheet Saved
            val sharedTimesheetItem =  getSharedPreferences("SharedTimesheetItem",Context.MODE_PRIVATE)
            var timesheetItemEditor = sharedTimesheetItem.edit()
            timesheetItemEditor.putString(counter.toString(),   "Category:\t$category\r\n" +
                                                                "Date:\t$date\r\n" +
                                                                "Start Time:\t$startTime\r\n" +
                                                                "End Time:\t$endTime\r\n" +
                                                                "Description:\t$description")
            timesheetItemEditor.commit()
            //Feedback Message
            Toast.makeText(this, "Timesheet Capture", Toast.LENGTH_SHORT).show()
            Toast.makeText(this,    "Category:\t$category\r\n" +
                                                "Date:\t$date\r\n" +
                                                "Start Time:\t$startTime\r\n" +
                                                "End Time:\t$endTime\r\n" +
                                                "Description:\t$description", Toast.LENGTH_LONG).show()
        }
        catch (e: java.text.ParseException)
        {
            Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.", Toast.LENGTH_LONG).show()
        }

    }
    //----------------------------------------------------------------------------------------//
    //
    private  fun populateSpinner(binding: ActivityTimesheetBinding)
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
    private fun getDate(binding: ActivityTimesheetBinding,etDate: String): Date?
    {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        var date: Date? = null
        try {
            date = formatter.parse(etDate)
        }
        catch (e: java.text.ParseException)
        {
            binding.etDate.error = "Please enter a date dd/mm/yyyy"
        }
        return date
    }
    private fun getStartTime(binding: ActivityTimesheetBinding,etStartTime: String): Time?
    {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        var startTime: Time? = null
        try {
            startTime = Time(sdf.parse(etStartTime).time)
        }
        catch (e: java.text.ParseException)
        {
            binding.etStartTime.error = "Please enter a start time eg. 13:00"
        }
        return startTime
    }
    private fun getEndTime(binding: ActivityTimesheetBinding,etEndTime: String): Time?
    {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        var endTime: Time? = null

        try {
            endTime = Time(sdf.parse(etEndTime).time)
        }
        catch (e: java.text.ParseException)
        {
            binding.etEndTime.error = "Please enter a end time eg. 14:00"
        }
        return endTime
    }
}