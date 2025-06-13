package com.rotinadependentes.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import java.io.ByteArrayOutputStream

class OrganizationCreateActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etType: EditText
    private lateinit var etDescription: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText
    private lateinit var etCountry: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etWebsite: EditText
    private lateinit var imgLogo: ImageView
    private lateinit var btnPickLogo: Button
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    private var selectedLogoUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedLogoUri = it
            val inputStream = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imgLogo.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_create)

        etName = findViewById(R.id.etName)
        etType = findViewById(R.id.etType)
        etDescription = findViewById(R.id.etDescription)
        etAddress = findViewById(R.id.etAddress)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etCountry = findViewById(R.id.etCountry)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etWebsite = findViewById(R.id.etWebsite)
        imgLogo = findViewById(R.id.imgLogo)
        btnPickLogo = findViewById(R.id.btnPickLogo)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)

        btnPickLogo.setOnClickListener { pickLogoFromGallery() }
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { handleSubmit() }
    }

    private fun pickLogoFromGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun handleSubmit() {
        val name = etName.text.toString().trim()
        val type = etType.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val city = etCity.text.toString().trim()
        val state = etState.text.toString().trim()
        val country = etCountry.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val website = etWebsite.text.toString().trim()

        if (name.isEmpty() || type.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        var logoBase64: String? = null
        selectedLogoUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            logoBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }

        val data = hashMapOf(
            "name" to name,
            "type" to type,
            "description" to description,
            "address" to address,
            "city" to city,
            "state" to state,
            "country" to country,
            "phone" to phone,
            "email" to email,
            "website" to website,
            "logoBase64" to logoBase64,
            "createdAt" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("organization")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Organização salva com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
