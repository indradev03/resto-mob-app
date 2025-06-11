package com.example.indradev_resto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.repository.TableModelRepository


class TableViewModel(private val repository: TableModelRepository) : ViewModel() {

    private val _tables = MutableLiveData<List<TableModel>?>()
    val tables: LiveData<List<TableModel>?> get() = _tables

    private val _table = MutableLiveData<TableModel?>()
    val table: LiveData<TableModel?> get() = _table

    fun addTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        repository.addTable(table, callback)
    }

    fun getAllTables(callback: (Boolean, String, List<TableModel>?) -> Unit) {
        repository.getAllTables { success, message, tableList ->
            if (success) {
                _tables.postValue(tableList)
            } else {
                _tables.postValue(null)
            }
            callback(success, message, tableList)
        }
    }

    fun getTableById(tableId: String, callback: (Boolean, String, TableModel?) -> Unit) {
        repository.getTableById(tableId) { success, message, tableData ->
            if (success) {
                _table.postValue(tableData)
            } else {
                _table.postValue(null)
            }
            callback(success, message, tableData)
        }
    }

    fun updateTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        repository.updateTable(table, callback)
    }

    fun deleteTable(tableId: String, callback: (Boolean, String) -> Unit) {
        repository.deleteTable(tableId, callback)
    }

    fun findAvailableTables(minCapacity: Int, callback: (Boolean, String, List<TableModel>?) -> Unit) {
        repository.findAvailableTables(minCapacity) { success, message, tableList ->
            if (success) {
                _tables.postValue(tableList)
            } else {
                _tables.postValue(null)
            }
            callback(success, message, tableList)
        }
    }
}
