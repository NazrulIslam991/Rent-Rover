package com.example.rent_rover

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

class LoadingDialog(context: Context) : Dialog(context) {

    init {
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER
        window?.attributes = layoutParams
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)

        val view: View = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        setContentView(view)
    }
}
