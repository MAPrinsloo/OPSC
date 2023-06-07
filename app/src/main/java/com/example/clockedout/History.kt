package com.example.clockedout

import android.R
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clockedout.databinding.ActivityHistoryBinding
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import kotlin.collections.ArrayList


class History : AppCompatActivity() {
    //Categories Array.
    private var arrCat = arrayListOf<String>()
    //Holds Timesheet items pre filtering.
    private var arrTimesheetItems = arrayListOf<String>()
    //Holds Timesheet items post filtering.
    private var arrFilteredTimesheetItems = arrayListOf<String>()
    //Holds Start times post Category filtering.
    private var arrFilteredCatStartTimes = arrayListOf<String>()
    //Holds End times post Category filtering.
    private var arrFilteredCatEndTimes = arrayListOf<String>()
    //Holds Start times post Date filtering.
    private var arrFilteredDateStartTimes = arrayListOf<String>()
    //Holds End times post Date filtering.
    private var arrFilteredDateEndTimes = arrayListOf<String>()
    //Holds array of Timesheet items post date filtering.
    private var arrFilteredDates = arrayListOf<String>()
    //Holds array of dates post Category filtering.
    private var arrFilteredCatDates = arrayListOf<Date>()
    //Boolean for telling when a start date has been entered.
    private var startDateEntered = false
    //Boolean for telling when a end date has been entered.
    private var endDateEntered = false
    //Holds string value for the selected category.
    private var selectedCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Populate selection fields.
        populateSpinner(binding)

