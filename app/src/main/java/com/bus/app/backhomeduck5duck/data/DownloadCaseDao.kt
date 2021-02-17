/*
 * Copyright 2021 headuck (https://blog.headuck.com/)
 *
 * This file is part of GoOutWithDuck
 *
 * GoOutWithDuck is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GoOutWithDuck is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GoOutWithDuck. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bus.app.backhomeduck5duck.data

import androidx.paging.PagingSource
import androidx.room.*

/**
 * The Data Access Object for the [DownloadCase] class.
 */
@Dao
interface DownloadCaseDao {
    @Query("SELECT * FROM download_case ORDER BY start_date DESC")
    fun getDownloadCaseList(): PagingSource<Int, DownloadCase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadCase(downloadCase: DownloadCase): Long

    @Delete
    suspend fun deleteDownloadCase(downloadCase: DownloadCase)

    @Query("UPDATE download_case SET matched = 1 WHERE matched = 0")
    suspend fun updateAllAsMatched()

    @Query("DELETE FROM download_case")
    suspend fun deleteAllDownloadCase()

    @Transaction
    @Query("SELECT " +
            "visit_history.id, visit_history.type, visit_history.venueId, " +
            "visit_history.nameEn, visit_history.nameZh, visit_history.licenseNo, " +
            "visit_history.random as visit_random, " +
            "visit_history.start_date as visit_start_date, visit_history.end_date as visit_end_date, " +
            "download_case.start_date as download_start_date, download_case.end_date as download_end_date, " +
            "download_case.random as download_random " +
            "FROM visit_history INNER JOIN download_case " +
            "ON visit_history.type = download_case.type AND visit_history.venueId = download_case.venueId " +
            "WHERE visit_history.bookmark = 0 AND visit_start_date >= :startDate AND download_case.matched = 0")
    suspend fun checkExposure(startDate: Long) : List<DownloadCaseMatchResult>

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT " +
            "visit_history.id, visit_history.type, visit_history.venueId, " +
            "visit_history.nameEn, visit_history.nameZh, visit_history.licenseNo, " +
            "visit_history.random as visit_random, " +
            "visit_history.start_date as visit_start_date, " +
            "download_case.start_date as download_start_date, download_case.end_date as download_end_date, " +
            "download_case.random as download_random " +
            "FROM visit_history INNER JOIN download_case " +
            "ON visit_history.type = download_case.type AND visit_history.venueId = download_case.venueId " +
            "WHERE visit_history.bookmark = 1 AND download_case.matched = 0")
    suspend fun checkBookmarkMatch() : List<DownloadCaseMatchResult>

}
