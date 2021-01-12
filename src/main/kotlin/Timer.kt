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

/**
 * Object for handling cooldown feature
 */
object Timer {

    /**
     * Cooldown map for users
     */
    private val userCooldown = mutableMapOf<Long, Boolean>()

    /**
     * Cooldown map for groups
     */
    private val groupCooldown = mutableMapOf<Long, Boolean>()

    /**
     * The lambda function for getting user's cooldown status
     */
    private val getUserCooldown: (Long) -> Boolean = { key -> userCooldown.getOrDefault(key, true) }

    /**
     * The lambda function for getting group's cooldown status
     */
    private val getGroupCooldown: (Long) -> Boolean = { key -> groupCooldown.getOrDefault(key, true) }

    /**
     * The lambda function for setting user's cooldown status
     */
    private val setUserCooldown: (Long, Boolean) -> Unit = { key, value -> userCooldown[key] = value }

    /**
     * The lambda function for setting group's cooldown status
     */
    private val setGroupCooldown: (Long, Boolean) -> Unit = { key, value -> groupCooldown[key] = value }

    /**
     * Asynchronously cooldown for users
     *
     * @param key [Long] User's id
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
     *
     * @param key [Long] Group's id
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
     *
     * @param subject [Contact]?
     * @return [Boolean]
     */
    fun getCooldown(subject: Contact?): Boolean {
        return when (subject) {
            is User -> getUserCooldown(subject.id)
            is Group -> getGroupCooldown(subject.id)
            else -> true
        }
    }

    /**
     * Set [subject]'s cooldown status
     *
     * @param subject [Contact]?
     */
    fun setCooldown(subject: Contact?) {
        when (subject) {
            is User -> setUserCooldown(subject.id, false)
            is Group -> setGroupCooldown(subject.id, false)
        }
    }

    /**
     * Asynchronously cooldown
     *
     * @param subject [Contact]?
     */
    suspend fun cooldown(subject: Contact?, cooldown: Int) {
        when (subject) {
            is User -> userCooldownAsync(subject.id, cooldown).await()
            is Group -> groupCooldownAsync(subject.id, cooldown).await()
        }
    }
}