package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModelTest.Companion.error_Massage

class FakeDataSource(var remindersServiceData: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(error_Massage)
        }
        if (remindersServiceData?.isEmpty()!!) {
            return Result.Success(remindersServiceData!!)
        } else {
            return Result.Success(remindersServiceData!!)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Reminder not found")
        }
        val reminder = remindersServiceData?.find {
            it.id == id
        }
        return if (reminder!=null){
            Result.Success(reminder)
        } else{
            Result.Error("Reminder not found")
        }
    }
    override suspend fun deleteAllReminders() {
        remindersServiceData?.clear()
    }
}