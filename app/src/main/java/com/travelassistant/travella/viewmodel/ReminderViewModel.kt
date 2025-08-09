package com.travelassistant.travella.viewmodel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travelassistant.travella.data.database.ReminderDatabase
import com.travelassistant.travella.data.model.ReminderEntity
import com.travelassistant.travella.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ReminderDatabase.getDatabase(application).reminderDao()
    private val repository = ReminderRepository(dao)

    val allReminders: Flow<List<ReminderEntity>> = repository.allReminders

    fun insert(reminder: ReminderEntity) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun delete(reminder: ReminderEntity) = viewModelScope.launch {
        repository.delete(reminder)
    }
}
