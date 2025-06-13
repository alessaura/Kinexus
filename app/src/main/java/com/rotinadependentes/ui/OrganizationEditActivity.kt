package com.rotinadependentes.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.model.Organization
import java.io.ByteArrayOutputStream
import java.util.Calendar

class OrganizationEditActivity : AppCompatActivity() {

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

    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedLogoUri: Uri? = null
    private var organizationId: String? = null

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

        btnPickLogo.setOnClickListener { imagePickerLauncher.launch("image/*") }
        btnCancel.setOnClickListener { finish() }
        btnSave.setOnClickListener { handleSave() }

        val organization = intent.getSerializableExtra("organization") as? Organization
        organization?.let { fillForm(it) }
    }

    private fun fillForm(org: Organization) {
        organizationId = org.id
        etName.setText(org.name)
        etType.setText(org.type)
        etDescription.setText(org.description)
    }


    private fun handleSave() {
        val name = etName.text.toString().trim()
        val type = etType.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (name.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf<String, Any>(
            "name" to name,
            "type" to type,
            "description" to description
        )

        selectedLogoUri?.let { uri ->
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
            data["logoUrl"] = base64Image
        }

        val db = FirebaseFirestore.getInstance()
        organizationId?.let { id ->
            db.collection("organization").document(id)
                .update(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Organização atualizada!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
