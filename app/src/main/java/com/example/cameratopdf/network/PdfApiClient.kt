package com.example.cameratopdf.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.cameratopdf.models.CapturedImage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.File


@Serializable
data class GeneratePdfResponse(val message: String, val filePath: String)

class PdfApiClient(private val baseUrl: String) {
    private val uploadImagesUrl = "$baseUrl/api/android/upload"
    private val generatePdfUrl = "$baseUrl/api/android/generate"
    private val getPdfUrl = "$baseUrl/api/android/file"
    private val clearImagesUrl = "$baseUrl/api/android/clear"

    suspend fun uploadImage(image: CapturedImage): Boolean {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
            }
        }
        val file = image.uri.path?.let { File(it) }
        if (file != null) {
            val base64String = "\"" + convertJpegToBase64(file) + "\""
            val response = (client.post(uploadImagesUrl) {
                contentType(ContentType.Application.Json)
                setBody(base64String)
            })
            client.close()
            if (response.status.value == 200) {
                return true
            }
        }
        return false
    }

    suspend fun generatePdf(fileName: String, language: String) : String? {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
            }
        }
        val response : HttpResponse = client.post(generatePdfUrl) {
            url {
                parameter("name", fileName)
                parameter("language", language)
            }
            contentType(ContentType.Application.Json)
        }
        client.close()
        if (response.status.value == 200) {
            val responseBody = response.body<GeneratePdfResponse>()
            return responseBody.filePath
        }
        return null
    }

    suspend fun getPdf(fileName: String) : ByteArray? {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
            }
        }
        val response : HttpResponse = client.get(getPdfUrl) {
            url {
                parameter("fileName", fileName)
            }
            contentType(ContentType.Application.Pdf)
        }
        client.close()
        if (response.status.value == 200) {
            return response.readRawBytes()
        }
        return null
    }

    suspend fun clearImages() : Boolean {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
            }
        }
        val response = client.get(clearImagesUrl)
        client.close()
        return response.status.value == 200
    }

    private fun convertJpegToBase64(imageFile: File) : String {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}