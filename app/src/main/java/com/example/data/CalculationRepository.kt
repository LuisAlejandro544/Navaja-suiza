package com.example.data

import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val calculationDao: CalculationDao) {
    val allCalculations: Flow<List<CalculationEntity>> = calculationDao.getAllCalculations()

    suspend fun saveCalculation(expression: String, result: String) {
        calculationDao.insert(
            CalculationEntity(
                expression = expression,
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() {
        calculationDao.clearAll()
    }

    suspend fun deleteCalculation(id: Long) {
        calculationDao.deleteById(id)
    }
}
