package com.example.indradev_resto.repository

import android.util.Log
import com.example.indradev_resto.model.TableModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TableModelRepositoryImpl:TableModelRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("tables")

    override fun addTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        val tableId = table.tableId
        if (tableId.isBlank()) {
            callback(false, "Table ID cannot be empty")
            return
        }

        ref.child(tableId)
            .setValue(table)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TableRepo", "Table added successfully: $tableId")
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
                if (!snapshot.exists()) {
                    callback(true, "No tables found", emptyList())
                    return
                }

                val tables = mutableListOf<TableModel>()
                for (tableSnapshot in snapshot.children) {
                    val table = tableSnapshot.getValue(TableModel::class.java)
                    table?.let { tables.add(it) }
                }
                Log.d("TableRepo", "Fetched ${tables.size} tables.")
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
                    Log.d("TableRepo", "Table found: $tableId")
                    callback(true, "Table found", table)
                } else {
                    Log.w("TableRepo", "Table not found: $tableId")
                    callback(false, "Table not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TableRepo", "Error fetching table: ${error.message}")
                callback(false, error.message, null)
            }
        })
    }

    override fun updateTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        val tableId = table.tableId
        if (tableId.isBlank()) {
            callback(false, "Table ID cannot be empty")
            return
        }

        ref.child(tableId)
            .setValue(table)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("TableRepo", "Table updated: $tableId")
                    callback(true, "Table updated successfully")
                } else {
                    val error = it.exception?.localizedMessage ?: "Failed to update table"
                    Log.e("TableRepo", error)
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
                    Log.d("TableRepo", "Table deleted: $tableId")
                    callback(true, "Table deleted successfully")
                } else {
                    val error = it.exception?.localizedMessage ?: "Failed to delete table"
                    Log.e("TableRepo", error)
                    callback(false, error)
                }
            }
    }

    override fun findAvailableTables(minCapacity: Int, callback: (Boolean, String, List<TableModel>?) -> Unit) {
        ref.orderByChild("capacity").startAt(minCapacity.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val availableTables = mutableListOf<TableModel>()
                    for (tableSnapshot in snapshot.children) {
                        val table = tableSnapshot.getValue(TableModel::class.java)
                        if (table != null && table.isAvailable && table.capacity >= minCapacity) {
                            availableTables.add(table)
                        }
                    }
                    Log.d("TableRepo", "Available tables found: ${availableTables.size}")
                    callback(true, "Available tables fetched", availableTables)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TableRepo", "Error finding available tables: ${error.message}")
                    callback(false, error.message, null)
                }
            })
    }
}