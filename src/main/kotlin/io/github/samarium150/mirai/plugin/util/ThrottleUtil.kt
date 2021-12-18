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

import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User

/**
 * 节流工具类
 *
 * @constructor 实例化节流工具类
 */
object ThrottleUtil {

    private val userLockMap = mutableMapOf<Long, Mutex>()

    private val groupLockMap = mutableMapOf<Long, Mutex>()

    private fun getUserLock(id: Long): Mutex {
        return userLockMap.getOrPut(id) { Mutex() }
    }

    private fun getGroupLock(id: Long): Mutex {
        return groupLockMap.getOrPut(id) { Mutex() }
    }

    /**
     * 上锁以节流
     *
     * @param subject 联系对象
     * @return 上锁结果
     * @see CommandSender.subject
     */
    fun lock(subject: Contact?): Boolean {
        val mutex = when(subject) {
            is User -> getUserLock(subject.id)
            is Group -> getGroupLock(subject.id)
            else -> null
        }
        return mutex?.tryLock() ?: false
    }

    /**
     * 从map中移除锁并解锁
     *
     * @param subject 联系对象
     * @see CommandSender.subject
     */
    fun unlock(subject: Contact?) {
        val mutex = when(subject) {
            is User -> userLockMap.remove(subject.id)
            is Group -> groupLockMap.remove(subject.id)
            else -> null
        }
        mutex?.unlock()
    }
}
