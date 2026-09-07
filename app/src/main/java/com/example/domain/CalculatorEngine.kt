package com.example.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.*

sealed class EvaluationResult {
    data class Success(val formattedValue: String, val rawValue: Double) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

object CalculatorEngine {

    private const val MAX_DECIMALS = 10

    /**
     * Evaluates a mathematical string expression and returns either a Success or Error.
     * @param isDegrees Whether trigonometric functions should use degrees (true) or radians (false).
     */
    fun evaluate(rawExpression: String, isDegrees: Boolean = true): EvaluationResult {
        if (rawExpression.isBlank()) {
            return EvaluationResult.Error("Expresión vacía")
        }

        try {
            val sanitized = sanitize(rawExpression)
            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) {
                return EvaluationResult.Error("Expresión vacía")
            }

            val rpn = shuntingYard(tokens)
            val rawValue = evaluateRpn(rpn, isDegrees)

            if (rawValue.isNaN()) {
                return EvaluationResult.Error("Resultado indefinido")
            }
            if (rawValue.isInfinite()) {
                return EvaluationResult.Error("No se puede dividir por cero")
            }

            val formatted = formatResult(rawValue)
            return EvaluationResult.Success(formattedValue = formatted, rawValue = rawValue)
        } catch (e: ArithmeticException) {
            return EvaluationResult.Error(e.message ?: "Error aritmético")
        } catch (e: Exception) {
            return EvaluationResult.Error("Error en la expresión")
        }
    }

    /**
     * Attempts a quiet evaluation for live preview. Returns null if expression is incomplete or invalid.
     */
    fun evaluatePreview(rawExpression: String, isDegrees: Boolean = true): String? {
        if (rawExpression.isBlank()) return null
        // If expression ends with a trailing operator, strip it for preview
        var expr = rawExpression.trim()
        while (expr.isNotEmpty() && isTrailingOperator(expr.last())) {
            expr = expr.dropLast(1).trim()
        }
        if (expr.isEmpty()) return null

        // Balance open parentheses for preview evaluation
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        if (openCount > closeCount) {
            expr += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(expr, isDegrees)) {
            is EvaluationResult.Success -> res.formattedValue
            is EvaluationResult.Error -> null
        }
    }

    private fun isTrailingOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '−' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^' || c == '('
    }

