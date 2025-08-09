package com.travelassistant.travella.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import org.json.JSONObject

data class CurrencyRate(
    val currency: String,
    val rate: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

class CurrencyService {

    private val baseUrl = "https://api.exchangerate-api.com/v4/latest"
    private val cachedRates = mutableMapOf<String, CurrencyRate>()
    private val cacheTimeout = 30 * 60 * 1000L // 30 minutes

    suspend fun convertCurrency(
        amount: Double,
        fromCurrency: String,
        toCurrency: String = "USD"
    ): Double {
        return withContext(Dispatchers.IO) {
            try {
                if (fromCurrency == toCurrency) return@withContext amount

                val rate = getExchangeRate(fromCurrency, toCurrency)
                amount * rate
            } catch (e: Exception) {
                // Fallback to cached rates or default conversion
                // Log the error for debugging
                println("Error converting currency: ${e.message}")
                getOfflineRate(fromCurrency, toCurrency) * amount
            }
        }
    }

    private suspend fun getExchangeRate(from: String, to: String): Double {

        val cacheKey = "${from}_${to}"
        val cachedRate = cachedRates[cacheKey]

        if (cachedRate != null &&
            System.currentTimeMillis() - cachedRate.lastUpdated < cacheTimeout) {
            return cachedRate.rate
        }


        return try {
            val url = URL("$baseUrl/$from")
            val response = url.readText()
            val json = JSONObject(response)
            val rates = json.getJSONObject("rates")
            val rate = rates.getDouble(to)


            cachedRates[cacheKey] = CurrencyRate(to, rate)
            rate
        } catch (e: Exception) {
            // Use fallback rate
            println("API call failed for $from to $to: ${e.message}. Using offline rate.")
            getOfflineRate(from, to)
        }
    }

    private fun getOfflineRate(from: String, to: String): Double {
        // Fallback exchange rates (approximate)
        val rates = mapOf(
            "EUR_USD" to 1.10,
            "GBP_USD" to 1.25,
            "JPY_USD" to 0.0067,
            "AUD_USD" to 0.65,
            "CAD_USD" to 0.75,
            "CHF_USD" to 1.05,
            "CNY_USD" to 0.14,
            "INR_USD" to 0.012,
            "KRW_USD" to 0.00076,
            "SGD_USD" to 0.74
        )

        val key = "${from}_${to}"
        val reverseKey = "${to}_${from}"

        return when {
            rates.containsKey(key) -> rates[key]!!
            rates.containsKey(reverseKey) -> 1.0 / rates[reverseKey]!!
            from == to -> 1.0 // If same currency, rate is 1
            from == "USD" -> 1.0 // If from USD, assume 1 (as base)
            to == "USD" -> 1.0 // If to USD, assume 1 (as base) - this might be less accurate without a specific rate
            else -> 1.0 // Default fallback if no specific rate found
        }
    }

    suspend fun getSupportedCurrencies(): List<String> {
        // In a real app, this would also come from the API.
        // For now, return a hardcoded list.
        return listOf(
            "USD", "EUR", "GBP", "JPY", "AUD", "CAD",
            "CHF", "CNY", "INR", "KRW", "SGD", "HKD",
            "NZD", "SEK", "NOK", "DKK", "PLN", "CZK",
            "HUF", "RUB", "BRL", "MXN", "ZAR", "TRY"
        )
    }

    suspend fun refreshRates() {
        cachedRates.clear()
        // Pre-load common currency rates
        val commonCurrencies = listOf("EUR", "GBP", "JPY", "AUD")
        commonCurrencies.forEach { currency ->
            try {
                getExchangeRate("USD", currency)
                getExchangeRate(currency, "USD")
            } catch (e: Exception) {
                // Ignore errors during preload, as offline rates will be used
                println("Preload of currency rates failed for $currency: ${e.message}")
            }
        }
    }
}
