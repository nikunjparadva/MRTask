package com.example.mrtask.ui

import android.Manifest
import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.mrtask.R
import com.example.mrtask.adapter.ImageAdapter
import com.example.mrtask.api.ApiClient
import com.example.mrtask.databinding.ActivityMainBinding
import com.example.mrtask.model.ResponseModel
import com.example.mrtask.util.Util.getRealPathFromUri
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    var imagelist = mutableListOf<Uri>()
    var adapter: ImageAdapter? = null
    var showMobilenumber = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        adapter = ImageAdapter(this, imagelist)
        binding.recylerPhotos.adapter = adapter

        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.layAddimage.setOnClickListener {
            if (imagelist.size == 10) {
                Toast.makeText(applicationContext, "Maximum Limit Reached", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            Permissions.check(
                this /*context*/,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        pickImages()
                    }
                })

        }

        binding.btnPostAd.setOnClickListener {
            uploadRequest()
        }

        binding.tvCheckYes.setOnClickListener {
            showMobilenumber = "1"
            binding.tvCheckNo.setTextColor(Color.BLACK)
            binding.tvCheckYes.setTextColor(Color.WHITE)

            binding.tvCheckYes.setBackgroundResource(R.drawable.bg_et_colored)
            binding.tvCheckNo.setBackgroundResource(R.drawable.bg_et)
        }
        binding.tvCheckNo.setOnClickListener {
            showMobilenumber = "0"
            binding.tvCheckYes.setTextColor(Color.BLACK)
            binding.tvCheckNo.setTextColor(Color.WHITE)
            binding.tvCheckYes.setBackgroundResource(R.drawable.bg_et)
            binding.tvCheckNo.setBackgroundResource(R.drawable.bg_et_colored)
        }
    }

    private fun uploadRequest() {
        if (binding.etTitle.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Add Title", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.etPrice.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Add Price", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.etDescription.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Add Description", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.tvLocation.text.toString().trim().isEmpty()) {
            Toast.makeText(applicationContext, "Add Location", Toast.LENGTH_SHORT).show()
            return
        }

        if (imagelist.size == 0) {
            Toast.makeText(applicationContext, "Select Image First", Toast.LENGTH_SHORT).show()
            return
        }

        val token =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2NTA2OTE3MDQsImV4cCI6MTY4MjIyNzcwNH0.VJlEAot_mp73G8mFyzZ5QzBMcNJaZKccSVGysuo85gY"

        val listpart = mutableListOf<MultipartBody.Part>()

        for (k in imagelist.withIndex()) {
            toImageRequestBody(k.index)?.let { listpart.add(it) }
        }

        binding.layProgress.isVisible = true

        ApiClient.retrofit.updateProfile(
            token,
            toRequestBody("0"),
            toRequestBody("1"),
            toRequestBody("5"),
            toRequestBody("5"),
            toRequestBody(binding.etTitle.text.toString()),
            toRequestBody(binding.etPrice.text.toString()),
            listpart,
            toRequestBody(showMobilenumber),
            toRequestBody(binding.etDescription.text.toString()),
            toRequestBody(binding.tvLocation.text.toString()),
            toRequestBody("12.2365"),
            toRequestBody("78.2558")
        ).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                Log.e(TAG, "onResponse: " + response.body().toString())
                binding.layProgress.isVisible = false
                if (response.body()?.success == 1) {
                    Toast.makeText(applicationContext, "Successfully Uploaded", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error : " + response.body().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                binding.layProgress.isVisible = false
                Toast.makeText(applicationContext, "Something went Wrong", Toast.LENGTH_LONG)
                    .show()
                t.printStackTrace()
            }
        })
    }

    private fun pickImages() {
        ImagePicker.with(this)
            .galleryOnly()    //User can only select image from Gallery
            .createIntent { startForProfileImageResult.launch(it) }
    }

    private fun toRequestBody(string: String): RequestBody {
        return RequestBody.create(
            "text/plain".toMediaTypeOrNull(),
            string
        )
    }

    private fun toImageRequestBody(position: Int): MultipartBody.Part? {
        if (position < imagelist.size) {
            val filePath: String? = getRealPathFromUri(this, imagelist[position])
            val file = File(filePath)

            val requestBody: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "picture_link_" + (position + 1),
                    "${System.currentTimeMillis()}_" + file.name,
                    requestBody
                )

            return body
        }
        return null

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!
                imagelist.add(fileUri)
                adapter?.notifyDataSetChanged()
                Log.e(TAG, "fileuri " + fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Image Pick Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


}