@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.weatherpulse.favourite.viewmodel

import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.LocationKey
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


abstract class BaseViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    open fun baseSetUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun baseTearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        clearAllMocks()
    }
}

class FavViewModelTest : BaseViewModelTest() {

    private lateinit var repo: WeatherRepo
    private lateinit var viewModel: FavViewModel

    @Before
    fun setup() {
        super.baseSetUp()
        repo = mockk(relaxed = true)
        viewModel = FavViewModel(repo)
    }


    @Test
    fun insertLocation_should_emit_success_when_insertion_returns_id_greater_than_0() = runTest {
        val location = FavouritePlacesPojo(
            LocationKey(30.0, 31.0),
            "Cairo",
            "23°C")
        coEvery { repo.insertLocation(location) } returns 1L

        viewModel.insertLocation(location)
        advanceUntilIdle()

        val result = viewModel.message.first()
        assertTrue(result is Result.Success)
        assertEquals("Added Successfully", (result as Result.Success).data)

        coVerify { repo.insertLocation(location) }
    }



    @Test
    fun deleteLocation_should_emit_error_when_location_is_null() = runTest {

        viewModel.deleteLocation(null)
        advanceUntilIdle()

        val result = viewModel.message.first()
        assertTrue(result is Result.Error)
        assertEquals("Location is Null", (result as Result.Error).message)
    }
}