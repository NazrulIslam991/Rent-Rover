package com.example.rent_rover

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast

class Filter_form : AppCompatActivity() {

    private lateinit var spDivision: Spinner
    private lateinit var spDistrict: Spinner
    private lateinit var spUpazila: Spinner
    private lateinit var spFloorNo: Spinner
    private lateinit var exit: ImageView

    // Declare all RadioGroups and RadioButtons
    private lateinit var cbFamily: CheckBox
    private lateinit var cbBoysHostel: CheckBox
    private lateinit var cbGirlsHostel: CheckBox
    private lateinit var cbBoysMess: CheckBox
    private lateinit var cbGirlsMess: CheckBox

    private lateinit var rgBedrooms: RadioGroup
    private lateinit var rbBedroom1: RadioButton
    private lateinit var rbBedroom2: RadioButton
    private lateinit var rbBedroom3: RadioButton
    private lateinit var rbBedroom4: RadioButton
    private lateinit var rbBedroom5: RadioButton

    private lateinit var rgBathrooms: RadioGroup
    private lateinit var rbBathroom1: RadioButton
    private lateinit var rbBathroom2: RadioButton
    private lateinit var rbBathroom3: RadioButton
    private lateinit var rbBathroom4: RadioButton
    private lateinit var rbBathroom5: RadioButton

    private lateinit var cbBikeParking: CheckBox
    private lateinit var cbCarParking: CheckBox
    private lateinit var cbGasSupply: CheckBox
    private lateinit var cbWaterSupply: CheckBox
    private lateinit var cbFurnished: CheckBox
    private lateinit var cbWifiConnection: CheckBox
    private lateinit var cbCCTV: CheckBox
    private lateinit var min_R: EditText
    private lateinit var max_R: EditText

