package com.haroldadmin.moonshotRepository.search

import com.haroldadmin.cnradapter.executeWithRetry
import com.haroldadmin.moonshot.core.Resource
import com.haroldadmin.moonshot.database.LaunchDao
import com.haroldadmin.moonshot.models.SearchQuery
import com.haroldadmin.moonshot.models.launch.Launch
import com.haroldadmin.moonshotRepository.launch.PersistLaunchesUseCase
import com.haroldadmin.spacex_api_wrapper.launches.LaunchesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SearchLaunchesUseCase(
    private val launchesDao: LaunchDao,
    private val launchesService: LaunchesService,
    persistLaunchesUseCase: PersistLaunchesUseCase
) : SearchUseCase() {

    @ExperimentalCoroutinesApi
    private val resource = searchResource(
        dbQuery = { _query },
        dbLimit = { _limit },
        dbFetcher = { _, query, limit -> getSearchResultsCached(query, limit) },
        cacheValidator = { cached -> !cached.isNullOrEmpty() },
        apiFetcher = { getAllLaunchesFromApi() },
        dataPersister = { launches -> persistLaunchesUseCase.persistLaunches(launches) }
    )

    @ExperimentalCoroutinesApi
    fun searchFor(query: SearchQuery, limit: Int): Flow<Resource<List<Launch>>> {
        _query = query.sqlQuery()
        _limit = limit
        return resource.flow()
    }

    private suspend fun getSearchResultsCached(query: String, limit: Int) =
        withContext(Dispatchers.IO) {
            launchesDao.forQuery(query, limit)
        }

    private suspend fun getAllLaunchesFromApi() = withContext(Dispatchers.IO) {
        executeWithRetry {
            launchesService.getAllLaunches().await()
        }
    }
}