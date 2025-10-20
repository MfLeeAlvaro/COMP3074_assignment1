package com.example.comp3074_assignment1

import com.example.comp3074_assignment1.pay.PayCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class PayCalculatorTest {

    @Test
    fun noOvertime_10h_at20_tax10() {
        val r = PayCalculator.calculate(10.0, 20.0, 10.0)
        assertEquals(200.0, r.basePay, 0.001)
        assertEquals(0.0, r.overtimePay, 0.001)
        assertEquals(200.0, r.gross, 0.001)
        assertEquals(20.0, r.taxAmount, 0.001)
        assertEquals(180.0, r.net, 0.001)
    }

    @Test
    fun withOvertime_45h_at20_tax0() {
        val r = PayCalculator.calculate(45.0, 20.0, 0.0)
        assertEquals(800.0, r.basePay, 0.001)     // 40 * 20
        assertEquals(150.0, r.overtimePay, 0.001) // 5 * 20 * 1.5
        assertEquals(950.0, r.gross, 0.001)
        assertEquals(0.0, r.taxAmount, 0.001)
        assertEquals(950.0, r.net, 0.001)
    }
}