    private lateinit var btnFilter: Button
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_form)

        spDivision = findViewById(R.id.sp_division)
        spDistrict = findViewById(R.id.sp_district)
        spUpazila = findViewById(R.id.sp_upazilla)
        spFloorNo = findViewById(R.id.sp_floorNo)

        cbFamily = findViewById(R.id.rb_family)
        cbBoysHostel = findViewById(R.id.rb_boysHostel)
        cbGirlsHostel = findViewById(R.id.rb_girlsHostel)
        cbBoysMess = findViewById(R.id.rb_boysMess)
        cbGirlsMess = findViewById(R.id.rb_girlsMess)

        rgBedrooms = findViewById(R.id.rg_bedrooms)
        rbBedroom1 = findViewById(R.id.rb_bedroom1)
        rbBedroom2 = findViewById(R.id.rb_bedroom2)
        rbBedroom3 = findViewById(R.id.rb_bedroom3)
        rbBedroom4 = findViewById(R.id.rb_bedroom4)
        rbBedroom5 = findViewById(R.id.rb_bedroom5)

        rgBathrooms = findViewById(R.id.rg_bathrooms)
        rbBathroom1 = findViewById(R.id.rb_bathroom1)
        rbBathroom2 = findViewById(R.id.rb_bathroom2)
        rbBathroom3 = findViewById(R.id.rb_bathroom3)
        rbBathroom4 = findViewById(R.id.rb_bathroom4)
        rbBathroom5 = findViewById(R.id.rb_bathroom5)

        cbBikeParking = findViewById(R.id.cb_bikeParking)
        cbCarParking = findViewById(R.id.cb_carParking)
        cbGasSupply = findViewById(R.id.cb_gasSupply)
        cbWaterSupply = findViewById(R.id.cb_waterSupply)
        cbFurnished = findViewById(R.id.cb_furnished)
        cbWifiConnection = findViewById(R.id.cb_wifiConnection)
        cbCCTV = findViewById(R.id.cb_cctv)

        min_R = findViewById(R.id.et_monthlyRentMin)
        max_R = findViewById(R.id.et_monthlyRentMax)

        exit=findViewById(R.id.back)

        // Initialize Button
        btnFilter = findViewById(R.id.btn_filter)
        loadingDialog = LoadingDialog(this)

        spDivision.setDropDownVerticalOffset(100);
        spDistrict.setDropDownVerticalOffset(100);
        spUpazila.setDropDownVerticalOffset(100);
        spFloorNo.setDropDownVerticalOffset(100);

        setupDivisionSpinner()
        setupFloorNoSpinner()

        exit.setOnClickListener{
            finish()
        }
        btnFilter.setOnClickListener {
            // Get selected values from spinners
            val selectedDivision = spDivision.selectedItem.toString()
            val selectedDistrict = spDistrict.selectedItem.toString()
            val selectedUpazila = spUpazila.selectedItem.toString()
            val selectedFloorNo = spFloorNo.selectedItem.toString().replace(" Floor", "")

            // Check if spinners have been selected
            if (selectedDivision.isEmpty() || selectedDistrict.isEmpty() || selectedUpazila.isEmpty() || selectedFloorNo.isEmpty()) {
                Toast.makeText(this, "Please select all the required fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected Bedrooms
            val selectedBedrooms = when (rgBedrooms.checkedRadioButtonId) {
                R.id.rb_bedroom1 -> "1"
                R.id.rb_bedroom2 -> "2"
                R.id.rb_bedroom3 -> "3"
                R.id.rb_bedroom4 -> "4"
                R.id.rb_bedroom5 -> "5+"
                else -> ""
            }

            // Check if a bedroom is selected
            if (selectedBedrooms.isEmpty()) {
                Toast.makeText(this, "Please select the number of bedrooms!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected Bathrooms
            val selectedBathrooms = when (rgBathrooms.checkedRadioButtonId) {
                R.id.rb_bathroom1 -> "1"
                R.id.rb_bathroom2 -> "2"
                R.id.rb_bathroom3 -> "3"
                R.id.rb_bathroom4 -> "4"
                R.id.rb_bathroom5 -> "5+"
                else -> ""
            }

            // Check if a bathroom is selected
            if (selectedBathrooms.isEmpty()) {
                Toast.makeText(this, "Please select the number of bathrooms!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Get rent range
            val minRent = min_R.text.toString()
            val maxRent = max_R.text.toString()

            // Check if rent range is valid
            if (minRent.isEmpty() || maxRent.isEmpty()) {
                Toast.makeText(this, "Please enter a valid rent range!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass data through intent
            val intent = Intent(this, FilterRentPost::class.java)
            intent.putExtra("division", selectedDivision)
            intent.putExtra("district", selectedDistrict)
            intent.putExtra("upazila", selectedUpazila)
            intent.putExtra("floorNo", selectedFloorNo)
            intent.putExtra("bedrooms", selectedBedrooms)
            intent.putExtra("bathrooms", selectedBathrooms)
            //intent.putExtra("facilities", selectedFacilities.joinToString(","))
            intent.putExtra("minRent", minRent)
            intent.putExtra("maxRent", maxRent)

            startActivity(intent)
        }

    }

    private fun setupDivisionSpinner() {
        val divisions = arrayOf("Sylhet")
        val divisionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, divisions)
        divisionAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spDivision.adapter = divisionAdapter

        spDivision.setOnItemSelectedListener { _, _, position, _ ->
            val selectedDivision = divisions[position]
            DistrictSpinner(selectedDivision)
        }
    }

    private fun DistrictSpinner(division: String) {
        val districts = resources.getStringArray(R.array.district_sylhet)
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        districtAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spDistrict.adapter = districtAdapter

        spDistrict.setOnItemSelectedListener { _, _, position, _ ->
            val selectedDistrict = districts[position]
            UpazilaSpinner(selectedDistrict)
        }
    }

    private fun UpazilaSpinner(district: String) {
        val upazilas = when (district) {
            "Sylhet" -> resources.getStringArray(R.array.upazilas_sylhet)
            "Sunamganj" -> resources.getStringArray(R.array.upazilas_sunamganj)
            "Moulvibazar" -> resources.getStringArray(R.array.upazilas_moulvibazar)
            "Habiganj" -> resources.getStringArray(R.array.upazilas_habiganj)
            else -> emptyArray()
        }

        val upazilaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, upazilas)
        upazilaAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spUpazila.adapter = upazilaAdapter
    }

    // Method to set up the floor number spinner
    private fun setupFloorNoSpinner() {
        val floorNumbers = (0..50).map { "$it Floor" }.toTypedArray()
        val floorNoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floorNumbers)
        floorNoAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        spFloorNo.adapter = floorNoAdapter

        spFloorNo.setOnItemSelectedListener { _, _, position, _ ->
            val selectedFloor = floorNumbers[position]
        }
    }

    private fun Spinner.setOnItemSelectedListener(action: (adapterView: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                action(parent!!, view, position, id)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
}