package com.example.data.model

import java.util.UUID

enum class TransactionType(val label: String) {
    EXPENSE("Kharcha / Expense"),
    INCOME("Aamdani / Income")
}

enum class TransactionCategory(
    val title: String,
    val iconEmoji: String,
    val hexColor: String,
    val defaultType: TransactionType
) {
    FOOD("Food & Dining", "🍔", "#F59E0B", TransactionType.EXPENSE),
    BILLS("Bills & Utilities", "💡", "#3B82F6", TransactionType.EXPENSE),
    SHOPPING("Shopping", "🛍️", "#EC4899", TransactionType.EXPENSE),
    TRANSPORT("Fuel & Transport", "🚗", "#6366F1", TransactionType.EXPENSE),
    HEALTH("Health & Medical", "💊", "#EF4444", TransactionType.EXPENSE),
    GROCERIES("Ration & Groceries", "🛒", "#10B981", TransactionType.EXPENSE),
    ENTERTAINMENT("Fun & Entertainment", "🎬", "#8B5CF6", TransactionType.EXPENSE),
    SALARY("Salary / Tankhwah", "💵", "#059669", TransactionType.INCOME),
    BUSINESS("Business / Profit", "📈", "#0284C7", TransactionType.INCOME),
    INVESTMENT("Investment Returns", "🪙", "#D97706", TransactionType.INCOME),
    OTHER("Other / Deegar", "📦", "#6B7280", TransactionType.EXPENSE);

    companion object {
        fun fromName(name: String?): TransactionCategory {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}

data class TransactionItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "amount" to amount,
        "type" to type.name,
        "category" to category.name,
        "timestamp" to timestamp,
        "notes" to notes
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): TransactionItem {
            val typeStr = map["type"] as? String ?: TransactionType.EXPENSE.name
            val type = try { TransactionType.valueOf(typeStr) } catch (e: Exception) { TransactionType.EXPENSE }
            val catStr = map["category"] as? String
            val category = TransactionCategory.fromName(catStr)

            return TransactionItem(
                id = (map["id"] as? String) ?: UUID.randomUUID().toString(),
                title = (map["title"] as? String) ?: "Untitled",
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                type = type,
                category = category,
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                notes = (map["notes"] as? String) ?: ""
            )
        }
    }
}

data class BudgetConfig(
    val monthlyLimit: Double = 50000.0,
    val currencySymbol: String = "Rs.",
    val budgetAlertsEnabled: Boolean = true
) {
    fun toMap(): Map<String, Any> = mapOf(
        "monthlyLimit" to monthlyLimit,
        "currencySymbol" to currencySymbol,
        "budgetAlertsEnabled" to budgetAlertsEnabled
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): BudgetConfig {
            return BudgetConfig(
                monthlyLimit = (map["monthlyLimit"] as? Number)?.toDouble() ?: 50000.0,
                currencySymbol = (map["currencySymbol"] as? String) ?: "Rs.",
                budgetAlertsEnabled = (map["budgetAlertsEnabled"] as? Boolean) ?: true
            )
        }
    }
}

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val categoryTotals: Map<TransactionCategory, Double> = emptyMap(),
    val spentPercentageOfBudget: Float = 0f
)
