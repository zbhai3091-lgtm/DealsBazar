package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetConfig
import com.example.data.model.FinancialSummary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import java.util.Calendar
import java.util.Locale

@Composable
fun BudgetScreen(
    summary: FinancialSummary,
    budgetConfig: BudgetConfig,
    isEditDialogOpen: Boolean,
    budgetInput: String,
    currencyInput: String,
    onOpenEdit: () -> Unit,
    onCloseEdit: () -> Unit,
    onSaveBudget: (limit: Double, currency: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingBudget = budgetConfig.monthlyLimit - summary.totalExpense
    val spentPercentage = if (budgetConfig.monthlyLimit > 0) {
        (summary.totalExpense / budgetConfig.monthlyLimit).toFloat()
    } else 0f

    val daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val remainingDays = (daysInMonth - currentDay + 1).coerceAtLeast(1)
    val dailyAllowance = (remainingBudget / remainingDays).coerceAtLeast(0.0)

    val progressColor = when {
        spentPercentage >= 1.0f -> StatusError
        spentPercentage >= 0.8f -> StatusWarning
        else -> StatusSuccess
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mahana Budget / Monthly Limit",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Allocated: ${budgetConfig.currencySymbol} ${String.format(Locale.getDefault(), "%,.0f", budgetConfig.monthlyLimit)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenEdit,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("edit_budget_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { spentPercentage.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kharch hua: ${(spentPercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = progressColor
                        )
                        Text(
                            text = if (remainingBudget >= 0)
                                "Bacha: ${budgetConfig.currencySymbol} ${String.format(Locale.getDefault(), "%,.0f", remainingBudget)}"
                            else
                                "Exceeded by: ${budgetConfig.currencySymbol} ${String.format(Locale.getDefault(), "%,.0f", -remainingBudget)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingBudget >= 0) StatusSuccess else StatusError
                        )
                    }
                }
            }
        }

        // Daily Target Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📅", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Rozana Kharch Karne Ki Had (Daily Target)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${budgetConfig.currencySymbol} ${String.format(Locale.getDefault(), "%,.0f", dailyAllowance)} / day (Agley $remainingDays din ke liye)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Tips Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Smart Budget Tips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TipItem("💡 Rozana kharche raat ko add karein taake koi hisab na chhoote.")
                    TipItem("💡 Monthly salary aate hi 20% savings alag kar lein.")
                    TipItem("💡 Agar budget 80% se upar jaye to gair-zaroori kharche control karein.")
                }
            }
        }
    }

    if (isEditDialogOpen) {
        var limitText by remember { mutableStateOf(budgetInput) }
        var currText by remember { mutableStateOf(currencyInput) }

        AlertDialog(
            onDismissRequest = onCloseEdit,
            title = { Text("Mahana Budget Set Karein") },
            text = {
                Column {
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it.filter { c -> c.isDigit() } },
                        label = { Text("Budget Limit") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_budget")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = currText,
                        onValueChange = { currText = it },
                        label = { Text("Currency Symbol (e.g. Rs., $, ₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_currency")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val limit = limitText.toDoubleOrNull() ?: budgetConfig.monthlyLimit
                        onSaveBudget(limit, currText)
                    },
                    modifier = Modifier.testTag("save_budget_btn")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseEdit) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TipItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}
