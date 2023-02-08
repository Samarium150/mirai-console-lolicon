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
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageReceipt

enum class RecallType {
    NOTIFICATION,
    IMAGE,
    IMAGE_INFO
}

suspend fun recall(type: RecallType, receipt: MessageReceipt<Contact>, time: Int) = MiraiConsoleLolicon.launch {
    val result = receipt.recallIn((time * 1000).toLong()).awaitIsSuccess()
    if (result) logger.info("${receipt.target}${type}已撤回")
    else logger.warning("${receipt.target}${type}撤回失败")
}
