package com.travelassistant.travella.utils


import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object CurrencyConverter {
    // Returns rate for converting FROM `from` TO `to`. Example: rate USD->AUD.
    // Uses https://api.frankfurter.app/latest?from=USD&to=AUD
    fun getRate(from: String, to: String): Double {
        if (from.equals(to, ignoreCase = true)) return 1.0
        val urlStr = "https://api.frankfurter.app/latest?from=${from.uppercase()}&to=${to.uppercase()}"
        val url = URL(urlStr)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 7000
            readTimeout = 7000
        }
        conn.inputStream.use { input ->
            BufferedReader(InputStreamReader(input)).use { br ->
                val body = br.readText()
                val json = JSONObject(body)
                val rates = json.getJSONObject("rates")
                return rates.getDouble(to.uppercase())
            }
        }
    }
}
