package com.sriyank.javatokotlindemo.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import java.io.IOException


fun Context.toast(msg: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showErrorMessage(errorBody: ResponseBody, duration: Int = Toast.LENGTH_SHORT){

    val gson = GsonBuilder().create()
    try {
        val errorResponse = gson.fromJson(errorBody.string(), ErrorResponse::class.java)
        toast(errorResponse.message!!, duration)
    } catch (e: IOException) {
        Log.i("Exception ", e.toString())
    }
}