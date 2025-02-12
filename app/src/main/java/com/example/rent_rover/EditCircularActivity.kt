package com.example.rent_rover

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageView

class EditCircularActivity : AppCompatActivity() {

    private lateinit var exit: ImageButton
    private var rentCircularKey: String? = null

    private lateinit var etAddress: EditText
    private lateinit var rgPropertyType: RadioGroup
    private lateinit var rbFamily: RadioButton
    private lateinit var rbBoysHostel: RadioButton
    private lateinit var rbGirlsHostel: RadioButton
    private lateinit var rbBoysMess: RadioButton
    private lateinit var rbGirlsMess: RadioButton

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

    private lateinit var rgKitchens: RadioGroup
    private lateinit var rbKitchen0: RadioButton
    private lateinit var rbKitchen1: RadioButton
    private lateinit var rbKitchen2: RadioButton

    private lateinit var cbMuslim: CheckBox
    private lateinit var cbHindu: CheckBox
    private lateinit var cbChristian: CheckBox
    private lateinit var cbBuddhist: CheckBox
    private lateinit var cbOthers: CheckBox

    private lateinit var cbBikeParking: CheckBox
    private lateinit var cbCarParking: CheckBox
    private lateinit var cbGasSupply: CheckBox
    private lateinit var cbWaterSupply: CheckBox
    private lateinit var cbFurnished: CheckBox
    private lateinit var cbWifiConnection: CheckBox
    private lateinit var cbCCTV: CheckBox
    private lateinit var etDescription: EditText
    private lateinit var etMonthlyRent: EditText
    private lateinit var etPhoneNumber: EditText

    private lateinit var btnPostCircular: Button
    private lateinit var loadingDialog: LoadingDialog

    private val database = FirebaseDatabase.getInstance()
    private val rentCircularRef = database.getReference("Rent_Circular")

