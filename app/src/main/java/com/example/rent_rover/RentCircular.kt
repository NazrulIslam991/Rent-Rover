package com.example.rent_rover


import java.io.Serializable

data class RentCircular(
    val propertyType: String = "",
    val bedrooms: String = "",
    val bathrooms: String = "",
    val kitchens: String = "",
    val division: String = "",
    val district: String = "",
    val upazila: String = "",
    val floorNo: String = "",
    val address: String = "",
    val description: String = "",
    val monthlyRent: String = "",
    val phoneNumber: String = "",
    val religions: List<String> = listOf(),
    val facilities: List<String> = listOf(),
    val images: List<String> = emptyList(),
    val userId: String = ""
) : Serializable

