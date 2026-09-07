package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.data.CalculationRepository
import com.example.domain.CalculatorEngine
import com.example.domain.EvaluationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String? = null,
    val errorMessage: String? = null,
    val isScientificExpanded: Boolean = false,
    val isDegrees: Boolean = true, // DEG vs RAD
    val isInverse: Boolean = false, // INV mode
    val isHistoryVisible: Boolean = false,
    val toastMessage: String? = null
)

class CalculatorViewModel(
    application: Application,
    private val repository: CalculationRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<CalculationEntity>> = repository.allCalculations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var justCalculated = false

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (justCalculated) {
                justCalculated = false
                digit
            } else {
                if (state.expression == "0") digit else state.expression + digit
            }
            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun onDecimal() {
        _uiState.update { state ->
            val expr = if (justCalculated) {
                justCalculated = false
                "0"
            } else {
                state.expression
            }

            // Find last number token to ensure it doesn't already contain a decimal point
            val lastToken = expr.takeLastWhile { it.isDigit() || it == '.' }
            if (lastToken.contains('.')) {
                state
            } else {
                val newExpr = if (expr.isEmpty() || !expr.last().isDigit()) {
                    "$expr" + "0."
                } else {
                    "$expr."
                }
                state.copy(
                    expression = newExpr,
                    previewResult = CalculatorEngine.evaluatePreview(newExpr),
                    errorMessage = null
                )
            }
        }
    }

    fun onOperator(op: String) {
        justCalculated = false
        _uiState.update { state ->
            var expr = state.expression

            if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    return@update state.copy(expression = "−", errorMessage = null)
                }
                return@update state
            }

            // If expression ends with an operator, replace it
            val lastChar = expr.last()
            val isLastOperator = lastChar == '+' || lastChar == '−' || lastChar == '-' ||
                    lastChar == '×' || lastChar == '*' || lastChar == '÷' || lastChar == '/' || lastChar == '^'

            val newExpr = if (isLastOperator) {
                expr.dropLast(1) + op
            } else {
                expr + op
            }

            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun onParenthesis() {
        justCalculated = false
        _uiState.update { state ->
            val expr = state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val shouldClose = openCount > closeCount && expr.isNotEmpty() &&
                    (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')

            val newExpr = if (shouldClose) {
                "$expr)"
            } else {
                if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                    "$expr×("
                } else {
                    "$expr("
                }
            }

            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun onScientificFunction(fn: String) {
        justCalculated = false
        _uiState.update { state ->
            val expr = state.expression
            val newExpr = when (fn) {
                "π", "e" -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "$expr×$fn"
                    } else {
                        "$expr$fn"
                    }
                }
                "√" -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "$expr×√("
                    } else {
                        "${expr}√("
                    }
                }
                "%" -> {
                    if (expr.isNotEmpty() && expr.last().isDigit()) {
                        "$expr%"
                    } else {
                        expr
                    }
                }
                "^" -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                        "$expr^"
                    } else {
                        expr
                    }
                }
                "!" -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "$expr!"
                    } else {
                        expr
                    }
                }
                "1/x" -> {
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "1/($expr)"
                    } else {
                        "1/("
                    }
                }
                else -> { // sin, cos, tan, ln, log
                    if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')')) {
                        "$expr×$fn("
                    } else {
                        "$expr$fn("
                    }
                }
            }

            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr, state.isDegrees),
                errorMessage = null
            )
        }
    }

    fun onNegate() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) {
                return@update state.copy(expression = "−", errorMessage = null)
            }

            // Find last number or parenthesized number
            val lastOpenParen = expr.lastIndexOf('(')
            val newExpr = if (lastOpenParen != -1 && expr.endsWith(')') && expr.substring(lastOpenParen).startsWith("(-")) {
                // Remove (-...)
                expr.substring(0, lastOpenParen) + expr.substring(lastOpenParen + 2, expr.length - 1)
            } else {
                // Wrap the trailing number in (-number)
                var idx = expr.length - 1
                while (idx >= 0 && (expr[idx].isDigit() || expr[idx] == '.')) {
                    idx--
                }
                val prefix = expr.substring(0, idx + 1)
                val number = expr.substring(idx + 1)
                if (number.isNotEmpty()) {
                    "$prefix(-$number)"
                } else {
                    "$expr−"
                }
            }

            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        justCalculated = false
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) return@update state

            // Check if deleting a multi-char function name like "sin(", "cos(", "tan(", "ln(", "log(", "√("
            val multiChars = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(")
            var removed = false
            var newExpr = expr

            for (mc in multiChars) {
                if (expr.endsWith(mc)) {
                    newExpr = expr.dropLast(mc.length)
                    removed = true
                    break
                }
            }

            if (!removed) {
                newExpr = expr.dropLast(1)
            }

            state.copy(
                expression = newExpr,
                previewResult = CalculatorEngine.evaluatePreview(newExpr),
                errorMessage = null
            )
        }
    }

    fun onClear() {
        justCalculated = false
        _uiState.update { state ->
            state.copy(
                expression = "",
                previewResult = null,
                errorMessage = null
            )
        }
    }

    fun onCalculate() {
        val currentExpr = _uiState.value.expression
        if (currentExpr.isBlank()) return

        when (val result = CalculatorEngine.evaluate(currentExpr, _uiState.value.isDegrees)) {
            is EvaluationResult.Success -> {
                val formattedResult = result.formattedValue
                viewModelScope.launch {
                    repository.saveCalculation(currentExpr, formattedResult)
                }
                justCalculated = true
                _uiState.update {
                    it.copy(
                        expression = formattedResult,
                        previewResult = null,
                        errorMessage = null
                    )
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun toggleDegRad() {
        _uiState.update { state ->
            val newDeg = !state.isDegrees
            state.copy(
                isDegrees = newDeg,
                previewResult = CalculatorEngine.evaluatePreview(state.expression, newDeg)
            )
        }
    }

    fun toggleInverse() {
        _uiState.update { it.copy(isInverse = !it.isInverse) }
    }

    fun toggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun toggleHistory() {
        _uiState.update { it.copy(isHistoryVisible = !it.isHistoryVisible) }
    }

    fun onSelectHistory(item: CalculationEntity) {
        justCalculated = true
        _uiState.update {
            it.copy(
                expression = item.result,
                previewResult = null,
                errorMessage = null,
                isHistoryVisible = false
            )
        }
    }

    fun onRestoreExpression(item: CalculationEntity) {
        justCalculated = false
        _uiState.update {
            it.copy(
                expression = item.expression,
                previewResult = CalculatorEngine.evaluatePreview(item.expression),
                errorMessage = null,
                isHistoryVisible = false
            )
        }
    }

    fun onClearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
        _uiState.update { it.copy(toastMessage = "Historial borrado") }
    }

    fun copyCurrentResult(context: Context) {
        val textToCopy = if (_uiState.value.expression.isNotEmpty()) {
            _uiState.value.expression
        } else {
            return
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Resultado", textToCopy)
        clipboard.setPrimaryClip(clip)

        _uiState.update { it.copy(toastMessage = "Copiado: $textToCopy") }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val database = AppDatabase.getDatabase(application)
                val repository = CalculationRepository(database.calculationDao())
                CalculatorViewModel(application, repository)
            }
        }
    }
}
