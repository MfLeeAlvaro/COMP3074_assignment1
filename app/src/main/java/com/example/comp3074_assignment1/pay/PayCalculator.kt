package com.example.comp3074_assignment1.pay

data class PayBreakdown(
    val basePay: Double,
    val overtimePay: Double,
    val gross: Double,
    val taxAmount: Double,
    val net: Double
)

object PayCalculator {
    private const val OVERTIME_THRESHOLD = 40.0
    private const val OVERTIME_MULTIPLIER = 1.5

    fun calculate(hours: Double, rate: Double, taxPercent: Double): PayBreakdown {
        require(hours >= 0) { "Hours cannot be negative" }
        require(rate >= 0) { "Rate cannot be negative" }
        require(taxPercent in 0.0..100.0) { "Tax must be 0–100" }

        val baseHours = minOf(hours, OVERTIME_THRESHOLD)
        val overtimeHours = (hours - OVERTIME_THRESHOLD).coerceAtLeast(0.0)

        val basePay = baseHours * rate
        val overtimePay = overtimeHours * rate * OVERTIME_MULTIPLIER
        val gross = basePay + overtimePay
        val taxAmount = gross * (taxPercent / 100.0)
        val net = gross - taxAmount

        return PayBreakdown(
            basePay.round2(), overtimePay.round2(), gross.round2(),
            taxAmount.round2(), net.round2()
        )
    }

    private fun Double.round2() = kotlin.math.round(this * 100) / 100.0
}
