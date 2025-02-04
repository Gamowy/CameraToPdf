package com.example.cameratopdf.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.util.Base64
import com.example.cameratopdf.models.CapturedImage
import com.example.cameratopdf.models.PdfFile
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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.File


@Serializable
data class GeneratePdfResponse(val message: String, val filePath: String)

@Serializable
data class PdfFile(val fileName: String, val fullPath: String, val createdDate: String)

class PdfApiClient(baseUrl: String) {
    private val postImagesUrl = "$baseUrl/api/android/upload"
    private val generatePdfUrl = "$baseUrl/api/android/generate"
    private val getPdfUrl = "$baseUrl/api/android/file"
    private val getPdfsListUrl= "$baseUrl/api/android/files"
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
            val response = (client.post(postImagesUrl) {
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
            install(ContentNegotiation) {
                json()
            }
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
            val responseBody: GeneratePdfResponse = response.body()
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

    suspend fun getAllFilesList() : List<PdfFile> {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
            }
        }
        val response = client.get(getPdfsListUrl) {
            contentType(ContentType.Application.Json)
        }
        client.close()
        if (response.status.value != 200) {
            val responseBody : List<PdfFile> = response.body<List<PdfFile>>()
            return responseBody
        }
        return emptyList()
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
        val rotatedBitmap = checkImageRotation(bitmap, imageFile)
        val byteArrayOutputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun checkImageRotation(img: Bitmap, selectedImage: File): Bitmap {
        val ei = ExifInterface(selectedImage.absolutePath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
}