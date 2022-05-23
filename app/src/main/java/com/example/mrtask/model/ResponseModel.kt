package com.example.mrtask.model

import com.google.gson.annotations.SerializedName

data class ResponseModel(

    @field:SerializedName("data")
    val data: List<DataItem?>? = null,

    @field:SerializedName("success")
    val success: Int? = null,

    @field:SerializedName("message")
val message: String? = null
) {
    override fun toString(): String {
        return "ResponseModel(data=$data, success=$success, message=$message)"
    }
}

data class DataItem(

    @field:SerializedName("message")
    val message: String? = null
) {
    override fun toString(): String {
        return "DataItem(message=$message)"
    }
}
