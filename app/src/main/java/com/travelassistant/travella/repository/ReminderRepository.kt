package com.travelassistant.travella.repository


import com.travelassistant.travella.data.dao.ReminderDao
import com.travelassistant.travella.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    val allReminders: Flow<List<ReminderEntity>> = dao.getAllReminders()

    suspend fun insert(reminder: ReminderEntity) {
        dao.insertReminder(reminder)
    }

    suspend fun delete(reminder: ReminderEntity) {
        dao.deleteReminder(reminder)
    }
}
