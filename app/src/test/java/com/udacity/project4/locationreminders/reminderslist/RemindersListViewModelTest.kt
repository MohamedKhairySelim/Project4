package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.DummyReminderData
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var remindersRepository: FakeDataSource

    @Before
    fun setupViewModel() {
        stopKoin()
        remindersRepository = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_Success() = runBlockingTest {
        DummyReminderData.items.forEach { reminderDTO ->
            remindersRepository.saveReminder(reminderDTO)
        }

        remindersListViewModel.loadReminders()

        val loadedItems = remindersListViewModel.remindersList.getOrAwaitValue()
        MatcherAssert.assertThat(loadedItems.size, `is`(DummyReminderData.items.size))

        for (i in loadedItems.indices) {
            MatcherAssert.assertThat(loadedItems[i].title, `is`(DummyReminderData.items[i].title))
        }
        MatcherAssert.assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(false))
    }

    @Test
    fun loadReminders_DataSource_Error() {
        runBlockingTest {
            remindersRepository.setShouldReturnError(true)
            saveReminder()

            remindersListViewModel.loadReminders()

            MatcherAssert.assertThat(remindersListViewModel.showSnackBar.value, CoreMatchers.`is`("Error in Testing!"))
        }
    }

    @Test
    fun loadReminders_resultSuccess_noReminders() = runBlockingTest {
        remindersRepository.deleteAllReminders()
        remindersListViewModel.loadReminders()
        val loadedItems = remindersListViewModel.remindersList.getOrAwaitValue()
        MatcherAssert.assertThat(loadedItems.size, CoreMatchers.`is`(0))
        MatcherAssert.assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    private suspend fun saveReminder() {
        remindersRepository.saveReminder(
            ReminderDTO("title", "description", "location", 100.00, 10.00))
    }
}