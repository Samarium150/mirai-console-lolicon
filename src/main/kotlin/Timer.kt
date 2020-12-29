package com.github.samarium150.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User

/**
 * Object for handling cooling down feature
 */
object Timer {

    /**
     * CoolDown map for users
     */
    private val userCoolDown = mutableMapOf<Long, Boolean>()

    /**
     * CoolDown map for groups
     */
    private val groupCoolDown = mutableMapOf<Long, Boolean>()

    /**
     * The lambda function for getting user's cool down status
     */
    private val getUserCoolDown: (Long) -> Boolean = { key -> userCoolDown.getOrDefault(key, true) }

    /**
     * The lambda function for getting group's cool down status
     */
    private val getGroupCoolDown: (Long) -> Boolean = { key -> groupCoolDown.getOrDefault(key, true) }

    /**
     * The lambda function for setting user's cool down status
     */
    private val setUserCoolDown: (Long, Boolean) -> Unit = { key, value -> userCoolDown[key] = value }

    /**
     * The lambda function for setting group's cool down status
     */
    private val setGroupCoolDown: (Long, Boolean) -> Unit = { key, value -> groupCoolDown[key] = value }

    /**
     * Asynchronously cool down for users
     *
     * @param key [Long] User's id
     * @return [Deferred]
     */
    private suspend fun userCoolingDownAsync(key: Long) = GlobalScope.async(start = CoroutineStart.LAZY) {
        delay(60000L)
        setUserCoolDown(key, true)
    }

    /**
     * Asynchronously cool down for groups
     *
     * @param key [Long] Group's id
     * @return [Deferred]
     */
    private suspend fun groupCoolingDownAsync(key: Long) = GlobalScope.async(start = CoroutineStart.LAZY) {
        delay(60000L)
        setGroupCoolDown(key, true)
    }

    /**
     * Get [subject]'s cool down status
     *
     * @param subject [Contact]?
     * @return [Boolean]
     */
    fun getCoolDown(subject: Contact?): Boolean {
        return when (subject) {
            is User -> getUserCoolDown(subject.id)
            is Group -> getGroupCoolDown(subject.id)
            else -> true
        }
    }

    /**
     * Set [subject]'s cool down status
     *
     * @param subject [Contact]?
     */
    fun setCoolDown(subject: Contact?) {
        when (subject) {
            is User -> setUserCoolDown(subject.id, false)
            is Group -> setGroupCoolDown(subject.id, false)
        }
    }

    /**
     * Asynchronously cool down
     *
     * @param subject [Contact]?
     */
    suspend fun coolingDown(subject: Contact?) {
        when (subject) {
            is User -> userCoolingDownAsync(subject.id).await()
            is Group -> groupCoolingDownAsync(subject.id).await()
        }
    }
}