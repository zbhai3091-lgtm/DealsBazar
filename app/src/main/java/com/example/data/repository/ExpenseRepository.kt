package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.AuthUserState
import com.example.data.model.BudgetConfig
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ExpenseRepository(private val context: Context) {

    private val tag = "ExpenseRepository"
    private val scope = CoroutineScope(Dispatchers.IO)

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    private var transactionListener: ListenerRegistration? = null
    private var budgetListener: ListenerRegistration? = null

    private val _transactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    private val _budgetConfig = MutableStateFlow(BudgetConfig())
    val budgetConfig: StateFlow<BudgetConfig> = _budgetConfig.asStateFlow()

    private val _userState = MutableStateFlow(AuthUserState())
    val userState: StateFlow<AuthUserState> = _userState.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        initFirebase()
    }

    private fun initFirebase() {
        try {
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                FirebaseApp.getInstance()
            }

            auth = FirebaseAuth.getInstance()

            val customDbId = try {
                val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
                if (resId != 0) context.getString(resId) else null
            } catch (e: Exception) {
                null
            }

            firestore = if (!customDbId.isNullOrEmpty() && app != null) {
                try {
                    FirebaseFirestore.getInstance(app, customDbId)
                } catch (e: Exception) {
                    FirebaseFirestore.getInstance()
                }
            } else {
                FirebaseFirestore.getInstance()
            }

            auth?.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                _userState.value = AuthUserState.fromFirebaseUser(user)
                if (user != null) {
                    startRealtimeSync(user.uid)
                } else {
                    stopRealtimeSync()
                    // If no user is logged in, auto sign in anonymously for seamless experience
                    scope.launch {
                        try {
                            auth?.signInAnonymously()?.await()
                        } catch (e: Exception) {
                            Log.w(tag, "Auto anon sign-in failed, working locally: ${e.message}")
                            loadDefaultSampleDataIfEmpty()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Firebase init failed: ${e.message}", e)
            loadDefaultSampleDataIfEmpty()
        }
    }

    private fun startRealtimeSync(uid: String) {
        stopRealtimeSync()
        val db = firestore ?: return

        try {
            // Observe transactions
            val transColl = db.collection("users").document(uid).collection("transactions")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            transactionListener = transColl.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Transactions listener error: ${error.message}")
                    _syncMessage.value = "Sync alert: ${error.localizedMessage}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { TransactionItem.fromMap(it) }
                    }
                    if (list.isEmpty() && _transactions.value.isEmpty()) {
                        // Seed helpful starter transactions
                        seedInitialStarterData(uid)
                    } else {
                        _transactions.value = list
                    }
                }
            }

            // Observe budget config
            val budgetDoc = db.collection("users").document(uid).collection("config").document("budget")
            budgetListener = budgetDoc.addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                    _budgetConfig.value = BudgetConfig.fromMap(snapshot.data!!)
                }
            }

        } catch (e: Exception) {
            Log.e(tag, "Error setting up realtime listeners", e)
        }
    }

    private fun stopRealtimeSync() {
        transactionListener?.remove()
        transactionListener = null
        budgetListener?.remove()
        budgetListener = null
    }

    private fun seedInitialStarterData(uid: String) {
        val now = System.currentTimeMillis()
        val starters = listOf(
            TransactionItem(
                title = "Monthly Salary / Aamdani",
                amount = 75000.0,
                type = TransactionType.INCOME,
                category = TransactionCategory.SALARY,
                timestamp = now - 86400000 * 2,
                notes = "Bank transfer deposit"
            ),
            TransactionItem(
                title = "Supermarket & Ration",
                amount = 8500.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.GROCERIES,
                timestamp = now - 86400000,
                notes = "Monthly groceries"
            ),
            TransactionItem(
                title = "Fuel & Petrol",
                amount = 3200.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.TRANSPORT,
                timestamp = now - 3600000 * 4,
                notes = "Full tank"
            ),
            TransactionItem(
                title = "Dinner & Snacks",
                amount = 1450.0,
                type = TransactionType.EXPENSE,
                category = TransactionCategory.FOOD,
                timestamp = now - 1800000,
                notes = "Weekend family dinner"
            )
        )

        _transactions.value = starters

        scope.launch {
            starters.forEach { item ->
                addTransaction(item)
            }
        }
    }

    private fun loadDefaultSampleDataIfEmpty() {
        if (_transactions.value.isEmpty()) {
            val now = System.currentTimeMillis()
            _transactions.value = listOf(
                TransactionItem(
                    title = "Monthly Salary",
                    amount = 75000.0,
                    type = TransactionType.INCOME,
                    category = TransactionCategory.SALARY,
                    timestamp = now - 86400000,
                    notes = "Monthly incoming"
                ),
                TransactionItem(
                    title = "Groceries & Ration",
                    amount = 6200.0,
                    type = TransactionType.EXPENSE,
                    category = TransactionCategory.GROCERIES,
                    timestamp = now,
                    notes = "Weekly grocery store"
                )
            )
        }
    }

    suspend fun addTransaction(item: TransactionItem): Result<Unit> {
        // Immediate local update for zero-latency UI
        _transactions.update { current ->
            listOf(item) + current.filter { it.id != item.id }
        }

        val uid = auth?.currentUser?.uid
        val db = firestore
        if (uid != null && db != null) {
            _isCloudSyncing.value = true
            return try {
                db.collection("users").document(uid).collection("transactions")
                    .document(item.id)
                    .set(item.toMap()).await()
                _syncMessage.value = "Transaction saved to cloud!"
                Result.success(Unit)
            } catch (e: Exception) {
                Log.w(tag, "Failed to sync transaction to Firestore: ${e.message}")
                _syncMessage.value = "Saved locally (Offline)"
                Result.success(Unit)
            } finally {
                _isCloudSyncing.value = false
            }
        }
        return Result.success(Unit)
    }

    suspend fun deleteTransaction(id: String): Result<Unit> {
        _transactions.update { current -> current.filter { it.id != id } }

        val uid = auth?.currentUser?.uid
        val db = firestore
        if (uid != null && db != null) {
            _isCloudSyncing.value = true
            return try {
                db.collection("users").document(uid).collection("transactions")
                    .document(id)
                    .delete().await()
                _syncMessage.value = "Transaction deleted from cloud"
                Result.success(Unit)
            } catch (e: Exception) {
                Log.w(tag, "Delete Firestore failed: ${e.message}")
                Result.success(Unit)
            } finally {
                _isCloudSyncing.value = false
            }
        }
        return Result.success(Unit)
    }

    suspend fun updateBudgetConfig(newConfig: BudgetConfig): Result<Unit> {
        _budgetConfig.value = newConfig
        val uid = auth?.currentUser?.uid
        val db = firestore
        if (uid != null && db != null) {
            return try {
                db.collection("users").document(uid).collection("config").document("budget")
                    .set(newConfig.toMap()).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.success(Unit)
            }
        }
        return Result.success(Unit)
    }

    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            auth?.signInAnonymously()?.await()
            _syncMessage.value = "Signed in anonymously"
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            auth?.signInWithEmailAndPassword(email.trim(), pass)?.await()
            _syncMessage.value = "Signed in as $email"
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, pass: String): Result<Unit> {
        return try {
            auth?.createUserWithEmailAndPassword(email.trim(), pass)?.await()
            _syncMessage.value = "Account created & synced"
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
        _syncMessage.value = "Signed out"
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
