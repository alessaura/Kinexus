package com.rotinadependentes.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Member(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val status: String = "",
    val organizationId: String = "",
    val photoUrl: String? = null,
    val photoBase64: String? = null,
    val dependents: List<Map<String, Any>> = emptyList()
) : Serializable

