package com.example.rent_rover

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CircularFormActivity : AppCompatActivity() {

    private lateinit var spDivision: Spinner
    private lateinit var spDistrict: Spinner
    private lateinit var spUpazila: Spinner
    private lateinit var spFloorNo: Spinner

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


        spDivision.setDropDownVerticalOffset(100);
        spDistrict.setDropDownVerticalOffset(100);
        spUpazila.setDropDownVerticalOffset(100);
        spFloorNo.setDropDownVerticalOffset(100);

        setupDivisionSpinner()
        setupFloorNoSpinner()


        // Set up the factory for ImageSwitcher
        imageSwitcher.setFactory {
            ImageView(this).apply {
                setScaleType(ImageView.ScaleType.CENTER_CROP)
            }
        }

        // Set the initial image
        imageSwitcher.setImageResource(R.drawable.initial_image)

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
                imageSwitcher.setImageResource(R.drawable.initial_image)
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
        val districts = resources.getStringArray(R.array.district_sylhet) // Using string-array from resources
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
        val floorNumbers = (1..10).map { "$it Floor" }.toTypedArray()
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
}