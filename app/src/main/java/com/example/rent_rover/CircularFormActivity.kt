package com.example.rent_rover

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase

class CircularFormActivity : AppCompatActivity() {

    private lateinit var spDivision: Spinner
    private lateinit var spDistrict: Spinner
    private lateinit var spUpazila: Spinner
    private lateinit var spFloorNo: Spinner
    private lateinit var exit:ImageButton

    private lateinit var etAddress: EditText
    // Declare all RadioGroups and RadioButtons
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


    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance()
    private val rentCircularRef = database.getReference("Rent_Circular")

    private lateinit var imageSwitcher: ImageSwitcher
    private val imageUris = mutableListOf<Uri>()
    private var currentImageIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_form_circular);

        spDivision = findViewById(R.id.sp_division)
        spDistrict = findViewById(R.id.sp_district)
        spUpazila = findViewById(R.id.sp_upazilla)
        spFloorNo = findViewById(R.id.sp_floorNo)

        imageSwitcher = findViewById(R.id.post_image)

        etAddress = findViewById(R.id.et_address)
        // Initialize RadioGroups and RadioButtons
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

        // Initialize CheckBoxes
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

        exit=findViewById(R.id.exit)

        // Initialize Button
        btnPostCircular = findViewById(R.id.btn_postCircular)
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


        // Set up the factory for ImageSwitcher
        imageSwitcher.setFactory {
            ImageView(this).apply {
                setScaleType(ImageView.ScaleType.CENTER_CROP)
            }
        }

        // Set the initial image
        imageSwitcher.setImageResource(R.drawable.add_image)

        // Set an OnClickListener to the image field to open the gallery
        findViewById<LinearLayout>(R.id.image_feild).setOnClickListener {
            openGallery()
        }

        // Set OnClickListeners for the "previous" and "next" buttons
        findViewById<ImageButton>(R.id.before).setOnClickListener {
            showPreviousImage()
        }

        findViewById<ImageButton>(R.id.after).setOnClickListener {
            showNextImage()
        }

        // Set the OnClickListener for the delete button
        findViewById<ImageButton>(R.id.delete_image).setOnClickListener {
            deleteCurrentImage()
        }

        btnPostCircular.setOnClickListener {
            // Validate and check if fields are empty
            if (validateFields()) {
                loadingDialog.show()
                uploadImagesToCloudinary()
            }
        }
    }


    // Function to upload images to Cloudinary
    private fun uploadImagesToCloudinary() {
        val uploadedImageUrls = mutableListOf<String>()
        var uploadedCount = 0

        if (imageUris.isEmpty()) {
            showToast("Please select at least one image.")
            loadingDialog.dismiss()
            return
        }

        for (uri in imageUris) {
            MediaManager.get().upload(uri)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as String
                        uploadedImageUrls.add(imageUrl)
                        uploadedCount++

                        // When all images are uploaded (cloudinary), save data to Firebase
                        if (uploadedCount == imageUris.size) {
                            saveCircularData(uploadedImageUrls)
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        loadingDialog.dismiss()
                        showToast("Image upload failed: ${error.description}")
                        Log.e("CloudinaryError", "Error: ${error.description}")
                    }


                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                }).dispatch()
        }
    }



    // Function to open the gallery and allow multiple image selection
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, IMAGE_PICKER_REQUEST)
    }

    // Handle the result from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICKER_REQUEST) {
            data?.let {
                // If multiple images were selected
                if (it.clipData != null) {
                    val count = it.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = it.clipData!!.getItemAt(i).uri
                        imageUris.add(imageUri)
                    }
                    showImageInSwitcher()
                } else {
                    // If only one image was selected
                    it.data?.let { uri ->
                        imageUris.add(uri)
                        showImageInSwitcher()
                    }
                }
            }
        } else {
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Show the images in the ImageSwitcher one by one
    private fun showImageInSwitcher() {
        if (imageUris.isNotEmpty()) {
            imageSwitcher.setImageURI(imageUris[currentImageIndex])
        }
    }

    // Show previous image
    private fun showPreviousImage() {
        if (imageUris.isNotEmpty()) {
            currentImageIndex = (currentImageIndex - 1 + imageUris.size) % imageUris.size
            showImageInSwitcher()
        }
    }

    // Show next image
    private fun showNextImage() {
        if (imageUris.isNotEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % imageUris.size
            showImageInSwitcher()
        }
    }

    // Function to delete the current image from the ImageSwitcher
    private fun deleteCurrentImage() {
        if (imageUris.isNotEmpty()) {
            // Remove the current image from the list
            imageUris.removeAt(currentImageIndex)

            // If there are still images left, show the next/previous image
            if (imageUris.isNotEmpty()) {
                // Update the index and show the image
                currentImageIndex = if (currentImageIndex >= imageUris.size) {
                    imageUris.size - 1
                } else {
                    currentImageIndex
                }
                showImageInSwitcher()
            } else {
                // If no images left, set a default image or reset the ImageSwitcher
                imageSwitcher.setImageResource(R.drawable.add_image)
                currentImageIndex = 0
            }
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
        val floorNumbers = (1..50).map { "$it Floor" }.toTypedArray()
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

    companion object {
        private const val IMAGE_PICKER_REQUEST = 1001
    }


    private fun validateFields(): Boolean {

        if (spDivision.selectedItem == null || spDivision.selectedItem.toString() == "Select Division") {
            Toast.makeText(this, "Division is required.", Toast.LENGTH_SHORT).show()
            spDivision.requestFocus()
            return false
        }

        else if (spDistrict.selectedItem == null || spDistrict.selectedItem.toString() == "Select District") {
            Toast.makeText(this, "District is required.", Toast.LENGTH_SHORT).show()
            spDistrict.requestFocus()
            return false
        }

        else if (spUpazila.selectedItem == null || spUpazila.selectedItem.toString() == "Select Upazila") {
            Toast.makeText(this, "Upazila is required.", Toast.LENGTH_SHORT).show()
            spUpazila.requestFocus()
            return false
        }
        else if (etAddress.text.toString().isEmpty()) {
            etAddress.error = "Address is required."
            etAddress.requestFocus()
            return false
        }

        else if (rgPropertyType.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Property Type is required.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (rgBedrooms.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Number of Bedrooms is required.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (rgBathrooms.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Number of Bathrooms is required.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (spFloorNo.selectedItem == null || spFloorNo.selectedItem.toString() == "Select Floor") {
            Toast.makeText(this, "Floor Number is required.", Toast.LENGTH_SHORT).show()
            spFloorNo.requestFocus()
            return false
        }

        else  if (rgKitchens.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Number of Kitchens is required.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (!cbMuslim.isChecked && !cbHindu.isChecked && !cbChristian.isChecked && !cbBuddhist.isChecked && !cbOthers.isChecked) {
            Toast.makeText(this, "Please select at least one religion.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (!cbBikeParking.isChecked && !cbCarParking.isChecked && !cbGasSupply.isChecked && !cbWaterSupply.isChecked &&
            !cbFurnished.isChecked && !cbWifiConnection.isChecked && !cbCCTV.isChecked) {
            Toast.makeText(this, "Please select at least one additional facility.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (etDescription.text.toString().isEmpty()) {
            etDescription.error = "Additional Description is required."
            etDescription.requestFocus()
            return false
        }

        else if (etMonthlyRent.text.toString().isEmpty()) {
            etMonthlyRent.error = "Monthly Rent is required."
            etMonthlyRent.requestFocus()
            return false
        }

        else if (etPhoneNumber.text.toString().isEmpty()) {
            etPhoneNumber.error = "Phone Number is required."
            etPhoneNumber.requestFocus()
            return false
        }
        else{
            return true
        }
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCircularData(imageUrls: List<String>) {
        val propertyType = getSelectedRadioButtonText(rgPropertyType)
        val bedrooms = getSelectedRadioButtonText(rgBedrooms)
        val bathrooms = getSelectedRadioButtonText(rgBathrooms)
        val kitchens = getSelectedRadioButtonText(rgKitchens)
        val division = spDivision.selectedItem.toString()
        val district = spDistrict.selectedItem.toString()
        val upazila = spUpazila.selectedItem.toString()
        val floorNo = spFloorNo.selectedItem.toString()
        val address = etAddress.text.toString()
        val description = etDescription.text.toString()
        val monthlyRent = etMonthlyRent.text.toString()
        val phoneNumber = etPhoneNumber.text.toString()

        // Fetch the selected religions
        val selectedReligions = mutableListOf<String>().apply {
            if (cbMuslim.isChecked) add("Muslim")
            if (cbHindu.isChecked) add("Hindu")
            if (cbChristian.isChecked) add("Christian")
            if (cbBuddhist.isChecked) add("Buddhist")
            if (cbOthers.isChecked) add("Others")
        }

        // Fetch the selected facilities
        val selectedFacilities = mutableListOf<String>().apply {
            if (cbBikeParking.isChecked) add("Bike Parking")
            if (cbCarParking.isChecked) add("Car Parking")
            if (cbGasSupply.isChecked) add("Gas Supply")
            if (cbWaterSupply.isChecked) add("Water Supply")
            if (cbFurnished.isChecked) add("Furnished")
            if (cbWifiConnection.isChecked) add("Wifi Connection")
            if (cbCCTV.isChecked) add("CCTV")
        }

        // Get the current user's ID from SessionManager
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId() ?: ""

        val circularData = mapOf(
            "propertyType" to propertyType,
            "bedrooms" to bedrooms,
            "bathrooms" to bathrooms,
            "kitchens" to kitchens,
            "division" to division,
            "district" to district,
            "upazila" to upazila,
            "floorNo" to floorNo,
            "address" to address,
            "description" to description,
            "monthlyRent" to monthlyRent,
            "phoneNumber" to phoneNumber,
            "religions" to selectedReligions,
            "facilities" to selectedFacilities,
            "images" to imageUrls,
            "userId" to userId
        )

        rentCircularRef.push().setValue(circularData).addOnCompleteListener { task ->
            loadingDialog.dismiss()
            if (task.isSuccessful) {
                showToast("Circular posted successfully")
                finish()
            } else {
                showToast("Failed to post circular")
            }
        }
    }


    private fun getSelectedRadioButtonText(rg: RadioGroup): String {
        val selectedRadioButtonId = rg.checkedRadioButtonId
        return findViewById<RadioButton>(selectedRadioButtonId)?.text.toString()
    }


}