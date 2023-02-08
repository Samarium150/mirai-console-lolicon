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

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import java.util.*

private val userCooldownLockMap = Collections.synchronizedMap(mutableMapOf<Long, Mutex>())

private val groupCooldownLockMap = Collections.synchronizedMap(mutableMapOf<Long, Mutex>())

private fun getCooldownLock(subject: Contact?): Mutex? {
    return when (subject) {
        is User -> userCooldownLockMap.getOrPut(subject.id) { Mutex() }
        is Group -> groupCooldownLockMap.getOrPut(subject.id) { Mutex() }
        else -> null
    }
}

fun getCooldownStatus(subject: Contact?): Boolean {
    return getCooldownLock(subject)?.isLocked ?: false
}

suspend fun cooldown(subject: Contact?, time: Int) = MiraiConsoleLolicon.launch {
    getCooldownLock(subject)?.withLock {
        logger.info("${subject}进入冷却")
        delay(time * 1000L)
        logger.info("${subject}已冷却")
    }
}
