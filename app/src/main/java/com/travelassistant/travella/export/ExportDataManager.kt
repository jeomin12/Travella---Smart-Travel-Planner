package com.travelassistant.travella.export

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.travelassistant.travella.data.dao.ExpenseDao
import com.travelassistant.travella.data.dao.TripDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class ExportOptions(
    val includeTrips: Boolean,
    val includeExpenses: Boolean
)

class ExportDataManager(
    private val context: Context,
    private val tripDao: TripDao,
    private val expenseDao: ExpenseDao
) {
    private val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val dateTimeFmt = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    suspend fun exportToPdf(options: ExportOptions): Uri? = withContext(Dispatchers.IO) {
        val doc = PdfDocument()
        val pageWidth = 595 // A4 width in points (approx) @ 72dpi
        val pageHeight = 842 // A4 height

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val h2Paint = Paint().apply {
            isAntiAlias = true
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val normal = Paint().apply {
            isAntiAlias = true
            textSize = 12f
        }
        val faint = Paint().apply {
            isAntiAlias = true
            textSize = 11f
        }

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = 40

        fun newPageIfNeeded(linesNeeded: Int = 1) {
            val required = y + (linesNeeded * 18)
            if (required > pageHeight - 40) {
                doc.finishPage(page)
                pageNumber++
                page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = 40
            }
        }

        fun drawLine(text: String, paint: Paint = normal, x: Int = 24) {
            newPageIfNeeded()
            canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
            y += 18
        }

        // Header
        canvas.drawText("Travella Export", 24f, y.toFloat(), titlePaint); y += 26
        drawLine("Generated: ${dateTimeFmt.format(Date())}", faint)

        // Gather data based on options
        if (options.includeTrips) {
            val trips = tripDao.getAll().first()  // Flow<List<TripItem>> -> snapshot【1:97-L100-L100】
            if (trips.isNotEmpty()) {
                y += 10
                drawLine("Trips", h2Paint)
                y += 6
                trips.forEach { t ->
                    newPageIfNeeded(3)
                    drawLine("${t.title} — ${t.destination}")
                    drawLine("Dates: ${dateFmt.format(Date(t.startDate))} - ${dateFmt.format(Date(t.endDate))}", faint)
                    drawLine("Status: ${t.status} • Type: ${t.type} • Budget: $${"%.0f".format(t.totalBudget)} • Spent: $${"%.0f".format(t.spentAmount)}", faint)
                    y += 6
                }
            }
        }

        if (options.includeExpenses) {
            val expenses = expenseDao.getAllExpenses().first()  // Flow<List<ExpenseEntity>> -> snapshot【2:104-L105-L105】
            if (expenses.isNotEmpty()) {
                y += 10
                drawLine("Expenses", h2Paint)
                y += 6
                // Simple header row
                drawLine("Date        Category        Title                     Amount (USD)", faint)
                drawLine("------------------------------------------------------------------", faint)
                expenses.forEach { e ->
                    newPageIfNeeded()
                    val date = dateFmt.format(Date(e.date))
                    val line = String.format(
                        Locale.getDefault(),
                        "%-11s %-14s %-25s $%.2f",
                        date.take(10),
                        e.category.take(14),
                        e.title.take(25),
                        e.amountInUSD
                    )
                    drawLine(line)
                }
            }
        }

        doc.finishPage(page)

        // Save into Downloads via MediaStore (no legacy WRITE_EXTERNAL_STORAGE needed on Q+)
        val fileName = "travella_export_${System.currentTimeMillis()}.pdf"
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Files.getContentUri("external")
        }
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }
        val uri = resolver.insert(collection, values)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out ->
                doc.writeTo(out)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
        }
        doc.close()
        return@withContext uri
    }
}