    private fun sanitize(input: String): String {
        return input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "${Math.PI}")
            .replace("e", "${Math.E}")
            .replace(" ", "")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }

                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < len && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }

                c == '√' -> {
                    tokens.add("sqrt")
                    i++
                }

                c == '(' || c == ')' || c == '+' || c == '*' || c == '/' || c == '^' || c == '%' || c == '!' -> {
                    tokens.add(c.toString())
                    i++
                }

                c == '-' -> {
                    // Check if unary minus
                    val isUnary = tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "(", "^", "sqrt")
                    if (isUnary) {
                        tokens.add("neg")
                    } else {
                        tokens.add("-")
                    }
                    i++
                }

                else -> {
                    i++
                }
            }
        }

        // Insert implicit multiplications: e.g., "2", "(" -> "2", "*", "("
        val expanded = mutableListOf<String>()
        for (idx in tokens.indices) {
            val curr = tokens[idx]
            expanded.add(curr)

            if (idx + 1 < tokens.size) {
                val next = tokens[idx + 1]
                val currIsOperand = isNumber(curr) || curr == ")"
                val nextIsOperand = isNumber(next) || next == "(" || isFunction(next)

                if (currIsOperand && nextIsOperand) {
                    expanded.add("*")
                }
            }
        }

        return expanded
    }

    private fun isNumber(token: String): Boolean {
        return token.toDoubleOrNull() != null
    }

    private fun isFunction(token: String): Boolean {
        return token in listOf("sin", "cos", "tan", "ln", "log", "sqrt", "fact")
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "neg" -> 3
            "^" -> 4
            "!" -> 5
            else -> 0
        }
    }

    private fun isRightAssociative(op: String): Boolean {
        return op == "^" || op == "neg"
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()

        for (token in tokens) {
            when {
                isNumber(token) -> {
                    output.add(token)
                }

                isFunction(token) -> {
                    stack.addLast(token)
                }

                token == "(" -> {
                    stack.addLast(token)
                }

                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeLast())
                    }
                    if (stack.isNotEmpty() && stack.last() == "(") {
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty() && isFunction(stack.last())) {
                        output.add(stack.removeLast())
                    }
                }

                else -> { // Operator
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        val top = stack.last()
                        val hasHigherPrecedence = if (isRightAssociative(token)) {
                            precedence(top) > precedence(token)
                        } else {
                            precedence(top) >= precedence(token)
                        }
                        if (hasHigherPrecedence) {
                            output.add(stack.removeLast())
                        } else {
                            break
                        }
                    }
                    stack.addLast(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeLast()
            if (top != "(" && top != ")") {
                output.add(top)
            }
        }

        return output
    }

    private fun evaluateRpn(rpn: List<String>, isDegrees: Boolean): Double {
        val stack = ArrayDeque<Double>()

        for (token in rpn) {
            when {
                isNumber(token) -> {
                    stack.addLast(token.toDouble())
                }

                token == "neg" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Expresión inválida")
                    stack.addLast(-stack.removeLast())
                }

                token == "!" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Expresión inválida")
                    val a = stack.removeLast()
                    if (a < 0 || a != a.toLong().toDouble() || a > 100) {
                        throw ArithmeticException("Factorial inválido")
                    }
                    var fact = 1.0
                    for (k in 1..a.toLong()) {
                        fact *= k
                    }
                    stack.addLast(fact)
                }

                isFunction(token) -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Expresión inválida")
                    val a = stack.removeLast()
                    val res = when (token) {
                        "sin" -> sin(if (isDegrees) Math.toRadians(a) else a)
                        "cos" -> cos(if (isDegrees) Math.toRadians(a) else a)
                        "tan" -> tan(if (isDegrees) Math.toRadians(a) else a)
                        "ln" -> {
                            if (a <= 0) throw ArithmeticException("ln requiere valor > 0")
                            ln(a)
                        }
                        "log" -> {
                            if (a <= 0) throw ArithmeticException("log requiere valor > 0")
                            log10(a)
                        }
                        "sqrt" -> {
                            if (a < 0) throw ArithmeticException("Raíz de número negativo")
                            sqrt(a)
                        }
                        "fact" -> {
                            if (a < 0 || a != a.toLong().toDouble() || a > 100) {
                                throw ArithmeticException("Factorial inválido")
                            }
                            var fact = 1.0
                            for (k in 1..a.toLong()) {
                                fact *= k
                            }
                            fact
                        }
                        else -> throw IllegalArgumentException("Función desconocida: $token")
                    }
                    stack.addLast(res)
                }

                token in listOf("+", "-", "*", "/", "%", "^") -> {
                    if (stack.size < 2) throw IllegalArgumentException("Expresión incompleta")
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    val res = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (abs(b) < 1e-15) throw ArithmeticException("No se puede dividir por cero")
                            a / b
                        }
                        "%" -> a % b
                        "^" -> a.pow(b)
                        else -> throw IllegalArgumentException("Operador desconocido: $token")
                    }
                    stack.addLast(res)
                }

                else -> throw IllegalArgumentException("Elemento desconocido: $token")
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Expresión inválida")
        }

        return stack.removeLast()
    }

    /**
     * Formats numbers to avoid floating point precision issues (e.g. 0.1 + 0.2 = 0.3)
     */
    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "Infinito"

        // Handle integers or values very close to integers
        val roundedLong = value.roundToLong()
        if (abs(value - roundedLong) < 1e-10) {
            return roundedLong.toString()
        }

        // Format with BigDecimal to eliminate IEEE 754 precision artifacts
        return try {
            val bd = BigDecimal(value, MathContext(12, RoundingMode.HALF_UP))
            val scaled = bd.setScale(MAX_DECIMALS, RoundingMode.HALF_UP).stripTrailingZeros()
            val plain = scaled.toPlainString()

            // If string is excessively long in plain format, use standard string format
            if (plain.length > 15) {
                String.format(java.util.Locale.US, "%.6e", value)
            } else {
                plain
            }
        } catch (e: Exception) {
            value.toString()
        }
    }
}
