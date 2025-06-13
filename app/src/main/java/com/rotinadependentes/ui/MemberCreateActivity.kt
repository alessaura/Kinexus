package com.rotinadependentes.ui.member

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import java.io.ByteArrayOutputStream

class MemberCreateActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnPickImage: Button
    private lateinit var btnSubmit: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var spinnerOrganizations: Spinner

    private var photoBase64: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private var selectedOrganizationId: String? = null
    private val organizationMap = mutableMapOf<String, String>() // nome -> id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_create)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        ivPhoto = findViewById(R.id.ivPhoto)
        spinnerOrganizations = findViewById(R.id.spinnerOrganizations)

        loadOrganizations()

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSubmit.setOnClickListener {
            submitForm()
        }
    }

    private fun loadOrganizations() {
        FirebaseFirestore.getInstance().collection("organization")
            .get()
            .addOnSuccessListener { result ->
                val names = mutableListOf("Selecione a organização")
                organizationMap.clear()

                for (doc in result) {
                    val name = doc.getString("name") ?: "Sem nome"
                    val id = doc.id
                    names.add(name)
                    organizationMap[name] = id
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerOrganizations.adapter = adapter

                spinnerOrganizations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        val selectedName = names[position]
                        selectedOrganizationId = if (position == 0) null else organizationMap[selectedName]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedOrganizationId = null
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar organizações", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                ivPhoto.visibility = View.VISIBLE
                ivPhoto.setImageURI(uri)
                convertImageToBase64(uri)
            }
        }
    }

    private fun convertImageToBase64(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        val byteArray = stream.toByteArray()
        photoBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun submitForm() {
        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedOrganizationId.isNullOrEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val member = hashMapOf(
            "fullName" to name,
            "email" to email,
            "phone" to phone,
            "status" to "active",
            "photoBase64" to (photoBase64 ?: ""),
            "createdAt" to System.currentTimeMillis().toString(),
            "permissions" to listOf("read"),
            "organizationId" to selectedOrganizationId!!
        )

        FirebaseFirestore.getInstance()
            .collection("members")
            .add(member)
            .addOnSuccessListener {
                Toast.makeText(this, "Membro cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao cadastrar membro", Toast.LENGTH_SHORT).show()
            }
    }
}
