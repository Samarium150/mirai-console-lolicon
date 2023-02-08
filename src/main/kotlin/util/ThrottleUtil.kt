/*
 * Copyright (c) 2020-2023 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package io.github.samarium150.mirai.plugin.lolicon.util

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import java.util.*

private val userThrottleMutexMap = Collections.synchronizedMap(mutableMapOf<Long, Mutex>())

private val groupThrottleMutexMap = Collections.synchronizedMap(mutableMapOf<Long, Mutex>())

fun getThrottleMutex(subject: Contact?): Mutex {
    return when (subject) {
        is User -> userThrottleMutexMap.getOrPut(subject.id) { Mutex() }
        is Group -> groupThrottleMutexMap.getOrPut(subject.id) { Mutex() }
        else -> Mutex()
    }
}
