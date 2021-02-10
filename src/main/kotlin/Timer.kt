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
package com.github.samarium150.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import org.jetbrains.annotations.Nullable

/**
 * Object for handling cooldown feature
 * <br>
 * 计时器
 *
 * @constructor Create a Timer instance <br> 实例化计时器
 */
object Timer {

    /**
     * Cooldown map for users
     * <br>
     * 用户及其冷却状态
     *
     */
    private val userCooldown = mutableMapOf<Long, Boolean>()

    /**
     * Cooldown map for groups
     * <br>
     * 群组及其冷却状态
     *
     */
    private val groupCooldown = mutableMapOf<Long, Boolean>()

    /**
     * The lambda function for getting user's cooldown status
     * <br>
     * 获取用户冷却状态的lambda函数
     *
     */
    private val getUserCooldown: (Long) -> Boolean = { key -> userCooldown.getOrDefault(key, true) }

    /**
     * The lambda function for getting group's cooldown status
     * <br>
     * 获取群组冷却状态的lambda函数
     *
     */
    private val getGroupCooldown: (Long) -> Boolean = { key -> groupCooldown.getOrDefault(key, true) }

    /**
     * The lambda function for setting user's cooldown status
     * <br>
     * 设置用户冷却状态的lambda函数
     *
     */
    private val setUserCooldown: (Long, Boolean) -> Unit = { key, value -> userCooldown[key] = value }

    /**
     * The lambda function for setting group's cooldown status
     * <br>
     * 设置群组冷却状态的lambda函数
     *
     */
    private val setGroupCooldown: (Long, Boolean) -> Unit = { key, value -> groupCooldown[key] = value }

    /**
     * Asynchronously cooldown for users
     * <br>
     * 异步冷却用户
     *
     * @param key User's id <br> 用户QQ号
     * @return [Deferred]
     */
    private suspend fun userCooldownAsync(key: Long, cooldown: Int) = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        delay((cooldown * 1000).toLong())
        setUserCooldown(key, true)
    }

    /**
     * Asynchronously cooldown for groups
     * <br>
     * 异步冷却群组
     *
     * @param key Group's id <br> 群号
     * @return [Deferred]
     */
    private suspend fun groupCooldownAsync(key: Long, cooldown: Int) = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        delay((cooldown * 1000).toLong())
        setGroupCooldown(key, true)
    }

    /**
     * Get [subject]'s cooldown status
     * <br>
     * 获取联系对象的冷却状态
     *
     * @param subject [net.mamoe.mirai.console.command.CommandSender.subject]
     * @return cooldown status <br> 是否已经冷却
     */
    fun getCooldown(@Nullable subject: Contact?): Boolean {
        return when (subject) {
            is User -> getUserCooldown(subject.id)
            is Group -> getGroupCooldown(subject.id)
            else -> true
        }
    }

    /**
     * Set [subject]'s cooldown status
     * <br>
     * 设置联系对象的冷却状态
     *
     * @param subject [net.mamoe.mirai.console.command.CommandSender.subject]
     */
    fun setCooldown(@Nullable subject: Contact?) {
        when (subject) {
            is User -> setUserCooldown(subject.id, false)
            is Group -> setGroupCooldown(subject.id, false)
        }
    }

    /**
     * Asynchronously cooldown
     * <br>
     * 异步冷却
     *
     * @param subject [net.mamoe.mirai.console.command.CommandSender.subject]
     * @param cooldown time to cooldown <br> 冷却所需时间
     */
    suspend fun cooldown(@Nullable subject: Contact?, cooldown: Int) {
        when (subject) {
            is User -> userCooldownAsync(subject.id, cooldown).await()
            is Group -> groupCooldownAsync(subject.id, cooldown).await()
        }
    }
}
