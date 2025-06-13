package com.rotinadependentes.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Dependent(
    @DocumentId val id: String = "",
    val name: String = "",
    val birthDate: Date = Date(),
    val responsibleIds: List<String> = listOf(), // IDs dos responsáveis
    val companyIds: List<String> = listOf(), // IDs das empresas/instituições
    val specialNeeds: List<SpecialNeed> = listOf(),
    val medicalConditions: List<MedicalCondition> = listOf(),
    val dietaryRestrictions: List<String> = listOf(),
    val preferences: Map<String, String> = mapOf(), // Preferências gerais
    val notes: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class SpecialNeed(
    val type: String = "", // Ex: "Autismo", "TDAH", "Deficiência física"
    val description: String = "",
    val supportRequirements: String = ""
)

data class MedicalCondition(
    val name: String = "",
    val description: String = "",
    val medications: List<Medication> = listOf(),
    val emergencyProcedures: String = ""
)

data class Medication(
    val name: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val instructions: String = "",
    val requiresAssistance: Boolean = false
)
