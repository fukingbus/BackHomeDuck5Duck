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

package com.bus.app.backhomeduck5duck.workers


import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess

import com.bus.app.backhomeduck5duck.usecases.DownloadUseCase
import com.bus.app.backhomeduck5duck.usecases.ExposureUseCase

import javax.inject.Singleton

@Singleton
class GetBatchesWorker @WorkerInject constructor(@Assisted ctx: Context, @Assisted params: WorkerParameters,
                                                 private val downloadUseCase: DownloadUseCase,
                                                 private val exposureUseCase: ExposureUseCase) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        lateinit var result: Result
        downloadUseCase.downloadCases().onSuccess {
            val outputData = if (exposureUseCase.exposureCheck()) {
                workDataOf(KEY_BATCH_DATA to "Exposure")
            } else {
                workDataOf(KEY_BATCH_DATA to "No Exposure")
            }
            result = Result.success(outputData)
         }.onFailure {
            result = Result.failure()
         }
        return result
    }


    companion object {
        const val KEY_BATCH_DATA = "KEY_BATCH_DATA"
    }
}