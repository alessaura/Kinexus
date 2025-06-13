package com.rotinadependentes.model

import java.io.Serializable

data class Organization(
    val id: String,
    val name: String,
    val type: String,
    val description: String? = null,
    val logoUrl: String? = null
) : Serializable
