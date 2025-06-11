package com.example.indradev_resto.repository

import com.example.indradev_resto.model.TableModel

interface TableModelRepository {

    // Add a new table
    fun addTable(table: TableModel, callback: (Boolean, String) -> Unit)

    // Get all tables
    fun getAllTables(callback: (Boolean, String, List<TableModel>?) -> Unit)

    // Get a table by its ID
    fun getTableById(tableId: String, callback: (Boolean, String, TableModel?) -> Unit)

    // Update a table (pass full updated TableModel)
    fun updateTable(table: TableModel, callback: (Boolean, String) -> Unit)

    // Delete a table by its ID
    fun deleteTable(tableId: String, callback: (Boolean, String) -> Unit)

    // Find available tables with minimum capacity (optional)
    fun findAvailableTables(minCapacity: Int, callback: (Boolean, String, List<TableModel>?) -> Unit)

    // Book a table (sets isAvailable = false)
    fun bookTable(tableId: String, callback: (Boolean, String) -> Unit)
}
