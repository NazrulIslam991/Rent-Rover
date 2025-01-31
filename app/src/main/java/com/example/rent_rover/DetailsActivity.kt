package com.example.rent_rover

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val rentCircular = intent.getSerializableExtra("RENT_CIRCULAR") as RentCircular
        val Location = "${rentCircular.address}, ${rentCircular.upazila}, ${rentCircular.district}, ${rentCircular.division}"
        val floorNo = rentCircular.floorNo.replace("Floor", "").trim()
        val formattedFloorNo = addOrdinalSuffix(floorNo)

        findViewById<TextView>(R.id.tv_houseType).text = rentCircular.propertyType
        findViewById<TextView>(R.id.tv_monthlyRent).text = "${rentCircular.monthlyRent}"
        //findViewById<TextView>(R.id.tv_area).text = rentCircular.floorNo
        findViewById<TextView>(R.id.tv_floor).text = formattedFloorNo
        findViewById<TextView>(R.id.tv_bedroom).text = rentCircular.bedrooms
        findViewById<TextView>(R.id.tv_bathroom).text = rentCircular.bathrooms
        findViewById<TextView>(R.id.tv_kitchen).text = rentCircular.kitchens
        findViewById<TextView>(R.id.tv_religion).text = rentCircular.religions.joinToString(", ")
        findViewById<TextView>(R.id.tv_description).text = rentCircular.description
        findViewById<TextView>(R.id.tv_contact).text = rentCircular.phoneNumber
        findViewById<TextView>(R.id.tv_houseAddress).text = Location

        val btnSendMessage = findViewById<Button>(R.id.btn_sendMessage)
        btnSendMessage.setOnClickListener {
            val intent = Intent(this, MessageChatActivity::class.java)
            intent.putExtra("USER_ID", rentCircular.userId)
            startActivity(intent)
        }



        // Set up image slider and tab layout as in the adapter
        val imageAdapter = ImagePagerAdapter(rentCircular.images)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = imageAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Checking the facilities and updating the UI
        findViewById<TextView>(R.id.tv_bikeParking).text = if (rentCircular.facilities.contains("Bike Parking")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_carParking).text = if (rentCircular.facilities.contains("Car Parking")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_gasSupply).text = if (rentCircular.facilities.contains("Gas Supply")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_waterSupply).text = if (rentCircular.facilities.contains("Water Supply")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_furnished).text = if (rentCircular.facilities.contains("Furnished")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_wifi).text = if (rentCircular.facilities.contains("Wifi Connection")) "Yes" else "No"
        findViewById<TextView>(R.id.tv_CCTV).text = if (rentCircular.facilities.contains("CCTV")) "Yes" else "No"
    }

    private fun addOrdinalSuffix(numberString: String): String {
        return try {
            val number = numberString.toInt()
            val suffix = when {
                number in 11..13 -> "th"
                number % 10 == 1 -> "st"
                number % 10 == 2 -> "nd"
                number % 10 == 3 -> "rd"
                else -> "th"
            }
            "$number$suffix Floor"
        } catch (e: NumberFormatException) {
            numberString
        }
    }
}

