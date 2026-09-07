package com.example.ui

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.CalculationEntity
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CoralOperator
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkDisplayBg
import com.example.ui.theme.DarkKeyActionBg
import com.example.ui.theme.DarkKeyEqualsBg
import com.example.ui.theme.DarkKeyNumBg
import com.example.ui.theme.DarkKeyOpBg
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.OperatorTeal
import com.example.ui.theme.TealAccent
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Brand, History, Scientific expander, and Copy
            CalculatorHeader(
                isScientificExpanded = uiState.isScientificExpanded,
                isDegrees = uiState.isDegrees,
                onToggleScientific = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.toggleScientific()
                },
                onToggleDegRad = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.toggleDegRad()
                },
                onOpenHistory = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.toggleHistory()
                },
                onCopy = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.copyCurrentResult(context)
                }
            )

            // Modern Minimalist Display Section
            CalculatorDisplay(
                expression = uiState.expression,
                previewResult = uiState.previewResult,
                errorMessage = uiState.errorMessage,
                isDegrees = uiState.isDegrees,
                onBackspace = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onBackspace()
                },
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Scientific panel (Collapsible)
            AnimatedVisibility(
                visible = uiState.isScientificExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ScientificKeypad(
                    isDegrees = uiState.isDegrees,
                    isInverse = uiState.isInverse,
                    onToggleDegRad = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.toggleDegRad()
                    },
                    onToggleInverse = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.toggleInverse()
                    },
                    onFunctionClick = { fn ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onScientificFunction(fn)
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Keypad with rounded pill aesthetics
            MainKeypad(
                onDigit = { digit ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDigit(digit)
                },
                onOperator = { op ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onOperator(op)
                },
                onDecimal = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onDecimal()
                },
                onParenthesis = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onParenthesis()
                },
                onNegate = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onNegate()
                },
                onClear = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onClear()
                },
                onCalculate = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onCalculate()
                }
            )
        }

        // History Bottom Sheet
        if (uiState.isHistoryVisible) {
            val sheetState = rememberModalBottomSheetState()
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleHistory() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                HistoryBottomSheetContent(
                    historyList = historyList,
                    onSelectResult = { item ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onSelectHistory(item)
                    },
                    onRestoreExpression = { item ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onRestoreExpression(item)
                    },
                    onClearHistory = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.onClearHistory()
                    },
                    onClose = { viewModel.toggleHistory() }
                )
            }
        }
    }
}

@Composable
private fun CalculatorHeader(
    isScientificExpanded: Boolean,
    isDegrees: Boolean,
    onToggleScientific: () -> Unit,
    onToggleDegRad: () -> Unit,
    onOpenHistory: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App / Mode badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AmberPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.calculator_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Quick Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DEG / RAD quick toggle pill
            Surface(
                onClick = onToggleDegRad,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.testTag("calc_header_degrad")
            ) {
                Text(
                    text = if (isDegrees) "DEG" else "RAD",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (isDegrees) CyanAccent else CoralOperator,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Copy button
            IconButton(
                onClick = onCopy,
                modifier = Modifier.testTag("calc_copy_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.calc_copy_result),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Scientific mode toggle
            IconButton(
                onClick = onToggleScientific,
                modifier = Modifier.testTag("calc_toggle_scientific"),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isScientificExpanded) AmberPrimary.copy(alpha = 0.2f) else Color.Transparent
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = stringResource(
                        if (isScientificExpanded) R.string.calc_scientific_less else R.string.calc_scientific_more
                    ),
                    tint = if (isScientificExpanded) AmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // History button
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.testTag("calc_open_history")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = stringResource(R.string.calc_history),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CalculatorDisplay(
    expression: String,
    previewResult: String?,
    errorMessage: String?,
    isDegrees: Boolean,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(expression) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkDisplayBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mode status indicator row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isDegrees) "DEG" else "RAD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                // Backspace button top-right / accessible
                IconButton(
                    onClick = onBackspace,
                    enabled = expression.isNotEmpty(),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("calc_btn_backspace")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = stringResource(R.string.calc_backspace),
                        tint = if (expression.isNotEmpty()) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Primary expression
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = when {
                            expression.length > 18 -> 26.sp
                            expression.length > 12 -> 32.sp
                            else -> 40.sp
                        },
                        fontWeight = FontWeight.Normal
                    ),
                    color = if (expression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.testTag("calc_display_expression")
                )
            }

            // Live Preview or Error status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("calc_display_error")
                    )
                } else if (previewResult != null && previewResult != expression) {
                    Text(
                        text = "= $previewResult",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = AmberPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("calc_display_preview")
                    )
                }
            }
        }
    }
}

