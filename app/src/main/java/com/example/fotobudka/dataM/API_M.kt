package com.example.fotobudka

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import kotlin.properties.Delegates


class APIvm(ipAddress: String, port: String) {
    private val mainURL = "http://$ipAddress:$port"

    var postedImages = 0

    //var numberOfPhotos by Delegates.notNull<Int>()
    //var posBanner by Delegates.notNull<Int>()

    private val okHttpClient = OkHttpClient()

    /*fun convertPhotoToBase64(path: String): String {
        val photoFile = File(path)
        var fileInputString: FileInputStream? = null

        try {
            fileInputString = FileInputStream(photoFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val bitMap: Bitmap? = BitmapFactory.decodeStream(fileInputString)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitMap?.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val b: ByteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }*/

    fun sendImageToServer(path: String, posFilter: Int, posBanner: Int, numberOfPhotos: Int) {
        val file = File(path)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("filterID", posFilter.toString())
            .addFormDataPart("image",
                file.name,
                file.asRequestBody(MEDIA_TYPE_JPG))
            .build()

        val formBody = FormBody.Builder()
            .add("search", "Jurassic Park")
            .build()

        val request = Request.Builder()
            .url("${mainURL}/postIMG")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                postedImages += 1
                if (postedImages == numberOfPhotos) {
                    createPDF(posBanner, numberOfPhotos)
                    postedImages = 0
                }
            }

        })
    }

    // "http://your_flask_api_url.com/endpoint?arg1=value1&arg2=value2"
    fun createPDF(banner: Int, numberOfPhotos: Int) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("bannerID", banner.toString())
            .addFormDataPart("numberOfPhotos", numberOfPhotos.toString())
            .build()

        val request = Request.Builder()
            .url("${mainURL}/createPDF")
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                println("PDF created!")
            }

        })
    }

    companion object {
        val MEDIA_TYPE_MARKDOWN = "image/x-markdown; charset=utf-8".toMediaType()
        val MEDIA_TYPE_JPG = "image/jpg".toMediaType()
    }
}