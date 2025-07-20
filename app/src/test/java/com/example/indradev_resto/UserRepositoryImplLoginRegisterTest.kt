package com.example.indradev_resto

import com.example.indradev_resto.repository.UserRepositoryImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplLoginRegisterTest {

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockRef: DatabaseReference

    @Mock
    private lateinit var mockChildRef: DatabaseReference

    @Mock
    private lateinit var mockTaskAuth: Task<AuthResult>

    @Mock
    private lateinit var mockUpdateTask: Task<Void>

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock database.reference to return mockRef (root)
        `when`(mockDatabase.reference).thenReturn(mockRef)

        // Mock mockRef.child("users") returns mockChildRef
        `when`(mockRef.child("users")).thenReturn(mockChildRef)

        // For editProfile, the code does: usersRef.child(userId).updateChildren(...)
        // So mock that mockChildRef.child(userId) returns another mock (simulate deeper child)
        `when`(mockChildRef.child(anyString())).thenReturn(mockChildRef)

        // Mock updateChildren to return mockUpdateTask
        `when`(mockChildRef.updateChildren(any<Map<String, Any>>())).thenReturn(mockUpdateTask)

        userRepository = UserRepositoryImpl(
            auth = mockAuth,
            database = mockDatabase,
            ref = mockChildRef
        )
    }

    @Test
    fun `test login success`() {
        val email = "test@gmail.com"
        val password = "password"
        val callback = mock<(Boolean, String) -> Unit>()

        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTaskAuth)

        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer {
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            `when`(mockTaskAuth.isSuccessful).thenReturn(true)
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }

        userRepository.login(email, password, callback)

        verify(callback).invoke(true, "Login Successful")
    }

    @Test
    fun `test register failure`() {
        val email = "fail@gmail.com"
        val password = "123"
        val callback = mock<(Boolean, String, String) -> Unit>()

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTaskAuth)

        `when`(mockTaskAuth.addOnCompleteListener(any())).thenAnswer {
            val listener = it.arguments[0] as OnCompleteListener<AuthResult>
            `when`(mockTaskAuth.isSuccessful).thenReturn(false)
            `when`(mockTaskAuth.exception).thenReturn(Exception("Registration failed"))
            listener.onComplete(mockTaskAuth)
            mockTaskAuth
        }

        userRepository.register(email, password, callback)

        verify(callback).invoke(false, "Registration failed", "")
    }

    @Test
    fun `test editProfile success`() {
        val userId = "testUserId"
        val data = mutableMapOf<String, Any?>(
            "name" to "Updated Name",
            "phone" to "1234567890",
            "email" to null  // This should be filtered out by the implementation
        )
        val callback = mock<(Boolean, String) -> Unit>()

        // Mock updateChildren success callback
        doAnswer {
            val successListener = it.arguments[0] as com.google.android.gms.tasks.OnSuccessListener<Void>
            successListener.onSuccess(null)
            mockUpdateTask
        }.`when`(mockUpdateTask).addOnSuccessListener(any())

        // Mock updateChildren failure callback (not called in this test)
        `when`(mockUpdateTask.addOnFailureListener(any())).thenReturn(mockUpdateTask)

        userRepository.editProfile(userId, data, callback)

        // Verify updateChildren called with filtered map (no null values)
        val expectedMap = mapOf(
            "name" to "Updated Name",
            "phone" to "1234567890"
        )
        verify(mockChildRef).updateChildren(expectedMap)

        // Verify callback invoked with success
        verify(callback).invoke(true, "Profile updated successfully")
    }

    @Test
    fun `test editProfile failure`() {
        val userId = "testUserId"
        val data = mutableMapOf<String, Any?>(
            "name" to "Updated Name"
        )
        val callback = mock<(Boolean, String) -> Unit>()

        // Mock updateChildren failure callback
        doAnswer {
            val failureListener = it.arguments[0] as com.google.android.gms.tasks.OnFailureListener
            failureListener.onFailure(Exception("Update failed"))
            mockUpdateTask
        }.`when`(mockUpdateTask).addOnFailureListener(any())

        // Mock updateChildren success callback (not called in this test)
        `when`(mockUpdateTask.addOnSuccessListener(any())).thenReturn(mockUpdateTask)

        userRepository.editProfile(userId, data, callback)

        val expectedMap = mapOf("name" to "Updated Name")
        verify(mockChildRef).updateChildren(expectedMap)

        verify(callback).invoke(false, "Update failed")
    }
}
