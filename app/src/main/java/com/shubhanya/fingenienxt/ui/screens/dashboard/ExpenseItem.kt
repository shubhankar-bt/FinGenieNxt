package com.shubhanya.fingenienxt.ui.screens.transactions // Or a common composables package

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubhanya.fingenienxt.data.local.entity.Expense
import com.shubhanya.fingenienxt.expense.CategoryVisuals
// CRITICAL: Ensure this imports your Firestore Expense data class
// Import your new theme colors if needed directly, or use MaterialTheme.colorScheme
import com.shubhanya.fingenienxt.ui.theme.DarkOnError // Example if using directly
import com.shubhanya.fingenienxt.ui.theme.DarkPrimaryBlue // Example

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseItem(
    expense: Expense,
    formatter: DecimalFormat,
    onItemClick: (Expense) -> Unit,
    onDeleteConfirm: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            text = { Text("Are you sure you want to delete '${expense.description}'?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirm(expense)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(expense) }
            .padding(vertical = 14.dp, horizontal = 8.dp), // Increased vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon with themed background
        Box(
            modifier = Modifier
                .size(42.dp) // Slightly larger icon container
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)), // Use primary container from your dark theme
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CategoryVisuals.getIcon(expense.category),
                contentDescription = expense.category,
                tint = MaterialTheme.colorScheme.onPrimaryContainer, // Good contrast on primary container
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // Description, Category & Time
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium, // Adjusted weight
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface // Use onSurface from your dark theme
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${expense.category} • ${timeFormatter.format(expense.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Use onSurfaceVariant
            )
        }

        Spacer(Modifier.width(10.dp)) // Space before amount

        // Amount
        Text(
            text = "-${formatter.format(expense.amount)}",
            style = MaterialTheme.typography.bodyLarge, // Consistent size
            fontWeight = FontWeight.Bold, // Bolder amount
            color = MaterialTheme.colorScheme.error, // Use error color from your dark theme (e.g., DarkErrorRed)
            textAlign = TextAlign.End
        )

        // Delete Action Icon
        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.size(40.dp).padding(start = 6.dp)
        ) {
            Icon(
                Icons.Filled.DeleteOutline,
                contentDescription = "Delete Transaction",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) // Subtle delete
            )
        }
    }
}
