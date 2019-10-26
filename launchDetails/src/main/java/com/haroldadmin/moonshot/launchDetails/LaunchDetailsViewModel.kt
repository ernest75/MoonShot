package com.haroldadmin.moonshot.launchDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.haroldadmin.moonshot.base.MoonShotViewModel
import com.haroldadmin.moonshot.base.koin
import com.haroldadmin.moonshot.base.safeArgs
import com.haroldadmin.moonshotRepository.launch.GetLaunchDetailsUseCase
import com.haroldadmin.moonshotRepository.launch.GetLaunchPicturesUseCase
import com.haroldadmin.vector.VectorViewModelFactory
import com.haroldadmin.vector.ViewModelOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class LaunchDetailsViewModel(
    initState: LaunchDetailsState,
    private val launchDetailsUseCase: GetLaunchDetailsUseCase,
    private val launchPicturesUseCase: GetLaunchPicturesUseCase
) : MoonShotViewModel<LaunchDetailsState>(initState) {

    init {
        viewModelScope.launch {
            getLaunchDetails(initState.flightNumber)
            getLaunchPictures(initState.flightNumber)
        }
    }

    suspend fun getLaunchDetails(flightNumber: Int) {
        launchDetailsUseCase
            .getLaunchDetails(flightNumber)
            .collect { launchRes ->
                setState {
                    copy(launch = launchRes)
                }
            }
    }

    suspend fun getLaunchPictures(flightNumber: Int) {
        launchPicturesUseCase
            .getLaunchPictures(flightNumber)
            .collect { picturesRes ->
                setState {
                    copy(launchPictures = picturesRes)
                }
            }
    }

    companion object : VectorViewModelFactory<LaunchDetailsViewModel, LaunchDetailsState> {
        override fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): LaunchDetailsState? {
            val safeArgs = owner.safeArgs<LaunchDetailsFragmentArgs>()
            return LaunchDetailsState(safeArgs.flightNumber)
        }

        override fun create(
            initialState: LaunchDetailsState,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): LaunchDetailsViewModel? = with(owner.koin()) {
            LaunchDetailsViewModel(initialState, get(), get())
        }
    }
}
