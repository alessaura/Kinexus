package com.rotinadependentes.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Responsible(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val userId: String = "", // ID do usuário autenticado
    val relationship: String = "", // Pai, Mãe, Tutor, etc.
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class NotificationPreferences(
    val enableEmail: Boolean = true,
    val enablePush: Boolean = true,
    val routineChanges: Boolean = true,
    val medicationReminders: Boolean = true,
    val occurrenceAlerts: Boolean = true,
    val shareRequests: Boolean = true
)
