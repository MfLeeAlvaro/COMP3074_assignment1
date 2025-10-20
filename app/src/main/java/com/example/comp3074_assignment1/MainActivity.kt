package com.example.comp3074_assignment1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.comp3074_assignment1.pay.PayBreakdown
import com.example.comp3074_assignment1.pay.PayCalculator
import com.example.comp3074_assignment1.ui.theme.COMP3074_assignment1Theme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            COMP3074_assignment1Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PayCalculatorScreen()
                }
            }
        }
    }
}

@Composable
private fun PayCalculatorScreen() {
    val focus = LocalFocusManager.current
    val context = LocalContext.current

    var hours by rememberSaveable { mutableStateOf("") }
    var rate  by rememberSaveable { mutableStateOf("") }
    var tax   by rememberSaveable { mutableStateOf("10") } // default 10%

    var errHours by remember { mutableStateOf<String?>(null) }
    var errRate  by remember { mutableStateOf<String?>(null) }
    var errTax   by remember { mutableStateOf<String?>(null) }

    var result by remember { mutableStateOf<PayBreakdown?>(null) }

    fun validate(): Boolean {
        errHours = validateNumber(hours, "Hours") { it >= 0 }
        errRate  = validateNumber(rate,  "Rate")  { it >= 0 }
        errTax   = validateNumber(tax,   "Tax %") { it in 0.0..100.0 }
        return errHours == null && errRate == null && errTax == null
    }

    fun onCalculate() {
        if (!validate()) return
        val h = hours.toDouble()
        val r = rate.toDouble()
        val t = tax.toDouble()
        result = PayCalculator.calculate(h, r, t)
        focus.clearFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Pay Calculator", style = MaterialTheme.typography.headlineMedium)

        NumberField("Hours worked", hours, { hours = it }, errHours)
        NumberField("Hourly rate",  rate,  { rate  = it }, errRate, prefix = "$")
        NumberField("Tax (%)",      tax,   { tax   = it }, errTax,  suffix = "%")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onCalculate() }) { Text("Calculate") }
            OutlinedButton(onClick = {
                hours = ""; rate = ""; tax = "10"
                errHours = null; errRate = null; errTax = null
                result = null
            }) { Text("Reset") }
        }

        // About button to open AboutActivity
        TextButton(onClick = {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }) { Text("About") }

        HorizontalDivider()
        ResultCard(result)
    }
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    prefix: String? = null,
    suffix: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // allow empty or valid numeric input (ints/decimals)
            if (input.isEmpty() || input.matches(Regex("""\d*\.?\d*"""))) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        leadingIcon = if (prefix != null) ({ Text(prefix) }) else null,
        trailingIcon = if (suffix != null) ({ Text(suffix) }) else null,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResultCard(result: PayBreakdown?) {
    if (result == null) {
        Text("Enter values and tap Calculate to see results.")
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RowLine("Base Pay", currency(result.basePay))
            RowLine("Overtime Pay", currency(result.overtimePay))
            HorizontalDivider()
            RowLine("Gross", currency(result.gross))
            RowLine("Tax", "- ${currency(result.taxAmount)}")
            HorizontalDivider()
            RowLine("Net Pay", currency(result.net), bold = true)
        }
    }
}

@Composable
private fun RowLine(label: String, value: String, bold: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val style = if (bold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge
        Text(label, style = style)
        Text(value, style = style)
    }
}

private fun currency(x: Double) = "$" + String.format(Locale.getDefault(), "%,.2f", x)

private fun validateNumber(text: String, field: String, rule: (Double) -> Boolean): String? {
    if (text.isBlank()) return "$field is required."
    val num = text.toDoubleOrNull()
    return when {
        num == null -> "$field must be a number."
        !rule(num) -> "$field is out of range."
        else -> null
    }
}