@Composable
private fun ScientificKeypad(
    isDegrees: Boolean,
    isInverse: Boolean,
    onToggleDegRad: () -> Unit,
    onToggleInverse: () -> Unit,
    onFunctionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkDisplayBg.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: DEG/RAD, INV, sin, cos, tan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificPillKey(
                    label = if (isDegrees) "DEG" else "RAD",
                    isActive = !isDegrees,
                    onClick = onToggleDegRad,
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "INV",
                    isActive = isInverse,
                    onClick = onToggleInverse,
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = if (isInverse) "sin⁻¹" else "sin",
                    onClick = { onFunctionClick("sin") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = if (isInverse) "cos⁻¹" else "cos",
                    onClick = { onFunctionClick("cos") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = if (isInverse) "tan⁻¹" else "tan",
                    onClick = { onFunctionClick("tan") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: ln, log, √, ^, !
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificPillKey(
                    label = if (isInverse) "eˣ" else "ln",
                    onClick = { onFunctionClick(if (isInverse) "e^" else "ln") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = if (isInverse) "10ˣ" else "log",
                    onClick = { onFunctionClick(if (isInverse) "10^" else "log") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = if (isInverse) "x²" else "√",
                    onClick = { onFunctionClick(if (isInverse) "^2" else "√") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "^",
                    onClick = { onFunctionClick("^") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "x!",
                    onClick = { onFunctionClick("!") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: π, e, %, 1/x, ( )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScientificPillKey(
                    label = "π",
                    onClick = { onFunctionClick("π") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "e",
                    onClick = { onFunctionClick("e") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "%",
                    onClick = { onFunctionClick("%") },
                    modifier = Modifier.weight(1f)
                )
                ScientificPillKey(
                    label = "1/x",
                    onClick = { onFunctionClick("1/x") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScientificPillKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) AmberPrimary.copy(alpha = 0.25f) else DarkKeyOpBg,
        label = "sciKeyBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) AmberPrimary else CyanAccent,
        label = "sciKeyText"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(38.dp)
            .testTag("calc_btn_$label"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        tonalElevation = 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = textColor
            )
        }
    }
}

@Composable
private fun MainKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onParenthesis: () -> Unit,
    onNegate: () -> Unit,
    onClear: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorKey(
                text = "AC",
                type = KeyType.Action,
                testTag = "calc_btn_clear",
                modifier = Modifier.weight(1f),
                onClick = onClear
            )
            CalculatorKey(
                text = "( )",
                type = KeyType.Function,
                testTag = "calc_btn_parens",
                modifier = Modifier.weight(1f),
                onClick = onParenthesis
            )
            CalculatorKey(
                text = "%",
                type = KeyType.Function,
                testTag = "calc_btn_percent",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("%") }
            )
            CalculatorKey(
                text = "÷",
                type = KeyType.Operator,
                testTag = "calc_btn_divide",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorKey(
                text = "7",
                type = KeyType.Number,
                testTag = "calc_btn_7",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("7") }
            )
            CalculatorKey(
                text = "8",
                type = KeyType.Number,
                testTag = "calc_btn_8",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("8") }
            )
            CalculatorKey(
                text = "9",
                type = KeyType.Number,
                testTag = "calc_btn_9",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("9") }
            )
            CalculatorKey(
                text = "×",
                type = KeyType.Operator,
                testTag = "calc_btn_multiply",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorKey(
                text = "4",
                type = KeyType.Number,
                testTag = "calc_btn_4",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("4") }
            )
            CalculatorKey(
                text = "5",
                type = KeyType.Number,
                testTag = "calc_btn_5",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("5") }
            )
            CalculatorKey(
                text = "6",
                type = KeyType.Number,
                testTag = "calc_btn_6",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("6") }
            )
            CalculatorKey(
                text = "−",
                type = KeyType.Operator,
                testTag = "calc_btn_minus",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorKey(
                text = "1",
                type = KeyType.Number,
                testTag = "calc_btn_1",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("1") }
            )
            CalculatorKey(
                text = "2",
                type = KeyType.Number,
                testTag = "calc_btn_2",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("2") }
            )
            CalculatorKey(
                text = "3",
                type = KeyType.Number,
                testTag = "calc_btn_3",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("3") }
            )
            CalculatorKey(
                text = "+",
                type = KeyType.Operator,
                testTag = "calc_btn_plus",
                modifier = Modifier.weight(1f),
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ±, 0, ., =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorKey(
                text = "±",
                type = KeyType.Function,
                testTag = "calc_btn_negate",
                modifier = Modifier.weight(1f),
                onClick = onNegate
            )
            CalculatorKey(
                text = "0",
                type = KeyType.Number,
                testTag = "calc_btn_0",
                modifier = Modifier.weight(1f),
                onClick = { onDigit("0") }
            )
            CalculatorKey(
                text = ".",
                type = KeyType.Number,
                testTag = "calc_btn_dot",
                modifier = Modifier.weight(1f),
                onClick = onDecimal
            )
            CalculatorKey(
                text = "=",
                type = KeyType.Equals,
                testTag = "calc_btn_equals",
                modifier = Modifier.weight(1f),
                onClick = onCalculate
            )
        }
    }
}

enum class KeyType {
    Number,
    Operator,
    Function,
    Action,
    Equals
}

@Composable
private fun CalculatorKey(
    text: String,
    type: KeyType,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Elegant color mapping based on modern calculator aesthetic
    val backgroundColor = when (type) {
        KeyType.Number -> DarkKeyNumBg
        KeyType.Operator -> DarkKeyOpBg
        KeyType.Function -> DarkKeyOpBg
        KeyType.Action -> DarkKeyActionBg
        KeyType.Equals -> DarkKeyEqualsBg
    }

    val contentColor = when (type) {
        KeyType.Number -> MaterialTheme.colorScheme.onSurface
        KeyType.Operator -> CoralOperator
        KeyType.Function -> OperatorTeal
        KeyType.Action -> CrimsonError
        KeyType.Equals -> Color.White
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1.28f)
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        color = backgroundColor,
        tonalElevation = if (type == KeyType.Equals) 4.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = if (text.length > 2) 20.sp else 24.sp,
                    fontWeight = if (type == KeyType.Number) FontWeight.Normal else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun HistoryBottomSheetContent(
    historyList: List<CalculationEntity>,
    onSelectResult: (CalculationEntity) -> Unit,
    onRestoreExpression: (CalculationEntity) -> Unit,
    onClearHistory: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = AmberPrimary
                )
                Text(
                    text = stringResource(R.string.calc_history),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (historyList.isNotEmpty()) {
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("calc_history_clear_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.calc_history_clear),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        if (historyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    text = stringResource(R.string.calc_history_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onUseResult = { onSelectResult(item) },
                        onUseExpression = { onRestoreExpression(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: CalculationEntity,
    onUseResult: () -> Unit,
    onUseExpression: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = DateFormat.format("HH:mm - dd/MM", Date(item.timestamp)).toString()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "Toca para usar",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberPrimary
                )
            }

            // Expression
            Text(
                text = item.expression,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUseExpression() },
                textAlign = TextAlign.End
            )

            // Result
            Text(
                text = "= ${item.result}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUseResult() },
                textAlign = TextAlign.End
            )
        }
    }
}