        //----------------------------------------------------------------------------------------//
        //Filter button.
        binding.btnFilter.setOnClickListener()
        {
            try {
                //Data Validations
                if ((binding.etEndDate.text.isNotEmpty()) && (binding.etStartDate.text.isNotEmpty())) {
                    this.startDateEntered = true
                    this.endDateEntered = true
                    initializeActivity(binding)
                }
                if (binding.etStartDate.text.isEmpty()) {
                    binding.etStartDate.error = "Please enter a start date dd/mm/yyyy"
                }
                if (binding.etEndDate.text.isEmpty()) {
                    binding.etEndDate.error = "Please enter a end date dd/mm/yyyy"
                }
            } catch (e: java.lang.IllegalArgumentException) {
                binding.etStartDate.error = "Please enter a start date dd/mm/yyyy"
                binding.etEndDate.error = "Please enter a end date dd/mm/yyyy"
                Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.",Toast.LENGTH_LONG).show()
            }
        }
        //----------------------------------------------------------------------------------------//
        //https://stackoverflow.com/questions/22462339/implementing-onitemselectedlistener-for-spinner
        //https://stackoverflow.com/questions/46447296/android-kotlin-onitemselectedlistener-for-spinner-not-working
        //When a category is selected.
        binding.spCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                binding.etStartDate.text.clear()
                startDateEntered = false
                binding.etEndDate.text.clear()
                endDateEntered = false
                selectedCategory = selectedItem
                initializeActivity(binding)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        //----------------------------------------------------------------------------------------//
        //When a listview is selected.
        binding.lvTimesheets.setOnItemClickListener { parent, view, position, id ->
            val selectedItem =  binding.lvTimesheets.getItemAtPosition(position).toString()
            var counter = 0
            val sharedTimesheetImage =  getSharedPreferences("SharedTimesheetImage",Context.MODE_PRIVATE)

            arrTimesheetItems.forEach{
                counter++
                if(it.equals(selectedItem)) {
                    val encodedImage = sharedTimesheetImage.getString(counter.toString(), null)
                    val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    binding.ivPicture.setImageBitmap(bitmap)
                    }
                }
            }
        }

    //----------------------------------------------------------------------------------------//
    //Populates the spinner with categories.
    private fun populateSpinner(binding: ActivityHistoryBinding)
    {
        //populating category array using SharedCategories
        val sharedPreferences = getSharedPreferences("SharedCategories", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((key, value) in allEntries) {
            arrCat.add(value.toString())
        }
        //Populating the spinner
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, arrCat)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spCategories.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    //----------------------------------------------------------------------------------------//
    //https://stackoverflow.com/questions/10690370/how-do-i-get-difference-between-two-dates-in-android-tried-every-thing-and-pos#:~:text=getTime()%20%2D%20date2.,diffDate%20%3D%20new%20Date(diff)%3B
    //Calculates the hours recorded and displays in the tvHours.text.
    private fun calculateHours(binding: ActivityHistoryBinding, arrStartTimes: ArrayList<String>, arrEndTimes: ArrayList<String>)
    {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        var startTime: Time = Time(sdf.parse("12:00:00").time)
        var endTime: Time = Time(sdf.parse("12:00:00").time)
        var diff: Long = 0
        var seconds = 0
        var minutes = 0
        var hours = 0
        //Counter used to track positions in the array.
        var counter = 0
        //Iterates through an array of start times.
        arrStartTimes.forEach{
            startTime = Time(sdf.parse(it).time)
            endTime = Time(sdf.parse(arrEndTimes.get(counter)).time)
            diff =  endTime.getTime() - startTime.getTime()
            seconds = (diff / 1000).toInt()
            minutes = seconds / 60
            hours += minutes / 60
            counter++
        }
        binding.tvHours.text="Hours: $hours"
        //Resetting the values.
        diff = 0
        seconds = 0
        minutes = 0
        hours = 0
        counter = 0

    }
    //----------------------------------------------------------------------------------------//
    //Initializes the activity using a combination of arrays and shared preferences.
    private fun initializeActivity(binding: ActivityHistoryBinding)
    {
        try {
            //SharedTimesheetItem holds all the timesheet entries verbatim.
            val sharedTimeSheetItem = getSharedPreferences("SharedTimesheetItem", Context.MODE_PRIVATE)
            val allTimeSheetEntries: Map<String, *> = sharedTimeSheetItem.all
            var adapter: ArrayAdapter<String>

            //Populating the pre filtered array of Timesheet items.
            arrTimesheetItems.clear()
            for ((key, value) in allTimeSheetEntries) {
                arrTimesheetItems.add(value.toString())
            }
            //Filter Timesheet items by Category.
            filterByCategory(binding)
            //Filter Timesheet items by Date if edit texts are entered.
            if((this.startDateEntered == true) && (this.endDateEntered == true)) {
                filterByDate(binding)
                //Adapter used for when dates have been entered.
                adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrFilteredDates)
            }
            else
            {
                //Default adapter when no dates have been entered for filtering.
                adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrFilteredTimesheetItems)
            }
            //Display Timesheet items in the list view lvTimesheets using the adapter.
            adapter.setDropDownViewResource(R.layout.simple_list_item_1)
            binding.lvTimesheets.adapter = adapter
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        catch (e: java.lang.IllegalArgumentException)
        {
            Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.",Toast.LENGTH_LONG).show()
        }
    }
    //----------------------------------------------------------------------------------------//
    //https://stackoverflow.com/questions/883060/how-can-i-determine-if-a-date-is-between-two-dates-in-java
    //Filters the Timesheet entries by the start and end date.
    private fun filterByDate(binding: ActivityHistoryBinding)
    {
        try {
            val stringToDateFormatter = SimpleDateFormat("dd/MM/yyyy")
            val etStartDate: String = binding.etStartDate.text.toString()
            val etEndDate: String = binding.etEndDate.text.toString()
            var counter = 0
            //Clears the Arrays and counter for the new filter.
            arrFilteredDates.clear()
            arrFilteredDateStartTimes.clear()
            arrFilteredDateEndTimes.clear()
            counter = 0
            //Category dates must be populated first in the initializeActivity().
            arrFilteredCatDates.forEach{
                if(it.after(stringToDateFormatter.parse(etStartDate)) &&
                    (it.before(stringToDateFormatter.parse(etEndDate)))){
                    //Used to alter the adapter.
                    arrFilteredDates.add(arrFilteredTimesheetItems[counter])
                    //used for calculating the hours based on the filter.
                    arrFilteredDateStartTimes.add(arrFilteredCatStartTimes[counter])
                    arrFilteredDateEndTimes.add(arrFilteredCatEndTimes[counter])
                }
                counter++
                calculateHours(binding, arrFilteredDateStartTimes, arrFilteredDateEndTimes)
            }
        }
        catch (e: java.text.ParseException)
        {
            binding.etStartDate.error = "Please enter a start date dd/mm/yyyy"
            binding.etEndDate.error = "Please enter a end date dd/mm/yyyy"
        }
    }
    //----------------------------------------------------------------------------------------//
    //Filters the Timesheet entries by the category selected.
    private fun filterByCategory(binding: ActivityHistoryBinding)
    {
        try {
            //Timesheet items for list view.
            val sharedTimeSheetItem = getSharedPreferences("SharedTimesheetItem", Context.MODE_PRIVATE)
            //Timesheet Categories relating the SharedTimesheetItem.
            val sharedTimesheetCategories = getSharedPreferences("SharedTimesheetCategory", Context.MODE_PRIVATE)
            //Timesheet start time relating the SharedTimesheetItem.
            val sharedTimesheetStartTime =  getSharedPreferences("SharedTimesheetStartTime",Context.MODE_PRIVATE)
            //Timesheet end time relating the SharedTimesheetItem.
            val sharedTimesheetEndTime =  getSharedPreferences("SharedTimesheetEndTime",Context.MODE_PRIVATE)
            //Timesheet dates relating the SharedTimesheetItem.
            val sharedTimesheetDate =  getSharedPreferences("SharedTimesheetDate",Context.MODE_PRIVATE)
            //convert the date into a string format for comparisons.
            val dateToStringFormatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())


            val allTimeSheetEntries: Map<String, *> = sharedTimeSheetItem.all
            //Clear variables for the new filter to be applied.
            arrFilteredTimesheetItems.clear()
            arrFilteredCatStartTimes.clear()
            arrFilteredCatEndTimes.clear()
            arrFilteredCatDates.clear()
            //Iterate through all timesheet entries.
            for ((key, value) in allTimeSheetEntries) {
                if (sharedTimesheetCategories.getString(key,"").equals(selectedCategory))
                {
                    arrFilteredTimesheetItems.add(value.toString())
                    arrFilteredCatStartTimes.add(sharedTimesheetStartTime.getString(key,"").toString())
                    arrFilteredCatEndTimes.add(sharedTimesheetEndTime.getString(key,"").toString())
                    arrFilteredCatDates.add(dateToStringFormatter.parse(sharedTimesheetDate.getString(key,"")))
                }
            }
            calculateHours(binding, arrFilteredCatStartTimes, arrFilteredCatEndTimes)
        }
        catch (e: java.lang.IllegalArgumentException)
        {
            Toast.makeText(this,"Error Occurred, Ensure all values are entered correctly.",Toast.LENGTH_LONG).show()
        }
    }
}
