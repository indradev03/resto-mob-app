package com.example.indradev_resto.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.indradev_resto.model.TableModel
import com.google.firebase.database.*

class TableModelRepositoryImpl : TableModelRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("tables")

    private val _tableListLiveData = MutableLiveData<List<TableModel>>()
    val tableListLiveData: LiveData<List<TableModel>> = _tableListLiveData

    override fun addTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        if (table.tableId.isBlank()) {
            callback(false, "Table ID cannot be empty")
            return
        }

        ref.child(table.tableId)
            .setValue(table)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TableRepo", "Table added successfully: ${table.tableId}")
                    callback(true, "Table added successfully")
                } else {
                    val error = it.exception?.localizedMessage ?: "Failed to add table"
                    Log.e("TableRepo", error)
                    callback(false, error)
                }
            }
    }

    override fun getAllTables(callback: (Boolean, String, List<TableModel>?) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tables = snapshot.children.mapNotNull { it.getValue(TableModel::class.java) }
                _tableListLiveData.postValue(tables)
                callback(true, "Tables fetched successfully", tables)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TableRepo", "Error fetching tables: ${error.message}")
                callback(false, error.message, null)
            }
        })
    }

    override fun getTableById(tableId: String, callback: (Boolean, String, TableModel?) -> Unit) {
        ref.child(tableId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val table = snapshot.getValue(TableModel::class.java)
                    callback(true, "Table found", table)
                } else {
                    callback(false, "Table not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun updateTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        if (table.tableId.isBlank()) {
            callback(false, "Table ID cannot be empty")
            return
        }

        ref.child(table.tableId)
            .setValue(table)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Table updated successfully")
                } else {
                    val error = it.exception?.localizedMessage ?: "Failed to update table"
                    callback(false, error)
                }
            }
    }

    override fun deleteTable(tableId: String, callback: (Boolean, String) -> Unit) {
        if (tableId.isBlank()) {
            callback(false, "Table ID cannot be empty")
            return
        }

        ref.child(tableId)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Table deleted successfully")
                } else {
                    val error = it.exception?.localizedMessage ?: "Failed to delete table"
                    callback(false, error)
                }
            }
    }

    override fun findAvailableTables(minCapacity: Int, callback: (Boolean, String, List<TableModel>?) -> Unit) {
        ref.orderByChild("capacity").startAt(minCapacity.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val availableTables = snapshot.children.mapNotNull {
                        val table = it.getValue(TableModel::class.java)
                        if (table != null && table.isAvailable && table.capacity >= minCapacity) table else null
                    }
                    callback(true, "Available tables fetched", availableTables)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    /**
     * Marks a table as unavailable (booked).
     * This method updates the 'isAvailable' property of the table to false.
     */
    override fun bookTable(tableId: String, callback: (Boolean, String) -> Unit) {
        getTableById(tableId) { success, msg, table ->
            if (success && table != null) {
                val updatedTable = table.copy(isAvailable = false)
                updateTable(updatedTable) { updateSuccess, updateMsg ->
                    if (updateSuccess) {
                        callback(true, "Table booked successfully")
                    } else {
                        callback(false, "Failed to book table: $updateMsg")
                    }
                }
            } else {
                callback(false, "Failed to fetch table for booking: $msg")
            }
        }
    }
}
