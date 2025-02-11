package com.example.rent_rover

import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RentCircularAdapter(private val rentCircularList: List<RentCircular>) : RecyclerView.Adapter<RentCircularAdapter.RentCircularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentCircularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.circular_layout, parent, false)
        return RentCircularViewHolder(view)
    }

    override fun onBindViewHolder(holder: RentCircularViewHolder, position: Int) {
        val rentCircular = rentCircularList[position]
        holder.tvHouseType.text = rentCircular.propertyType
        holder.tvAddress.text = rentCircular.address
        holder.tvMonthlyRent.text = "${rentCircular.monthlyRent}"
        val formattedFloorNo = addOrdinalSuffix(rentCircular.floorNo.replace("Floor", "").trim())
        holder.tvFloorNo.text = formattedFloorNo
        holder.tvBedrooms.text = rentCircular.bedrooms
        holder.tvBathrooms.text = rentCircular.bathrooms
        val wifiConnection = if (rentCircular.facilities.contains("Wifi Connection")) "Yes" else "No"
        holder.cbWifiConnection.text = wifiConnection

        val imageUrls = rentCircular.images
        if (imageUrls.isNotEmpty()) {
            val imageAdapter = ImagePagerAdapter(imageUrls)
            holder.viewPager.adapter = imageAdapter
            // Setup TabLayout for dot indicator
            TabLayoutMediator(holder.tabLayout, holder.viewPager) { tab, position -> }.attach()
        }

        // Handle click on the entire item and pass the key to DetailsActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("RENT_CIRCULAR", rentCircular)
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return rentCircularList.size
    }

    class RentCircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHouseType: TextView = itemView.findViewById(R.id.tv_houseType)
        val tvAddress: TextView = itemView.findViewById(R.id.address)
        val tvMonthlyRent: TextView = itemView.findViewById(R.id.tv_monthlyRent)
        val tvFloorNo: TextView = itemView.findViewById(R.id.sp_floorNo)
        val tvBedrooms: TextView = itemView.findViewById(R.id.rg_bedrooms)
        val tvBathrooms: TextView = itemView.findViewById(R.id.rg_bathrooms)
        val cbWifiConnection: TextView = itemView.findViewById(R.id.cb_wifiConnection)

        val viewPager: ViewPager2 = itemView.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = itemView.findViewById(R.id.tabLayout)
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
            "$number$suffix"
        } catch (e: NumberFormatException) {
            numberString
        }
    }

}

