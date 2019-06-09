package com.haroldadmin.moonshot.database.launch.rocket.secondStage

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.haroldadmin.moonshot.database.BaseDao
import com.haroldadmin.moonshot.models.launch.rocket.secondStage.SecondStageSummary
import com.haroldadmin.moonshot.models.launch.rocket.secondStage.SecondStageSummaryWithPayloads
import com.haroldadmin.moonshot.models.launch.rocket.secondStage.payload.Payload

@Dao
abstract class SecondStageSummaryDao : BaseDao<SecondStageSummary> {

    @Query("SELECT * FROM second_stage_summaries")
    abstract suspend fun getAllSecondStageSummaries(): List<SecondStageSummary>

    @Query("SELECT * FROM second_stage_summaries WHERE launch_flight_number = :flightNumber")
    abstract suspend fun getSecondStageSummary(flightNumber: Int): SecondStageSummary

    @Query("SELECT * FROM payloads WHERE payload_id = :id")
    abstract suspend fun getPayload(id: String): Payload

    @Query("SELECT * FROM second_stage_summaries WHERE launch_flight_number = :flightNumber")
    @Transaction
    abstract suspend fun getSecondStageWithPayloads(flightNumber: Int): SecondStageSummaryWithPayloads
}