package com.example.gastosapp.data.remote.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseAuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth
) : AuthRemoteDataSource {
    override suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): AuthenticatedFirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(correo, contrasena).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Firebase no retornó usuario autenticado.")
        return AuthenticatedFirebaseUser(
            uid = firebaseUser.uid,
            correo = firebaseUser.email ?: correo,
            nombre = firebaseUser.displayName
        )
    }

    override fun obtenerUsuarioActual(): AuthenticatedFirebaseUser? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return AuthenticatedFirebaseUser(
            uid = firebaseUser.uid,
            correo = firebaseUser.email ?: "",
            nombre = firebaseUser.displayName
        )
    }

    private suspend fun Task<AuthResult>.await(): AuthResult {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null) {
                        continuation.resume(result)
                    } else {
                        continuation.resumeWithException(IllegalStateException("Firebase no retornó resultado."))
                    }
                } else {
                    continuation.resumeWithException(
                        task.exception ?: IllegalStateException("No se pudo iniciar sesión con Firebase.")
                    )
                }
            }
        }
    }
}
