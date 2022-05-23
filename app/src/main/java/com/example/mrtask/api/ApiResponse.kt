package com.example.mrtask.api

import com.example.mrtask.model.ResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiResponse {

    @Multipart
    @POST("all_inventory_mast_save")
    fun updateProfile(
        @Header("Authorization") auth: String?,
        @Part ("all_inventory_id") all_inventory_id: RequestBody?,
        @Part ("user_id") user_id: RequestBody?,
        @Part ("cat_id") cat_id: RequestBody?,
        @Part ("sub_cat_id") sub_cat_id: RequestBody?,
        @Part ("title") title: RequestBody?,
        @Part ("price") price: RequestBody?,
        @Part picture_link_1: List<MultipartBody.Part>?,
        @Part ("show_mo_no") show_mo_no: RequestBody?,
        @Part ("description") description: RequestBody?,
        @Part ("location") location: RequestBody?,
        @Part ("latitude") latitude: RequestBody?,
        @Part ("longitude") longitude: RequestBody?
    ): Call<ResponseModel>
}