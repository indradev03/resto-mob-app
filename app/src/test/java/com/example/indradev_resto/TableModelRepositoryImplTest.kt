package com.example.indradev_resto

import com.example.indradev_resto.model.TableModel
import com.example.indradev_resto.repository.TableModelRepositoryImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TableModelRepositoryImplTest {

    @Mock
    private lateinit var mockRef: DatabaseReference

    @Mock
    private lateinit var mockTableRef: DatabaseReference

    @Mock
    private lateinit var mockTask: Task<Void>

    private lateinit var repository: TableModelRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Use lenient stubbing only when really needed
        lenient().`when`(mockRef.child(anyString())).thenReturn(mockTableRef)
        lenient().`when`(mockTableRef.child(anyString())).thenReturn(mockTableRef)

        repository = TableModelRepositoryImpl(mockRef)
    }

    @Test
    fun `addTable success`() {
        val table = TableModel("table1", 1, 4, "Indoor", true, null)
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockTableRef.setValue(table)).thenReturn(mockTask)
        doAnswer {
            val listener = it.arguments[0] as OnCompleteListener<Void>
            `when`(mockTask.isSuccessful).thenReturn(true)
            listener.onComplete(mockTask)
            null
        }.`when`(mockTask).addOnCompleteListener(any())

        repository.addTable(table, callback)

        verify(mockTableRef).setValue(table)
        verify(callback).invoke(true, "Table added successfully")
    }

    @Test
    fun `deleteTable success`() {
        val tableId = "table1"
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockRef.child(tableId)).thenReturn(mockTableRef)
        `when`(mockTableRef.removeValue()).thenReturn(mockTask)
        doAnswer {
            val listener = it.arguments[0] as OnCompleteListener<Void>
            `when`(mockTask.isSuccessful).thenReturn(true)
            listener.onComplete(mockTask)
            null
        }.`when`(mockTask).addOnCompleteListener(any())

        repository.deleteTable(tableId, callback)

        verify(mockTableRef).removeValue()
        verify(callback).invoke(true, "Table deleted successfully")
    }

    @Test
    fun `deleteTable failure due to empty tableId`() {
        val callback = mock<(Boolean, String) -> Unit>()

        repository.deleteTable("", callback)

        verify(callback).invoke(false, "Table ID cannot be empty")
        verify(mockTableRef, never()).removeValue()
    }

    @Test
    fun `deleteTable failure from firebase`() {
        val tableId = "table2"
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockRef.child(tableId)).thenReturn(mockTableRef)
        `when`(mockTableRef.removeValue()).thenReturn(mockTask)
        doAnswer {
            val listener = it.arguments[0] as OnCompleteListener<Void>
            `when`(mockTask.isSuccessful).thenReturn(false)
            `when`(mockTask.exception).thenReturn(Exception("Remove error"))
            listener.onComplete(mockTask)
            null
        }.`when`(mockTask).addOnCompleteListener(any())

        repository.deleteTable(tableId, callback)

        verify(callback).invoke(false, "Remove error")
    }

    @Test
    fun `updateTable success`() {
        val table = TableModel("table1", 1, 4, "Indoor", true, null)
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockRef.child(table.tableId)).thenReturn(mockTableRef)
        `when`(mockTableRef.setValue(table)).thenReturn(mockTask)
        doAnswer {
            val listener = it.arguments[0] as OnCompleteListener<Void>
            `when`(mockTask.isSuccessful).thenReturn(true)
            listener.onComplete(mockTask)
            null
        }.`when`(mockTask).addOnCompleteListener(any())

        repository.updateTable(table, callback)

        verify(mockTableRef).setValue(table)
        verify(callback).invoke(true, "Table updated successfully")
    }

    @Test
    fun `updateTable failure due to empty tableId`() {
        val table = TableModel("", 1, 4, "Indoor", true, null)
        val callback = mock<(Boolean, String) -> Unit>()

        repository.updateTable(table, callback)

        verify(callback).invoke(false, "Table ID cannot be empty")
        verify(mockTableRef, never()).setValue(any())
    }

    @Test
    fun `updateTable failure from firebase`() {
        val table = TableModel("table3", 3, 4, "Window-side", true, null)
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockRef.child(table.tableId)).thenReturn(mockTableRef)
        `when`(mockTableRef.setValue(table)).thenReturn(mockTask)
        doAnswer {
            val listener = it.arguments[0] as OnCompleteListener<Void>
            `when`(mockTask.isSuccessful).thenReturn(false)
            `when`(mockTask.exception).thenReturn(Exception("Update error"))
            listener.onComplete(mockTask)
            null
        }.`when`(mockTask).addOnCompleteListener(any())

        repository.updateTable(table, callback)

        verify(callback).invoke(false, "Update error")
    }

    @Test
    fun `getTableCount success`() {
        val callback = mock<(Boolean, Int) -> Unit>()

        doAnswer {
            val listener = it.arguments[0] as ValueEventListener
            val snapshot = mock<DataSnapshot>()
            `when`(snapshot.childrenCount).thenReturn(5L)
            listener.onDataChange(snapshot)
            null
        }.`when`(mockRef).addListenerForSingleValueEvent(any())

        repository.getTableCount(callback)

        verify(callback).invoke(true, 5)
    }

    @Test
    fun `getTableCount failure`() {
        val callback = mock<(Boolean, Int) -> Unit>()

        doAnswer {
            val listener = it.arguments[0] as ValueEventListener
            listener.onCancelled(mock())
            null
        }.`when`(mockRef).addListenerForSingleValueEvent(any())

        repository.getTableCount(callback)

        verify(callback).invoke(false, 0)
    }
}
