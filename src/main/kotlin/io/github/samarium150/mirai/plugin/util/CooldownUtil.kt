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

import io.github.samarium150.mirai.plugin.MiraiConsoleLolicon
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

/**
 * 冷却工具类
 *
 * @constructor 实例化冷却工具类
 */
object CooldownUtil {

    private val logger = MiraiConsoleLolicon.logger

    private val userLockMap = mutableMapOf<Long, Mutex>()

    private val groupLockMap = mutableMapOf<Long, Mutex>()

    private fun getUserLock(id: Long): Mutex {
        return userLockMap.getOrPut(id) { Mutex() }
    }

    private fun getGroupLock(id: Long): Mutex {
        return groupLockMap.getOrPut(id) { Mutex() }
    }

    /**
     * 获取冷却状态
     *
     * @param subject 联系对象
     * @return 是否已经冷却
     * @see CommandSender.subject
     */
    fun getCooldownStatus(subject: Contact?): Boolean {
        return when(subject) {
            is User -> getUserLock(subject.id).isLocked
            is Group -> getGroupLock(subject.id).isLocked
            else -> false
        }
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
        val mutex = when (subject) {
            is User -> userLockMap.remove(subject.id)
            is Group -> groupLockMap.remove(subject.id)
            else -> null
        }
        mutex?.withLock {
            logger.info("${subject}进入冷却")
            delay(time * 1000L)
            logger.info("${subject}已冷却")
        }
    }
}
