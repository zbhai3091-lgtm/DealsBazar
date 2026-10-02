package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthUserState
import com.example.data.model.BudgetConfig
import com.example.data.model.FinancialSummary
import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionItem
import com.example.data.model.TransactionType
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    HOME("Khata"),
    ANALYTICS("Analytics"),
    BUDGET("Budget"),
    CLOUD("Cloud Sync")
}

data class ExpenseUiState(
    val selectedTab: AppNavTab = AppNavTab.HOME,
    val selectedFilterType: TransactionType? = null,
    val searchQuery: String = "",
    val showAddDialog: Boolean = false,
    val showAuthDialog: Boolean = false,
    val isRegisterMode: Boolean = false,
    val emailInput: String = "",
    val passwordInput: String = "",
    val editBudgetDialog: Boolean = false,
    val budgetInput: String = "",
    val currencyInput: String = "Rs.",
    val snackbarMessage: String? = null
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(application)

    val transactions: StateFlow<List<TransactionItem>> = repository.transactions
    val budgetConfig: StateFlow<BudgetConfig> = repository.budgetConfig
    val userState: StateFlow<AuthUserState> = repository.userState
    val isCloudSyncing: StateFlow<Boolean> = repository.isCloudSyncing

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    // Calculated financial summary derived from transactions and budget
    val summary: StateFlow<FinancialSummary> = combine(transactions, budgetConfig) { transList, budget ->
        val income = transList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance = income - expense

        val catMap = mutableMapOf<TransactionCategory, Double>()
        transList.filter { it.type == TransactionType.EXPENSE }.forEach { item ->
            catMap[item.category] = (catMap[item.category] ?: 0.0) + item.amount
        }

        val percentage = if (budget.monthlyLimit > 0) {
            ((expense / budget.monthlyLimit).toFloat()).coerceAtLeast(0f)
        } else 0f

        FinancialSummary(
            totalIncome = income,
            totalExpense = expense,
            netBalance = balance,
            categoryTotals = catMap.toList().sortedByDescending { it.second }.toMap(),
            spentPercentageOfBudget = percentage
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialSummary()
    )

    init {
        viewModelScope.launch {
            repository.syncMessage.collect { msg ->
                if (msg != null) {
                    _uiState.update { it.copy(snackbarMessage = msg) }
                    repository.clearSyncMessage()
                }
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setFilterType(type: TransactionType?) {
        _uiState.update { it.copy(selectedFilterType = type) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun closeAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun addTransaction(title: String, amount: Double, type: TransactionType, category: TransactionCategory, notes: String) {
        val newItem = TransactionItem(
            title = title.ifBlank { category.title },
            amount = amount,
            type = type,
            category = category,
            notes = notes
        )
        viewModelScope.launch {
            repository.addTransaction(newItem)
            _uiState.update { it.copy(showAddDialog = false, snackbarMessage = "Transaction added!") }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            _uiState.update { it.copy(snackbarMessage = "Transaction deleted") }
        }
    }

    fun openEditBudget() {
        _uiState.update {
            it.copy(
                editBudgetDialog = true,
                budgetInput = budgetConfig.value.monthlyLimit.toInt().toString(),
                currencyInput = budgetConfig.value.currencySymbol
            )
        }
    }

    fun closeEditBudget() {
        _uiState.update { it.copy(editBudgetDialog = false) }
    }

    fun saveBudget(limit: Double, currency: String) {
        viewModelScope.launch {
            repository.updateBudgetConfig(
                budgetConfig.value.copy(
                    monthlyLimit = limit,
                    currencySymbol = currency.ifBlank { "Rs." }
                )
            )
            _uiState.update { it.copy(editBudgetDialog = false, snackbarMessage = "Monthly budget updated!") }
        }
    }

    // Auth functions
    fun openAuthDialog(register: Boolean) {
        _uiState.update {
            it.copy(
                showAuthDialog = true,
                isRegisterMode = register,
                emailInput = "",
                passwordInput = ""
            )
        }
    }

    fun closeAuthDialog() {
        _uiState.update { it.copy(showAuthDialog = false) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(emailInput = email) }
    }

    fun updatePassword(pass: String) {
        _uiState.update { it.copy(passwordInput = pass) }
    }

    fun submitAuth() {
        val email = _uiState.value.emailInput.trim()
        val pass = _uiState.value.passwordInput
        if (!email.contains("@")) {
            _uiState.update { it.copy(snackbarMessage = "Enter valid email") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(snackbarMessage = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            val res = if (_uiState.value.isRegisterMode) {
                repository.registerWithEmail(email, pass)
            } else {
                repository.signInWithEmail(email, pass)
            }
            if (res.isSuccess) {
                _uiState.update { it.copy(showAuthDialog = false) }
            } else {
                _uiState.update { it.copy(snackbarMessage = res.exceptionOrNull()?.localizedMessage ?: "Auth failed") }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            repository.signInAnonymously()
        }
    }

    fun signOut() {
        repository.signOut()
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