    private lateinit var imageSwitcher: ImageSwitcher
    private val imageUris = mutableListOf<Uri>()
    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_circular)

        imageSwitcher = findViewById(R.id.post_image)

        etAddress = findViewById(R.id.et_address)
        rgPropertyType = findViewById(R.id.rg_propertyType)
        rbFamily = findViewById(R.id.rb_family)
        rbBoysHostel = findViewById(R.id.rb_boysHostel)
        rbGirlsHostel = findViewById(R.id.rb_girlsHostel)
        rbBoysMess = findViewById(R.id.rb_boysMess)
        rbGirlsMess = findViewById(R.id.rb_girlsMess)

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

        rgKitchens = findViewById(R.id.rg_kitchens)
        rbKitchen0 = findViewById(R.id.rb_kitchen0)
        rbKitchen1 = findViewById(R.id.rb_kitchen1)
        rbKitchen2 = findViewById(R.id.rb_kitchen2)

        cbMuslim = findViewById(R.id.cb_muslim)
        cbHindu = findViewById(R.id.cb_hindu)
        cbChristian = findViewById(R.id.cb_christian)
        cbBuddhist = findViewById(R.id.cb_buddhist)
        cbOthers = findViewById(R.id.cb_others)

        cbBikeParking = findViewById(R.id.cb_bikeParking)
        cbCarParking = findViewById(R.id.cb_carParking)
        cbGasSupply = findViewById(R.id.cb_gasSupply)
        cbWaterSupply = findViewById(R.id.cb_waterSupply)
        cbFurnished = findViewById(R.id.cb_furnished)
        cbWifiConnection = findViewById(R.id.cb_wifiConnection)
        cbCCTV = findViewById(R.id.cb_cctv)

        etDescription = findViewById(R.id.et_description)
        etMonthlyRent = findViewById(R.id.et_monthlyRent)
        etPhoneNumber = findViewById(R.id.et_phoneNumber)

        btnPostCircular = findViewById(R.id.btn_postCircular)
        loadingDialog = LoadingDialog(this)

        exit = findViewById(R.id.exit)

        // Get the RentCircular data passed from PostedCircularDetailActivity
        val rentCircular = intent.getSerializableExtra("RENT_CIRCULAR") as RentCircular
        rentCircularKey = intent.getStringExtra("RENT_CIRCULAR_KEY")

        // Set the received data to the fields
        etAddress.setText(rentCircular.address)
        etDescription.setText(rentCircular.description)
        etMonthlyRent.setText(rentCircular.monthlyRent)
        etPhoneNumber.setText(rentCircular.phoneNumber)

        // Set the selected property type radio button
        val propertyType = rentCircular.propertyType ?: "Family"
        when (propertyType) {
            "Family" -> rbFamily.isChecked = true
            "Boys Hostel" -> rbBoysHostel.isChecked = true
            "Girls Hostel" -> rbGirlsHostel.isChecked = true
            "Boys Mess" -> rbBoysMess.isChecked = true
            "Girls Mess" -> rbGirlsMess.isChecked = true
            else -> rbFamily.isChecked = true
        }

        // Set the selected number of bedrooms, bathrooms, and kitchens
        setRoomDetails(rentCircular)

        // Set the selected religions
        setReligions(rentCircular)

        // Set the selected facilities
        setFacilities(rentCircular)

        // Setup ImageSwitcher
        imageSwitcher.setFactory {
            ImageView(this).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        // Set onClick listeners
        exit.setOnClickListener {
            finish()
        }

        btnPostCircular.setOnClickListener {
            // Get the updated values from the form
            val updatedRentCircular = RentCircular(
                propertyType = getSelectedPropertyType(),
                bedrooms = getSelectedBedrooms(),
                bathrooms = getSelectedBathrooms(),
                kitchens = getSelectedKitchens(),
                address = etAddress.text.toString(),
                description = etDescription.text.toString(),
                monthlyRent = etMonthlyRent.text.toString(),
                phoneNumber = etPhoneNumber.text.toString(),
                religions = getSelectedReligions(),
                facilities = getSelectedFacilities(),
                userId = rentCircular.userId,
                key = rentCircularKey
            )

            // Update the Rent Circular in Firebase under the provided key
            updateRentCircular(updatedRentCircular)
        }


    }

    private fun setRoomDetails(rentCircular: RentCircular) {
        when (rentCircular.bedrooms) {
            "1" -> rbBedroom1.isChecked = true
            "2" -> rbBedroom2.isChecked = true
            "3" -> rbBedroom3.isChecked = true
            "4" -> rbBedroom4.isChecked = true
            "5" -> rbBedroom5.isChecked = true
        }
        when (rentCircular.bathrooms) {
            "1" -> rbBathroom1.isChecked = true
            "2" -> rbBathroom2.isChecked = true
            "3" -> rbBathroom3.isChecked = true
            "4" -> rbBathroom4.isChecked = true
            "5" -> rbBathroom5.isChecked = true
        }
        when (rentCircular.kitchens) {
            "0" -> rbKitchen0.isChecked = true
            "1" -> rbKitchen1.isChecked = true
            "2" -> rbKitchen2.isChecked = true
        }
    }

    private fun setReligions(rentCircular: RentCircular) {
        cbMuslim.isChecked = rentCircular.religions.contains("Muslim")
        cbHindu.isChecked = rentCircular.religions.contains("Hindu")
        cbChristian.isChecked = rentCircular.religions.contains("Christian")
        cbBuddhist.isChecked = rentCircular.religions.contains("Buddhist")
        cbOthers.isChecked = rentCircular.religions.contains("Others")
    }

    private fun setFacilities(rentCircular: RentCircular) {
        cbBikeParking.isChecked = rentCircular.facilities.contains("Bike Parking")
        cbCarParking.isChecked = rentCircular.facilities.contains("Car Parking")
        cbGasSupply.isChecked = rentCircular.facilities.contains("Gas Supply")
        cbWaterSupply.isChecked = rentCircular.facilities.contains("Water Supply")
        cbFurnished.isChecked = rentCircular.facilities.contains("Furnished")
        cbWifiConnection.isChecked = rentCircular.facilities.contains("Wifi Connection")
        cbCCTV.isChecked = rentCircular.facilities.contains("CCTV")
    }

    private fun getSelectedPropertyType(): String {
        return when {
            rbFamily.isChecked -> "Apparment for Family"
            rbBoysHostel.isChecked -> "Boy's Hostel"
            rbGirlsHostel.isChecked -> "Girl's Hostel"
            rbBoysMess.isChecked -> "Boy's Mess"
            rbGirlsMess.isChecked -> "Girl's Mess"
            else -> "Apparment for Family"
        }
    }

    private fun getSelectedBedrooms(): String {
        return when {
            rbBedroom1.isChecked -> "1"
            rbBedroom2.isChecked -> "2"
            rbBedroom3.isChecked -> "3"
            rbBedroom4.isChecked -> "4"
            rbBedroom5.isChecked -> "5"
            else -> "1"
        }
    }

    private fun getSelectedBathrooms(): String {
        return when {
            rbBathroom1.isChecked -> "1"
            rbBathroom2.isChecked -> "2"
            rbBathroom3.isChecked -> "3"
            rbBathroom4.isChecked -> "4"
            rbBathroom5.isChecked -> "5"
            else -> "1"
        }
    }

    private fun getSelectedKitchens(): String {
        return when {
            rbKitchen0.isChecked -> "0"
            rbKitchen1.isChecked -> "1"
            rbKitchen2.isChecked -> "2"
            else -> "0"
        }
    }

    private fun getSelectedReligions(): List<String> {
        val religions = mutableListOf<String>()
        if (cbMuslim.isChecked) religions.add("Muslim")
        if (cbHindu.isChecked) religions.add("Hindu")
        if (cbChristian.isChecked) religions.add("Christian")
        if (cbBuddhist.isChecked) religions.add("Buddhist")
        if (cbOthers.isChecked) religions.add("Others")
        return religions
    }

    private fun getSelectedFacilities(): List<String> {
        val facilities = mutableListOf<String>()
        if (cbBikeParking.isChecked) facilities.add("Bike Parking")
        if (cbCarParking.isChecked) facilities.add("Car Parking")
        if (cbGasSupply.isChecked) facilities.add("Gas Supply")
        if (cbWaterSupply.isChecked) facilities.add("Water Supply")
        if (cbFurnished.isChecked) facilities.add("Furnished")
        if (cbWifiConnection.isChecked) facilities.add("Wifi Connection")
        if (cbCCTV.isChecked) facilities.add("CCTV")
        return facilities
    }

    private fun updateRentCircular(rentCircular: RentCircular) {
        if (rentCircularKey != null) {
            val rentCircularUpdates = mutableMapOf<String, Any>()

            // Only update fields that can change
            rentCircularUpdates["address"] = rentCircular.address
            rentCircularUpdates["description"] = rentCircular.description
            rentCircularUpdates["monthlyRent"] = rentCircular.monthlyRent
            rentCircularUpdates["phoneNumber"] = rentCircular.phoneNumber
            rentCircularUpdates["propertyType"] = rentCircular.propertyType
            rentCircularUpdates["bedrooms"] = rentCircular.bedrooms
            rentCircularUpdates["bathrooms"] = rentCircular.bathrooms
            rentCircularUpdates["kitchens"] = rentCircular.kitchens
            rentCircularUpdates["religions"] = rentCircular.religions
            rentCircularUpdates["facilities"] = rentCircular.facilities

            loadingDialog.show()

            // Update only if fields have been modified
            rentCircularRef.child(rentCircularKey!!).updateChildren(rentCircularUpdates)
                .addOnSuccessListener {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, PostedCircularActivityShow::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Rent Circular key is missing", Toast.LENGTH_SHORT).show()
        }
    }


}
