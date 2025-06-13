package com.rotinadependentes.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.model.Dependent
import kotlinx.coroutines.tasks.await

class DependentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val dependentsCollection = db.collection("dependents")
    
    suspend fun getDependentsByResponsible(responsibleId: String): List<Dependent> {
        return try {
            dependentsCollection
                .whereArrayContains("responsibleIds", responsibleId)
                .get()
                .await()
                .toObjects(Dependent::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getDependentsByCompany(companyId: String): List<Dependent> {
        return try {
            dependentsCollection
                .whereArrayContains("companyIds", companyId)
                .get()
                .await()
                .toObjects(Dependent::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getDependentById(dependentId: String): Dependent? {
        return try {
            dependentsCollection
                .document(dependentId)
                .get()
                .await()
                .toObject(Dependent::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun addDependent(dependent: Dependent): Boolean {
        return try {
            dependentsCollection.add(dependent).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateDependent(dependent: Dependent): Boolean {
        return try {
            dependentsCollection
                .document(dependent.id)
                .set(dependent)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteDependent(dependentId: String): Boolean {
        return try {
            dependentsCollection
                .document(dependentId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addCompanyToDependent(dependentId: String, companyId: String): Boolean {
        return try {
            val dependent = getDependentById(dependentId) ?: return false
            val updatedCompanyIds = dependent.companyIds.toMutableList()
            
            if (!updatedCompanyIds.contains(companyId)) {
                updatedCompanyIds.add(companyId)
                
                dependentsCollection
                    .document(dependentId)
                    .update("companyIds", updatedCompanyIds)
                    .await()
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun removeCompanyFromDependent(dependentId: String, companyId: String): Boolean {
        return try {
            val dependent = getDependentById(dependentId) ?: return false
            val updatedCompanyIds = dependent.companyIds.toMutableList()
            
            if (updatedCompanyIds.contains(companyId)) {
                updatedCompanyIds.remove(companyId)
                
                dependentsCollection
                    .document(dependentId)
                    .update("companyIds", updatedCompanyIds)
                    .await()
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
}
