package com.example.feihub_andriod.services

import android.provider.Settings.Global.getString
import androidx.core.content.ContentProviderCompat.requireContext
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import java.io.File
import java.util.*

class S3Service {
    private val bucketName = "feihub-admin-photos-bucket"
    private val s3Client: AmazonS3

    init {
        val accessKey = "AKIA3DFI4HWSYIXZBV7C"
        val secretKey = "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"
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        s3Client = AmazonS3Client(credentials)
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_2))
    }

    fun uploadImage(imagePath: String, customName: String): Boolean {
        return try {
            val putRequest = PutObjectRequest(bucketName, customName, File(imagePath))
            s3Client.putObject(putRequest)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getImageURL(imageName: String): String? {
        return try {
            val request = GeneratePresignedUrlRequest(bucketName, imageName)
                .withExpiration(Date(System.currentTimeMillis() + 604800000)) // 7 days expiration
            val url = s3Client.generatePresignedUrl(request)
            url.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
