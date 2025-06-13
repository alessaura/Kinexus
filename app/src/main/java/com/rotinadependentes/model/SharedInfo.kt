package com.rotinadependentes.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class SharedInfo(
    @DocumentId val id: String = "",
    val dependentId: String = "", // ID do dependente
    val sourceCompanyId: String = "", // ID da empresa de origem
    val targetCompanyId: String = "", // ID da empresa de destino
    val sharedBy: String = "", // ID do responsável que compartilhou
    val sharedRoutines: List<String> = listOf(), // IDs das rotinas compartilhadas
    val sharedNotes: Boolean = false, // Indica se as observações foram compartilhadas
    val sharedMedicalInfo: Boolean = false, // Indica se as informações médicas foram compartilhadas
    val sharedSpecialNeeds: Boolean = false, // Indica se as necessidades especiais foram compartilhadas
    val sharedDietaryRestrictions: Boolean = false, // Indica se as restrições alimentares foram compartilhadas
    val authorizationCode: String = "", // Código de autorização para importação
    val status: ShareStatus = ShareStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val expiresAt: Date? = null // Data de expiração do compartilhamento, se aplicável
)

enum class ShareStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED
}
