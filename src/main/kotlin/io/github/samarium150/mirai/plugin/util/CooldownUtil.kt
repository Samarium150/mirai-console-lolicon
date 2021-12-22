/**
 * Copyright (c) 2020-2021 Samarium
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
package io.github.samarium150.mirai.plugin.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User

private val userCooldownLockMap = mutableMapOf<Long, Mutex>()

private val groupCooldownLockMap = mutableMapOf<Long, Mutex>()

private fun getUserCooldownLock(id: Long): Mutex {
    return userCooldownLockMap.getOrPut(id) { Mutex() }
}

private fun getGroupCooldownLock(id: Long): Mutex {
    return groupCooldownLockMap.getOrPut(id) { Mutex() }
}

private fun getLock(subject: Contact?): Mutex? {
    return when (subject) {
        is User -> getUserCooldownLock(subject.id)
        is Group -> getGroupCooldownLock(subject.id)
        else -> null
    }
}

private fun removeLock(subject: Contact?) {
    when (subject) {
        is User -> userCooldownLockMap.remove(subject.id)
        is Group -> groupCooldownLockMap.remove(subject.id)
    }
}

/**
 * 获取冷却状态
 *
 * @param subject 联系对象
 * @return 是否已经冷却
 * @see CommandSender.subject
 */
fun getCooldownStatus(subject: Contact?): Boolean {
    return getLock(subject)?.isLocked ?: false
}

/**
 * 冷却联系对象
 *
 * @param subject 联系对象
 * @param time 冷却时间
 * @see CommandSender.subject
 */
@OptIn(DelicateCoroutinesApi::class)
suspend fun cooldown(subject: Contact?, time: Int) = GlobalScope.launch {
    getLock(subject)?.withLock {
        logger.info("${subject}进入冷却")
        delay(time * 1000L)
        logger.info("${subject}已冷却")
    }
    removeLock(subject)
}
