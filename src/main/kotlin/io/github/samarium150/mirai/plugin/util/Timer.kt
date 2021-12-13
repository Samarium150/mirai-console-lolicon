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

import kotlinx.coroutines.*
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import org.jetbrains.annotations.Nullable

/**
 * 计时器
 *
 * @constructor 实例化计时器
 */
object Timer {

    /**
     * 用户及其冷却状态
     */
    private val userCooldown = mutableMapOf<Long, Boolean>()

    /**
     * 群组及其冷却状态
     */
    private val groupCooldown = mutableMapOf<Long, Boolean>()

    /**
     * 获取用户冷却状态的lambda函数
     */
    private val getUserCooldown: (Long) -> Boolean = { key -> userCooldown.getOrDefault(key, true) }

    /**
     * 获取群组冷却状态的lambda函数
     */
    private val getGroupCooldown: (Long) -> Boolean = { key -> groupCooldown.getOrDefault(key, true) }

    /**
     * 设置用户冷却状态的lambda函数
     */
    private val setUserCooldown: (Long, Boolean) -> Unit = { key, value -> userCooldown[key] = value }

    /**
     * 设置群组冷却状态的lambda函数
     */
    private val setGroupCooldown: (Long, Boolean) -> Unit = { key, value -> groupCooldown[key] = value }

    /**
     * 异步冷却用户
     *
     * @param key 用户ID
     * @param cooldown 冷却时间
     */
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun userCooldownAsync(key: Long, cooldown: Int) = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        delay((cooldown * 1000).toLong())
        setUserCooldown(key, true)
    }

    /**
     * 异步冷却群组
     *
     * @param key 群ID
     * @param cooldown 冷却时间
     */
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun groupCooldownAsync(key: Long, cooldown: Int) = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        delay((cooldown * 1000).toLong())
        setGroupCooldown(key, true)
    }

    /**
     * 获取联系对象的冷却状态
     *
     * @param subject 联系对象
     * @return 是否已经冷却
     * @see CommandSender.subject
     */
    fun getCooldown(@Nullable subject: Contact?): Boolean {
        return when (subject) {
            is User -> getUserCooldown(subject.id)
            is Group -> getGroupCooldown(subject.id)
            else -> true
        }
    }

    /**
     * 设置联系对象的冷却状态
     *
     * @param subject 联系对象
     * @see CommandSender.subject
     */
    fun setCooldown(@Nullable subject: Contact?) {
        when (subject) {
            is User -> setUserCooldown(subject.id, false)
            is Group -> setGroupCooldown(subject.id, false)
        }
    }

    /**
     * 异步冷却
     *
     * @param subject 联系对象
     * @param cooldown 冷却时间
     */
    suspend fun cooldown(@Nullable subject: Contact?, cooldown: Int) {
        when (subject) {
            is User -> userCooldownAsync(subject.id, cooldown).await()
            is Group -> groupCooldownAsync(subject.id, cooldown).await()
        }
    }
}
