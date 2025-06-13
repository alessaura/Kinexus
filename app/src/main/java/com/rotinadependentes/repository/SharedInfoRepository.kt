package com.rotinadependentes.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.model.SharedInfo
import com.rotinadependentes.model.ShareStatus
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class SharedInfoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val sharedInfoCollection = db.collection("shared_info")
    
    suspend fun getSharedInfoByDependent(dependentId: String): List<SharedInfo> {
        return try {
            sharedInfoCollection
                .whereEqualTo("dependentId", dependentId)
                .get()
                .await()
                .toObjects(SharedInfo::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getSharedInfoBySourceCompany(companyId: String): List<SharedInfo> {
        return try {
            sharedInfoCollection
                .whereEqualTo("sourceCompanyId", companyId)
                .get()
                .await()
                .toObjects(SharedInfo::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getSharedInfoByTargetCompany(companyId: String): List<SharedInfo> {
        return try {
            sharedInfoCollection
                .whereEqualTo("targetCompanyId", companyId)
                .get()
                .await()
                .toObjects(SharedInfo::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getSharedInfoByAuthCode(authCode: String): SharedInfo? {
        return try {
            sharedInfoCollection
                .whereEqualTo("authorizationCode", authCode)
                .get()
                .await()
                .toObjects(SharedInfo::class.java)
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun createSharedInfo(sharedInfo: SharedInfo): String? {
        return try {
            // Gerar código de autorização único
            val authCode = UUID.randomUUID().toString().substring(0, 8).uppercase()
            
            val infoWithAuthCode = sharedInfo.copy(
                authorizationCode = authCode,
                status = ShareStatus.PENDING,
                createdAt = Date()
            )
            
            val docRef = sharedInfoCollection.add(infoWithAuthCode).await()
            authCode
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateSharedInfoStatus(id: String, status: ShareStatus): Boolean {
        return try {
            sharedInfoCollection
                .document(id)
                .update(
                    mapOf(
                        "status" to status,
                        "updatedAt" to Date()
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun importSharedInfo(authCode: String, targetCompanyId: String): Boolean {
        return try {
            val sharedInfo = getSharedInfoByAuthCode(authCode) ?: return false
            
            if (sharedInfo.status != ShareStatus.PENDING || sharedInfo.targetCompanyId != targetCompanyId) {
                return false
            }
            
            updateSharedInfoStatus(sharedInfo.id, ShareStatus.APPROVED)
        } catch (e: Exception) {
            false
        }
    }
}
