package com.rotinadependentes.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rotinadependentes.R
import com.rotinadependentes.adapter.DependentAdapter
import com.rotinadependentes.model.Member
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MemberEditActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnPickImage: Button
    private lateinit var btnSubmit: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddDependent: Button
    private lateinit var btnImportContacts: Button
    private lateinit var rvDependents: RecyclerView

    private var memberId: String? = null
    private var photoBase64: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private val dependents = mutableListOf<Map<String, Any>>()
    private lateinit var dependentAdapter: DependentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_edit)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        btnPickImage = findViewById(R.id.btnPickImage)
        btnSubmit = findViewById(R.id.btnSubmit)
        ivPhoto = findViewById(R.id.ivPhoto)
        progressBar = findViewById(R.id.progressBar)
        btnAddDependent = findViewById(R.id.btnAddDependent)
        btnImportContacts = findViewById(R.id.btnImportContacts)
        rvDependents = findViewById(R.id.rvDependents)

        dependentAdapter = DependentAdapter(
            dependents,
            onViewClick = { dependent ->
                val intent = Intent(this, DependentDetailsActivity::class.java)
                intent.putExtra("dependentId", dependent["id"].toString())
                intent.putExtra("dependentName", dependent["name"].toString())
                intent.putExtra("dependentRelationship", dependent["relationship"].toString())
                startActivity(intent)
            },
            onRemoveClick = { position ->
                dependents.removeAt(position)
                dependentAdapter.notifyItemRemoved(position)
            }
        )

        rvDependents.layoutManager = LinearLayoutManager(this)
        rvDependents.adapter = dependentAdapter

        memberId = intent.getStringExtra("memberId")
        if (memberId == null) {
            Toast.makeText(this, "ID do membro não encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadMemberData()

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSubmit.setOnClickListener {
            updateMember()
        }

        btnAddDependent.setOnClickListener {
            showAddDependentDialog()
        }

        btnImportContacts.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS), 100)
            } else {
                importContacts()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            importContacts()
        } else {
            Toast.makeText(this, "Permissão negada para ler os contatos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMemberData() {
        progressBar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection("members").document(memberId!!)
            .get()
            .addOnSuccessListener { document ->
                val member = document.toObject<Member>()
                member?.let {
                    etFullName.setText(it.fullName)
                    etEmail.setText(it.email)
                    etPhone.setText(it.phone)
                    photoBase64 = it.photoBase64

                    if (!it.photoBase64.isNullOrEmpty()) {
                        val imageBytes = Base64.decode(it.photoBase64, Base64.NO_WRAP)
                        val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        ivPhoto.setImageBitmap(bitmap)
                        ivPhoto.visibility = View.VISIBLE
                    }

                    val list = document.get("dependents") as? List<Map<String, Any>>
                    list?.let {
                        dependents.clear()
                        dependents.addAll(it)
                        dependentAdapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar membro", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
            }
    }

    private fun showAddDependentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_dependent, null)
        val etName = dialogView.findViewById<EditText>(R.id.etDependentName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etDependentEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etDependentPhone)
        val spinnerRelation = dialogView.findViewById<Spinner>(R.id.spinnerRelationship)

        val adapter = ArrayAdapter.createFromResource(this, R.array.relationship_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRelation.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Novo dependente")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val dependent = mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "name" to etName.text.toString(),
                    "email" to etEmail.text.toString(),
                    "phone" to etPhone.text.toString(),
                    "relationship" to spinnerRelation.selectedItem.toString(),
                    "createdAt" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
                )
                dependents.add(dependent)
                dependentAdapter.notifyItemInserted(dependents.size - 1)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importContacts() {
        val contacts = mutableListOf<Map<String, Any>>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ?: ""
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: ""

                val contact = mapOf(
                    "id" to UUID.randomUUID().toString(),
                    "name" to name,
                    "phone" to phone,
                    "email" to "",
                    "relationship" to "",
                    "createdAt" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
                )
                contacts.add(contact)
            }
            cursor.close()
        }
        showContactSelectionDialog(contacts)
    }

    private fun showContactSelectionDialog(contactList: List<Map<String, Any>>) {
        val names = contactList.map { it["name"] as String }.toTypedArray()
        val checkedItems = BooleanArray(contactList.size)

        AlertDialog.Builder(this)
            .setTitle("Selecionar contatos")
            .setMultiChoiceItems(names, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Adicionar") { _, _ ->
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        showAddRelationshipDialog(contactList[i])
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddRelationshipDialog(contact: Map<String, Any>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_dependent, null)
        val etName = dialogView.findViewById<EditText>(R.id.etDependentName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etDependentEmail)
        val etPhone = dialogView.findViewById<EditText>(R.id.etDependentPhone)
        val spinnerRelation = dialogView.findViewById<Spinner>(R.id.spinnerRelationship)

        val adapter = ArrayAdapter.createFromResource(this, R.array.relationship_options, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRelation.adapter = adapter

        etName.setText(contact["name"] as String)
        etPhone.setText(contact["phone"] as String)
        etEmail.setText(contact["email"] as String)

        AlertDialog.Builder(this)
            .setTitle("Atribuir relacionamento")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val dependent = mapOf(
                    "id" to contact["id"].toString(),
                    "name" to etName.text.toString(),
                    "phone" to etPhone.text.toString(),
                    "email" to etEmail.text.toString(),
                    "relationship" to spinnerRelation.selectedItem.toString(),
                    "createdAt" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
                )
                dependents.add(dependent)
                dependentAdapter.notifyItemInserted(dependents.size - 1)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                ivPhoto.setImageURI(uri)
                ivPhoto.visibility = View.VISIBLE
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

    private fun updateMember() {
        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = hashMapOf(
            "fullName" to name,
            "email" to email,
            "phone" to phone,
            "photoBase64" to (photoBase64 ?: ""),
            "dependents" to dependents
        )

        FirebaseFirestore.getInstance()
            .collection("members")
            .document(memberId!!)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Membro atualizado com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar membro", Toast.LENGTH_SHORT).show()
            }
    }
}
