package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

object DummyReminderData {
    val items = arrayListOf(
        ReminderDTO("Title 1", "Description 1", "Location 1", 35.1, -120.2),
        ReminderDTO("Title 2", "Description 2", "Location 2", 38.5, -115.3),
        ReminderDTO("title 3", "description 3", "location 3", 36.5, -125.1)
    )

    val reminderDataItem = ReminderDataItem("Title 100", "Description 100", "Location 100", 37.8, -122.2)
}