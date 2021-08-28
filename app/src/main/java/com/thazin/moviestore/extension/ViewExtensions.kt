package com.thazin.moviestore.extension

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

    fun ProgressBar.hide(){
        this.visibility= View.INVISIBLE
    }
    fun ProgressBar.show(){
        this.visibility= View.VISIBLE
    }
    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()