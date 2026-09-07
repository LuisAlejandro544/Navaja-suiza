package com.example

import com.example.domain.CalculatorEngine
import com.example.domain.EvaluationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {

    @Test
    fun `test basic operations`() {
        val res1 = CalculatorEngine.evaluate("2+3")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals("5", (res1 as EvaluationResult.Success).formattedValue)

        val res2 = CalculatorEngine.evaluate("10-4")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals("6", (res2 as EvaluationResult.Success).formattedValue)

        val res3 = CalculatorEngine.evaluate("6*7")
        assertTrue(res3 is EvaluationResult.Success)
        assertEquals("42", (res3 as EvaluationResult.Success).formattedValue)

        val res4 = CalculatorEngine.evaluate("20/4")
        assertTrue(res4 is EvaluationResult.Success)
        assertEquals("5", (res4 as EvaluationResult.Success).formattedValue)
    }

    @Test
    fun `test operator precedence and parentheses`() {
        val res1 = CalculatorEngine.evaluate("2+3*4")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals("14", (res1 as EvaluationResult.Success).formattedValue)

        val res2 = CalculatorEngine.evaluate("(2+3)*4")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals("20", (res2 as EvaluationResult.Success).formattedValue)

        val res3 = CalculatorEngine.evaluate("2^3+1")
        assertTrue(res3 is EvaluationResult.Success)
        assertEquals("9", (res3 as EvaluationResult.Success).formattedValue)
    }

    @Test
    fun `test decimals and precision`() {
        val res = CalculatorEngine.evaluate("0.1+0.2")
        assertTrue(res is EvaluationResult.Success)
        assertEquals("0.3", (res as EvaluationResult.Success).formattedValue)
    }

    @Test
    fun `test division by zero`() {
        val res = CalculatorEngine.evaluate("5/0")
        assertTrue(res is EvaluationResult.Error)
        assertEquals("No se puede dividir por cero", (res as EvaluationResult.Error).message)
    }

    @Test
    fun `test scientific square root and powers`() {
        val res1 = CalculatorEngine.evaluate("√(16)")
        assertTrue(res1 is EvaluationResult.Success)
        assertEquals("4", (res1 as EvaluationResult.Success).formattedValue)

        val res2 = CalculatorEngine.evaluate("2^8")
        assertTrue(res2 is EvaluationResult.Success)
        assertEquals("256", (res2 as EvaluationResult.Success).formattedValue)
    }

    @Test
    fun `test preview evaluation`() {
        assertEquals("5", CalculatorEngine.evaluatePreview("2+3"))
        assertEquals("5", CalculatorEngine.evaluatePreview("2+3+"))
        assertEquals("14", CalculatorEngine.evaluatePreview("2+3*4"))
        assertEquals("5", CalculatorEngine.evaluatePreview("(2+3"))
    }
}
