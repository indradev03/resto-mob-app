package com.example.indradev_resto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.repository.TableModelRepository

class TableViewModel(private val repository: TableModelRepository) : ViewModel() {

    private val _tables = MutableLiveData<List<TableModel>>(emptyList())
    val tables: LiveData<List<TableModel>> get() = _tables


    private val _table = MutableLiveData<TableModel?>(null)
    val table: LiveData<TableModel?> get() = _table

    private val _message = MutableLiveData<String>("")
    val message: LiveData<String> get() = _message

    fun addTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        repository.addTable(table) { success, msg ->
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun getAllTables(callback: (Boolean, String, List<TableModel>) -> Unit) {
        repository.getAllTables { success, msg, list ->
            if (success && list != null) {
                _tables.postValue(list!!)
            } else {
                _tables.postValue(emptyList())
            }
            _message.postValue(msg)
            callback(success, msg, list ?: emptyList())
        }
    }

    fun getTableById(tableId: String, callback: (Boolean, String, TableModel?) -> Unit) {
        repository.getTableById(tableId) { success, msg, tableData ->
            _table.postValue(if (success) tableData else null)
            _message.postValue(msg)
            callback(success, msg, tableData)
        }
    }

    fun updateTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        repository.updateTable(table) { success, msg ->
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun deleteTable(tableId: String, callback: (Boolean, String) -> Unit) {
        repository.deleteTable(tableId) { success, msg ->
            _message.postValue(msg)
            callback(success, msg)
        }
    }

    fun findAvailableTables(minCapacity: Int, callback: (Boolean, String, List<TableModel>) -> Unit) {
        repository.findAvailableTables(minCapacity) { success, msg, list ->
            if (success && list != null) {
                _tables.postValue(list!!)
            } else {
                _tables.postValue(emptyList())
            }
            _message.postValue(msg)
            callback(success, msg, list ?: emptyList())
        }
    }

    fun markTableUnavailable(tableId: String, callback: (Boolean, String) -> Unit) {
        repository.bookTable(tableId, callback)
    }



    fun bookTable(table: TableModel, callback: (Boolean, String) -> Unit) {
        repository.bookTable(table.tableId) { success, msg ->
            _message.postValue(msg)
            if (success) {
                // Refresh after booking
                getAllTables { _, _, _ -> }
            }
            callback(success, msg)
        }
    }

}