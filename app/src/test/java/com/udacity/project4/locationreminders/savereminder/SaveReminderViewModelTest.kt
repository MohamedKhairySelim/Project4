package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),FakeDataSource())
    }

    @After
    fun tearDown() { stopKoin() }

    @Test
    fun check_loading_Returns_True () {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(
            ReminderDataItem("title", "description", "location", (-430..350).random().toDouble(), (-230..640).random().toDouble()
            )
        )
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    @Test
    fun empty_location_returns_false () {
        val result=saveReminderViewModel.validateEnteredData(
            ReminderDataItem("title", "description", "", (-430..350).random().toDouble(), (-230..640).random().toDouble())
        )
        assertThat(result, CoreMatchers.`is`(false))
    }
    @Test
    fun  empty_title_returns_false () {
        val result=saveReminderViewModel.validateEnteredData(
            ReminderDataItem("", "description", "location",(-430..350).random().toDouble(),(-230..640).random().toDouble())
        )
        assertThat(result, CoreMatchers.`is`(false))
    }
}