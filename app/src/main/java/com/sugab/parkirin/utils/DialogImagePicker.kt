package com.sugab.parkirin.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sugab.parkirin.R
import java.io.InputStream

class DialogImagePicker : DialogFragment() {

    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_image_picker, container, false)
        val buttonChooseImage: Button = view.findViewById(R.id.buttonChooseImage)

        buttonChooseImage.setOnClickListener {
            openGallery()
        }
        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                val imageStream: InputStream? = activity?.contentResolver?.openInputStream(it)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                recognizeText(selectedImage)
            }
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val plateNumber = extractPlateNumber(visionText.text)
                Toast.makeText(context, "Plate Number: $plateNumber", Toast.LENGTH_LONG).show()
                updateFirestoreParkingData(plateNumber)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(context, "Text recognition failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun extractPlateNumber(text: String): String {
        val plateNumberPattern = Regex("[A-Z]{1,2}\\s\\d{1,4}\\s[A-Z]{1,3}")
        val matchResult = plateNumberPattern.find(text)
        return matchResult?.value ?: "No plate number found"
    }

    private fun updateFirestoreParkingData(plateNumber: String) {
        // Reference ke koleksi 'floors'
        val db = FirebaseFirestore.getInstance()
        val floorsRef = db.collection("floors")

        // Query untuk mencari dokumen yang mengandung parkingList dengan plat yang sesuai
        floorsRef.get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val parkingList = document["parkingList"] as List<Map<String, Any>>?

                    val updatedList = mutableListOf<Map<String, Any>>()

                    parkingList?.forEach { parking ->
                        val plat = parking["plat"] as String?
                        if (plat == plateNumber) {
                            // Modifikasi data parkir
                            val updatedParking = parking.toMutableMap()
                            updatedParking["isPlaced"] = false
                            updatedParking["namePlaced"] = ""
                            updatedParking["total"] = 0
                            updatedParking["plat"] = ""
                            updatedParking["startTime"] = Timestamp.now()
                            updatedParking["endTime"] = Timestamp.now()

                            updatedList.add(updatedParking)
                        } else {
                            updatedList.add(parking)
                        }
                    }

                    // Update array 'parkingList' di Firestore
                    floorsRef.document(document.id)
                        .update("parkingList", updatedList)
                        .addOnSuccessListener {
                            println("Data parkir dengan plat $plateNumber berhasil diupdate")
                        }
                        .addOnFailureListener { e ->
                            println("Gagal mengupdate data parkir dengan plat $plateNumber: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error saat mencari data parkir: $e")
            }
    }
}
